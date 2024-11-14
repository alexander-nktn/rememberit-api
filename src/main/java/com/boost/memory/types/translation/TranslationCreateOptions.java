package com.boost.memory.types.translation;

public class TranslationCreateOptions {
    public String text;

    public String translatedText;

    public String sourceLanguage;

    public String targetLanguage;

    public TranslationCreateOptions(
            String text,
            String translatedText,
            String sourceLanguage,
            String targetLanguage
    ) {
        this.text = text;
        this.translatedText = translatedText;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }
}
