package rememberit.translation.types.service;

import rememberit.translation.types.common.Language;

public class TranslateTranslationOptions {
    public String text;
    public Language sourceLanguage;
    public Language targetLanguage;

    public TranslateTranslationOptions(Builder builder) {
        this.text = builder.text;
        this.sourceLanguage = builder.sourceLanguage;
        this.targetLanguage = builder.targetLanguage;
    }

    public static class Builder {
        private String text;
        private Language sourceLanguage;
        private Language targetLanguage;

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder sourceLanguage(Language sourceLanguage) {
            this.sourceLanguage = sourceLanguage;
            return this;
        }

        public Builder targetLanguage(Language targetLanguage) {
            this.targetLanguage = targetLanguage;
            return this;
        }

        public TranslateTranslationOptions build() {
            return new TranslateTranslationOptions(this);
        }
    }
}
