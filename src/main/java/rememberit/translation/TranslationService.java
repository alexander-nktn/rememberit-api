package rememberit.translation;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import org.springframework.stereotype.Service;
import rememberit.config.ServiceMethodContext;
import rememberit.exception.ApplicationException;
import rememberit.exception.ErrorCode;
import rememberit.translation.types.service.CreateTranslationOptions;
import rememberit.translation.types.service.TranslateTranslationOptions;
import rememberit.translation.types.service.UpdateTranslationOptions;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TranslationService {
    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);
    private final Translate translate;
    private final TranslationRepository translationRepository;

    public TranslationService(TranslationRepository translationRepository) {
        this.translate = TranslateOptions.getDefaultInstance().getService();
        this.translationRepository = translationRepository;
    }

    public Optional<Translation> getOne(String id) {
        logger.debug("Fetching translation with ID: {}", id);
        return translationRepository.findById(id);
    }

    public Translation getOneOrFail(String id, ServiceMethodContext ctx) {
        ctx.addProperty("translationId", id);
        logger.info("Attempting to fetch translation with ID: {}", id);
        return translationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Translation with ID {} not found", id);
                    return new ApplicationException(
                            String.format("Translation with id %s not found", id),
                            ErrorCode.TRANSLATION_NOT_FOUND,
                            ctx
                    );
                });
    }

    public Translation create(CreateTranslationOptions opts, ServiceMethodContext ctx) {
        ctx.addProperty("createTranslationOptions", opts);
        logger.info("Creating a new translation: text={}, targetLanguage={}", opts.text, opts.targetLanguage);

        Translation translation = new Translation(
                opts.text,
                opts.translatedText,
                opts.sourceLanguage,
                opts.targetLanguage
        );

        try {
            Translation savedTranslation = translationRepository.save(translation);
            logger.info("Successfully created translation with ID: {}", savedTranslation.getId());
            return savedTranslation;
        } catch (Exception ex) {
            logger.error("Failed to create translation: {}", ex.getMessage(), ex);
            throw new ApplicationException(
                    "Failed to create translation",
                    ErrorCode.TRANSLATION_FAILED_TO_CREATE,
                    ctx,
                    ex
            );
        }
    }

    public Translation update(UpdateTranslationOptions opts, ServiceMethodContext ctx) {
        ctx.addProperty("updateTranslationOptions", opts);
        logger.info("Updating translation with ID: {}", opts.id);

        Translation translation = this.getOneOrFail(opts.id, ctx);
        translation.setText(opts.text);
        translation.setTranslatedText(opts.translatedText);

        try {
            Translation updatedTranslation = translationRepository.save(translation);
            logger.info("Successfully updated translation with ID: {}", updatedTranslation.getId());
            return updatedTranslation;
        } catch (Exception ex) {
            logger.error("Failed to update translation: {}", ex.getMessage(), ex);
            throw new ApplicationException(
                    "Failed to update translation",
                    ErrorCode.TRANSLATION_FAILED_TO_UPDATE,
                    ctx,
                    ex
            );
        }
    }

    private String translate(TranslateTranslationOptions opts, ServiceMethodContext ctx) {
        ctx.addProperty("translateOptions", opts);
        logger.info("Translating text: {}, targetLanguage={}", opts.text, opts.targetLanguage);

        if (opts.text == null || opts.text.trim().isEmpty()) {
            logger.warn("Translation text is invalid: {}", opts.text);
            throw new ApplicationException(
                    "Text to translate is invalid",
                    ErrorCode.TRANSLATION_TEXT_INVALID,
                    ctx
            );
        }

        try {
            com.google.cloud.translate.Translation translation = translate.translate(
                    opts.text,
                    Translate.TranslateOption.targetLanguage(opts.targetLanguage.getCode()),
                    Translate.TranslateOption.sourceLanguage(opts.sourceLanguage.getCode()),
                    Translate.TranslateOption.model("base")
            );
            logger.debug("Successfully translated text to {}: {}", opts.targetLanguage, translation.getTranslatedText());
            return translation.getTranslatedText();
        } catch (Exception ex) {
            logger.error("Failed to process translation: {}", ex.getMessage(), ex);
            throw new ApplicationException(
                    "Failed to process translation",
                    ErrorCode.TRANSLATION_FAILED_TO_PROCESS,
                    ctx,
                    ex
            );
        }
    }

    public Translation translateAndSave(TranslateTranslationOptions opts, ServiceMethodContext ctx) {
        ctx.addProperty("translateAndSaveOptions", opts);
        logger.info("Translating and saving text: {}", opts.text);

        String translatedText = this.translate(opts, ctx);

        CreateTranslationOptions translationCreateOptions = new CreateTranslationOptions.Builder()
                .text(opts.text)
                .translatedText(translatedText)
                .sourceLanguage(opts.sourceLanguage)
                .targetLanguage(opts.targetLanguage)
                .build();

        return this.create(translationCreateOptions, ctx);
    }
}