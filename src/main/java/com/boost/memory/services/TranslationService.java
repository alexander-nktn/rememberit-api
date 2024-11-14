package com.boost.memory.services;
import com.boost.memory.exception.ServiceMethodContext;
import com.boost.memory.models.Translation;
import com.boost.memory.repositories.TranslationRepository;
import com.boost.memory.types.translation.TranslationCreateOptions;
import com.boost.memory.types.translation.TranslationTranslateOptions;
import com.google.cloud.translate.Translate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.translate.TranslateOptions;


@Service
public class TranslationService {
    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    private final Translate translate;

    public TranslationService() {
        this.translate = TranslateOptions.getDefaultInstance().getService();
    }

    @Autowired
    private TranslationRepository translationRepository;

    private Translation create(TranslationCreateOptions opts, ServiceMethodContext ctx) {
        Translation card = new Translation(opts);

        try {
            return translationRepository.save(card);
        } catch (Exception error) {
            throw new RuntimeException("Failed to create translation", error);
        }
    }

    private String translate(TranslationTranslateOptions opts, ServiceMethodContext ctx) {
        try {
            com.google.cloud.translate.Translation translation = translate.translate(
                    opts.text,
                    Translate.TranslateOption.targetLanguage(opts.targetLanguage),
                    Translate.TranslateOption.sourceLanguage(opts.sourceLanguage),
                    Translate.TranslateOption.model("base"));
            return translation.getTranslatedText();
        } catch (Exception error) {
            throw new RuntimeException("Failed to translate word", error);
        }
    }

    public Translation translateAndSave(TranslationTranslateOptions opts, ServiceMethodContext ctx) {
        String translatedText = this.translate(opts, ctx);
        TranslationCreateOptions translationCreateOptions = new TranslationCreateOptions(
                opts.text,
                translatedText,
                opts.sourceLanguage,
                opts.targetLanguage
        );

        return this.create(translationCreateOptions, ctx);
    }
}
