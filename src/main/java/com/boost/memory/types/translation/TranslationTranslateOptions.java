package com.boost.memory.types.translation;

public class TranslationTranslateOptions {
    public String text;
    public String sourceLanguage;
    public String targetLanguage;

    public TranslationTranslateOptions(
            String text,
            String sourceLanguage,
            String targetLanguage
    ) {
        this.text = text;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }
}
