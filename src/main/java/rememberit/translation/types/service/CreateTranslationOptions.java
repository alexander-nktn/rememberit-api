package rememberit.translation.types.service;

import rememberit.translation.types.common.Language;

public class CreateTranslationOptions {
    public String text;
    public String translatedText;
    public Language sourceLanguage;
    public Language targetLanguage;

    public CreateTranslationOptions(Builder builder) {
        this.text = builder.text;
        this.translatedText = builder.translatedText;
        this.sourceLanguage = builder.sourceLanguage;
        this.targetLanguage = builder.targetLanguage;
    }

    public static class Builder {
        private String text;
        private String translatedText;
        private Language sourceLanguage;
        private Language targetLanguage;

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder translatedText(String translatedText) {
            this.translatedText = translatedText;
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

        public CreateTranslationOptions build() {
            return new CreateTranslationOptions(this);
        }
    }
}
