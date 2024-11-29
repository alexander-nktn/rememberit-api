package rememberit.translation.types.service;

public class CreateTranslationOptions {
    public String text;
    public String translatedText;
    public String sourceLanguage;
    public String targetLanguage;

    public CreateTranslationOptions(Builder builder) {
        this.text = text;
        this.translatedText = translatedText;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }

    public static class Builder {
        private String text;
        private String translatedText;
        private String sourceLanguage;
        private String targetLanguage;

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder translatedText(String translatedText) {
            this.translatedText = translatedText;
            return this;
        }

        public Builder sourceLanguage(String sourceLanguage) {
            this.sourceLanguage = sourceLanguage;
            return this;
        }

        public Builder targetLanguage(String targetLanguage) {
            this.targetLanguage = targetLanguage;
            return this;
        }

        public CreateTranslationOptions build() {
            return new CreateTranslationOptions(this);
        }
    }
}
