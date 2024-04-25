package com.boost.memory.types.translation;

public class TranslationCreateOptions {
    public String mainWord;

    public String translatedWord;

    public String language;

    public TranslationCreateOptions(
            String mainWord,
            String translatedWord,
            String language
    ) {
        this.mainWord = mainWord;
        this.translatedWord = translatedWord;
        this.language = language;
    }
}
