package rememberit.translation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rememberit.config.ServiceMethodContext;
import rememberit.exception.ApplicationException;
import rememberit.translation.types.common.Language;
import rememberit.translation.types.service.CreateTranslationOptions;
import rememberit.translation.types.service.UpdateTranslationOptions;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

    @Mock
    private TranslationRepository translationRepository;

    @InjectMocks
    private TranslationService translationService;


    private ServiceMethodContext ctx;

    @BeforeEach
    void setUp() {
        ctx = new ServiceMethodContext();
    }

    @Test
    void getOneOrFail_ReturnsTranslation_IfFound() {
        String translationId = "t1";
        Translation mockTranslation = new Translation();
        when(translationRepository.findById(translationId)).thenReturn(Optional.of(mockTranslation));

        Translation result = translationService.getOneOrFail(translationId, ctx);

        assertNotNull(result);
        assertEquals(mockTranslation, result);
        verify(translationRepository).findById(translationId);
    }

    @Test
    void getOneOrFail_ThrowsException_IfNotFound() {
        String translationId = "t1";
        when(translationRepository.findById(translationId)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> translationService.getOneOrFail(translationId, ctx));
        verify(translationRepository).findById(translationId);
    }

    @Test
    void create() {
        CreateTranslationOptions options = CreateTranslationOptions.builder()
                .sourceLanguage(Language.ENGLISH)
                .targetLanguage(Language.SPANISH)
                .text("Hello")
                .translatedText("Hola")
                .build();

        when(translationRepository.save(any(Translation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Translation result = translationService.create(options, ctx);

        assertNotNull(result);
        assertEquals(options.text, result.getText());
        assertEquals(options.translatedText, result.getTranslatedText());
        assertEquals(options.sourceLanguage, result.getSourceLanguage());
        assertEquals(options.targetLanguage, result.getTargetLanguage());
    }

    @Test
    void update() {
        UpdateTranslationOptions options = UpdateTranslationOptions.builder()
                .id("t1")
                .text("Hello")
                .translatedText("Hola")
                .build();

        Translation mockTranslation = new Translation();
        when(translationRepository.findById(options.id)).thenReturn(Optional.of(mockTranslation));
        when(translationRepository.save(any(Translation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Translation result = translationService.update(options, ctx);

        assertNotNull(result);
        assertEquals(options.text, result.getText());
        assertEquals(options.translatedText, result.getTranslatedText());
        assertEquals(mockTranslation.getSourceLanguage(), result.getSourceLanguage());
        assertEquals(mockTranslation.getTargetLanguage(), result.getTargetLanguage());
    }
}