package rememberit.card;

import rememberit.card.types.service.GenerateCardsTranslationsOptions;
import rememberit.config.ServiceMethodContext;
import rememberit.exception.ApplicationException;
import rememberit.exception.ErrorCode;
import rememberit.translation.Translation;
import rememberit.image.ImageService;
import rememberit.translation.TranslationService;
import rememberit.textCollector.TextCollectorService;
import rememberit.card.types.service.CreateCardOptions;
import rememberit.card.types.service.GenerateCardsOptions;
import rememberit.card.types.service.UpdateCardOptions;
import rememberit.translation.types.service.CreateTranslationOptions;
import rememberit.translation.types.service.TranslateTranslationOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    private final TranslationService translationService;
    private final ImageService imageService;
    private final TextCollectorService wordCollectionService;
    private final CardRepository cardRepository;

    public CardService(
            TranslationService translationService,
            ImageService imageService,
            TextCollectorService wordCollectionService,
            CardRepository cardRepository
    ) {
        this.translationService = translationService;
        this.imageService = imageService;
        this.wordCollectionService = wordCollectionService;
        this.cardRepository = cardRepository;
    }

    public Optional<Card> getOne(String id) {
        logger.debug("Fetching card with ID: {}", id);
        return cardRepository.findById(id);
    }

    public Card getOneOrFail(String id, ServiceMethodContext ctx) {
        ctx.addProperty("cardId", id);
        logger.info("Attempting to fetch card with ID: {}", id);
        return cardRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Card with ID {} not found", id);
                    return new ApplicationException(
                            String.format("Card with ID %s not found", id),
                            ErrorCode.CARD_NOT_FOUND,
                            ctx
                    );
                });
    }

    public List<Card> getMany() {
        logger.info("Fetching all cards...");
        List<Card> cards = cardRepository.findAll();
        cards.sort(Comparator.comparing(Card::getCreatedAt).reversed());
        logger.debug("Fetched {} cards", cards.size());
        return cards;
    }

    private Card create(CreateCardOptions opts, ServiceMethodContext ctx) {
        ctx.addProperty("createCardOptions", opts);
        logger.info("Creating a new card with options: {}", opts);

        Card card = Card.builder()
                .imageUrl(opts.imageUrl)
                .backgroundColor(opts.backgroundColor)
                .textColor(opts.textColor)
                .translatedTextColor(opts.translatedTextColor)
                .translation(opts.translation)
                .user(opts.user)
                .build();

        try {
            Card savedCard = cardRepository.save(card);
            logger.info("Successfully created card with ID: {}", savedCard.getId());
            return savedCard;
        } catch (Exception ex) {
            logger.error("Failed to create card: {}", ex.getMessage(), ex);
            throw new ApplicationException(
                    "Failed to create card",
                    ErrorCode.CARD_FAILED_TO_CREATE,
                    ctx,
                    ex
            );
        }
    }

    public Card update(UpdateCardOptions opts, ServiceMethodContext ctx) {
        ctx.addProperty("updateCardOptions", opts);
        logger.info("Updating card with ID: {}", opts.id);

        Card card = getOneOrFail(opts.id, ctx);

        card.setBackgroundColor(opts.backgroundColor);
        card.setTextColor(opts.textColor);
        card.setTranslatedTextColor(opts.translatedTextColor);
        card.setImageUrl(opts.imageUrl);

        try {
            Card updatedCard = cardRepository.save(card);
            logger.info("Successfully updated card with ID: {}", updatedCard.getId());
            return updatedCard;
        } catch (Exception ex) {
            logger.error("Failed to update card: {}", ex.getMessage(), ex);
            throw new ApplicationException(
                    "Failed to update card",
                    ErrorCode.CARD_FAILED_TO_UPDATE,
                    ctx,
                    ex
            );
        }
    }

    public void delete(String id, ServiceMethodContext ctx) {
        ctx.addProperty("deleteCardId", id);
        logger.info("Deleting card with ID: {}", id);

        Card card = getOneOrFail(id, ctx);

        try {
            cardRepository.delete(card);
            logger.info("Successfully deleted card with ID: {}", id);
        } catch (Exception ex) {
            logger.error("Failed to delete card with ID {}: {}", id, ex.getMessage(), ex);
            throw new ApplicationException(
                    "Failed to delete card",
                    ErrorCode.CARD_FAILED_TO_DELETE,
                    ctx,
                    ex
            );
        }
    }

    public List<Card> generate(GenerateCardsOptions opts, ServiceMethodContext ctx) {
        ctx.addProperty("generateCardsOptions", opts);
        logger.info("Generating cards with options: {}", opts);

        List<Card> cards = new ArrayList<>();

        if (opts.spreadsheetUrl != null && !opts.spreadsheetUrl.isEmpty()) {
            String spreadsheetId = getIdFromUrl(opts.spreadsheetUrl);
            ctx.addProperty("spreadsheetId", spreadsheetId);
            logger.info("Using spreadsheet with ID: {}", spreadsheetId);

            try {
                List<List<Object>> values = wordCollectionService.getSpreadsheetValues(spreadsheetId, "'Saved translations'!A:B", ctx);
                logger.debug("Retrieved {} rows from spreadsheet", values.size());

                List<GenerateCardsTranslationsOptions> translations = new ArrayList<>();
                for (List<Object> row : values) {
                    if (row.isEmpty()) continue;

                    GenerateCardsTranslationsOptions options = row.size() == 1
                            ? GenerateCardsTranslationsOptions.builder().text(row.get(0).toString()).build()
                            : GenerateCardsTranslationsOptions.builder().text(row.get(0).toString()).translatedText(row.get(1).toString()).build();

                    translations.add(options);
                }
                opts.setTranslations(translations);
            } catch (Exception ex) {
                logger.error("Failed to retrieve spreadsheet values: {}", ex.getMessage(), ex);
                throw new ApplicationException(
                        "Failed to retrieve spreadsheet values",
                        ErrorCode.GOOGLE_SPREADSHEET_FAILED_TO_RETRIEVE,
                        ctx,
                        ex
                );
            }
        }

        try {
            for (GenerateCardsTranslationsOptions translationOption : opts.translations) {
                Translation translation;

                if (translationOption.getTranslatedText() != null && !translationOption.getTranslatedText().isEmpty()) {
                    translation = translationService.create(
                            CreateTranslationOptions.builder()
                                    .text(translationOption.getText())
                                    .translatedText(translationOption.getTranslatedText())
                                    .sourceLanguage(opts.sourceLanguage)
                                    .targetLanguage(opts.targetLanguage)
                                    .build(),
                            ctx
                    );
                } else {
                    translation = translationService.translateAndSave(
                            TranslateTranslationOptions.builder()
                                    .text(translationOption.getText())
                                    .sourceLanguage(opts.sourceLanguage)
                                    .targetLanguage(opts.targetLanguage)
                                    .build(),
                            ctx
                    );
                }

                if (translation == null) continue;

                cards.add(create(
                        CreateCardOptions.builder()
                                .translation(translation)
                                .imageUrl("no image")
                                .backgroundColor(opts.backgroundColor)
                                .textColor(opts.textColor)
                                .translatedTextColor(opts.translatedTextColor)
                                .user(ctx.getUser())
                                .build(),
                        ctx
                ));
            }

            logger.info("Successfully generated {} cards", cards.size());
            return cards;
        } catch (Exception ex) {
            logger.error("Failed to generate cards: {}", ex.getMessage(), ex);
            throw new ApplicationException(
                    "Failed to generate cards",
                    ErrorCode.CARD_FAILED_TO_GENERATE,
                    ctx,
                    ex
            );
        }
    }

    private String getIdFromUrl(String url) {
        logger.debug("Extracting ID from URL: {}", url);
        String[] parts = url.split("/");
        String id = parts[parts.length - 2];

        if (id.isEmpty()) {
            logger.warn("Invalid spreadsheet URL: {}", url);
            throw new ApplicationException(
                    "Invalid spreadsheet URL: ID not found",
                    ErrorCode.GOOGLE_SPREADSHEET_INVALID_URL,
                    null
            );
        }

        return id;
    }
}