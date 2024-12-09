package rememberit.translation;
import jakarta.persistence.EntityNotFoundException;
import rememberit.config.ServiceMethodContext;
import com.google.cloud.translate.Translate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.google.cloud.translate.TranslateOptions;
import rememberit.translation.types.service.CreateTranslationOptions;
import rememberit.translation.types.service.TranslateTranslationOptions;
import rememberit.translation.types.service.UpdateTranslationOptions;

import java.util.Optional;


@Service
public class TranslationService {
    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);
    private final Translate translate;
    private final TranslationRepository translationRepository;

    public TranslationService(
            TranslationRepository translationRepository
    ) {
        this.translate = TranslateOptions.getDefaultInstance().getService();
        this.translationRepository = translationRepository;
    }


    public Optional<Translation> getOne(String id) {
        return translationRepository.findById(id);
    }

    public Translation getOneOrFail(String id, ServiceMethodContext ctx) {
        ctx.addProperty("id", id);
        Optional<Translation> translation  = this.getOne(id);

        if (translation.isEmpty()) {
            throw new EntityNotFoundException(String.format("Card with id: %s not found", id));
        }

        return translation.get();
    }

    public Translation create(CreateTranslationOptions opts, ServiceMethodContext ctx) {
        Translation card = new Translation(
                opts.text,
                opts.translatedText,
                opts.sourceLanguage,
                opts.targetLanguage
        );

        try {
            return translationRepository.save(card);
        } catch (Exception error) {
            throw new RuntimeException("Failed to create translation", error);
        }
    }

    public Translation update(UpdateTranslationOptions opts, ServiceMethodContext ctx) {
        Translation translation = this.getOneOrFail(opts.id, ctx);
        translation.setText(opts.text);
        translation.setTranslatedText(opts.translatedText);

        try {
            return translationRepository.save(translation);
        } catch (Exception error) {
            throw new RuntimeException("Failed to update translation", error);
        }
    }

    private String translate(TranslateTranslationOptions opts, ServiceMethodContext ctx) {
        try {
            com.google.cloud.translate.Translation translation = translate.translate(
                    opts.text,
                    Translate.TranslateOption.targetLanguage(opts.targetLanguage.getCode()),
                    Translate.TranslateOption.sourceLanguage(opts.sourceLanguage.getCode()),
                    Translate.TranslateOption.model("base"));
            return translation.getTranslatedText();
        } catch (Exception error) {
            throw new RuntimeException("Failed to translate word", error);
        }
    }

    public Translation translateAndSave(TranslateTranslationOptions opts, ServiceMethodContext ctx) {
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
