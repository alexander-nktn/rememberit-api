package rememberit.card;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import rememberit.card.types.service.GenerateCardsOptions;
import rememberit.card.types.service.GenerateCardsTranslationsOptions;
import rememberit.card.types.service.UpdateCardOptions;
import rememberit.config.ServiceMethodContext;
import rememberit.exception.ApplicationException;
import rememberit.image.ImageService;
import rememberit.textCollector.TextCollectorService;
import rememberit.translation.Translation;
import rememberit.translation.TranslationService;
import rememberit.translation.types.service.CreateTranslationOptions;
import rememberit.translation.types.service.TranslateTranslationOptions;
import rememberit.translation.types.common.Language;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TranslationService translationService;

    @Mock
    private ImageService imageService;

    @Mock
    private TextCollectorService textCollectorService;

    @InjectMocks
    private CardService cardService;

    private ServiceMethodContext ctx;

    @BeforeEach
    void setUp() {
        ctx = new ServiceMethodContext();
    }

    @Test
    void getOne_ReturnsCardIfExists() {
        String cardId = "c1";
        Card mockCard = new Card();
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));

        Optional<Card> result = cardService.getOne(cardId);

        assertTrue(result.isPresent());
        assertEquals(mockCard, result.get());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getOne_ReturnsEmptyIfNotExists() {
        String cardId = "c1";
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        Optional<Card> result = cardService.getOne(cardId);

        assertFalse(result.isPresent());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getOneOrFail_ReturnsCard_IfFound() {
        String cardId = "c1";
        Card mockCard = new Card();
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));

        Card result = cardService.getOneOrFail(cardId, ctx);

        assertNotNull(result);
        assertEquals(mockCard, result);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getOneOrFail_ThrowsException_IfNotFound() {
        String cardId = "c1";
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> cardService.getOneOrFail(cardId, ctx));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getMany_ReturnsSortedList() {
        Card card1 = new Card();
        card1.setCreatedAt(new Date(100L)); // earlier date
        Card card2 = new Card();
        card2.setCreatedAt(new Date(200L)); // later date

        when(cardRepository.findAll()).thenReturn(Arrays.asList(card1, card2));

        List<Card> cards = cardService.getMany();

        // Should be sorted by createdAt descending => card2 first, then card1
        assertEquals(2, cards.size());
        assertEquals(card2, cards.get(0));
        assertEquals(card1, cards.get(1));
        verify(cardRepository).findAll();
    }

    @Test
    void update_UpdatesAndReturnsUpdatedCard() {
        UpdateCardOptions opts = UpdateCardOptions.builder()
                .id("c1")
                .backgroundColor("blue")
                .textColor("white")
                .translatedTextColor("green")
                .imageUrl("http://example.com/image.jpg")
                .build();

        Card existingCard = new Card();
        existingCard.setId("c1");

        when(cardRepository.findById("c1")).thenReturn(Optional.of(existingCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card savedCard = invocation.getArgument(0);
            savedCard.setId("c1");
            return savedCard;
        });

        Card updatedCard = cardService.update(opts, ctx);

        assertNotNull(updatedCard);
        assertEquals("c1", updatedCard.getId());
        assertEquals("blue", updatedCard.getBackgroundColor());
        assertEquals("white", updatedCard.getTextColor());
        assertEquals("green", updatedCard.getTranslatedTextColor());
        assertEquals("http://example.com/image.jpg", updatedCard.getImageUrl());

        verify(cardRepository).findById("c1");
        verify(cardRepository).save(existingCard);
    }

    @Test
    void update_ThrowsException_IfCardNotFound() {
        UpdateCardOptions opts = UpdateCardOptions.builder()
                .id("not-found")
                .build();

        when(cardRepository.findById("not-found")).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> cardService.update(opts, ctx));
        verify(cardRepository).findById("not-found");
        verify(cardRepository, never()).save(any());
    }

    @Test
    void delete_DeletesCard_IfExists() {
        Card existingCard = new Card();
        existingCard.setId("c1");
        when(cardRepository.findById("c1")).thenReturn(Optional.of(existingCard));

        cardService.delete("c1", ctx);

        verify(cardRepository).delete(existingCard);
    }

    @Test
    void delete_ThrowsException_IfCardNotFound() {
        when(cardRepository.findById("c1")).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> cardService.delete("c1", ctx));
        verify(cardRepository, never()).delete(any());
    }

    // --------------------------------------------------------------------------------------
    // generate(GenerateCardsOptions opts, ServiceMethodContext ctx)
    // (indirectly covers private create(CreateCardOptions, ServiceMethodContext))
    // --------------------------------------------------------------------------------------
    @Test
    void generate_CreatesCardsFromProvidedTranslations() {
        GenerateCardsOptions opts = GenerateCardsOptions.builder()
                .sourceLanguage(Language.ENGLISH)
                .targetLanguage(Language.SPANISH)
                .translations(new ArrayList<>())
                .build();

        // Example translations
        GenerateCardsTranslationsOptions gto1 = GenerateCardsTranslationsOptions.builder()
                .text("Hello")
                .translatedText("Hola")
                .build();

        // This one has no translatedText => triggers translationService.translateAndSave
        GenerateCardsTranslationsOptions gto2 = GenerateCardsTranslationsOptions.builder()
                .text("Bye")
                .build();

        opts.getTranslations().add(gto1);
        opts.getTranslations().add(gto2);

        // Mock translations
        Translation mockTranslation1 = new Translation();
        mockTranslation1.setId("t1");
        when(translationService.create(any(CreateTranslationOptions.class), eq(ctx)))
                .thenReturn(mockTranslation1);

        Translation mockTranslation2 = new Translation();
        mockTranslation2.setId("t2");
        when(translationService.translateAndSave(any(TranslateTranslationOptions.class), eq(ctx)))
                .thenReturn(mockTranslation2);

        // Mock saving a Card
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card savedCard = invocation.getArgument(0);
            // Fake an ID
            savedCard.setId("card_" + savedCard.getTranslation().getId());
            return savedCard;
        });

        List<Card> result = cardService.generate(opts, ctx);

        assertEquals(2, result.size());
        assertEquals("card_t1", result.get(0).getId());
        assertEquals("card_t2", result.get(1).getId());

        // Verify correct flows
        verify(translationService).create(any(CreateTranslationOptions.class), eq(ctx));
        verify(translationService).translateAndSave(any(TranslateTranslationOptions.class), eq(ctx));
        verify(cardRepository, times(2)).save(any(Card.class));
    }
}