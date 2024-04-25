package com.boost.memory.services;

import com.boost.memory.exception.ServiceMethodContext;
import com.boost.memory.models.Translation;
import com.boost.memory.repositories.TranslationRepository;
import com.boost.memory.types.translation.TranslationCreateOptions;
import com.boost.memory.types.translation.TranslationTranslateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {
    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

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
        String mainWord = opts.Word;
        String language = opts.Language;

        // TODO: Implement translation logic
        return "Translated word";
    }

    public Translation translateAndSave(TranslationTranslateOptions opts, ServiceMethodContext ctx) {
        String translatedWord = this.translate(opts, ctx);
        TranslationCreateOptions translationCreateOptions = new TranslationCreateOptions(translatedWord, opts.Word, opts.Language);

        return this.create(translationCreateOptions, ctx);
    }
}
