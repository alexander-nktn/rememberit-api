package rememberit.translation.types.service;

public class TranslateTranslationOptions {
    public String text;
    public String sourceLanguage;
    public String targetLanguage;

    public TranslateTranslationOptions(Builder builder) {
        this.text = text;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }

    public static class Builder {
        private String text;
        private String sourceLanguage;
        private String targetLanguage;

        public Builder text(String text) {
            this.text = text;
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

        public TranslateTranslationOptions build() {
            return new TranslateTranslationOptions(this);
        }
    }
}
