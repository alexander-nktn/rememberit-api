package rememberit.translation.types.service;

public class UpdateTranslationOptions {
    public String id;
    public String text;
    public String translatedText;

    public UpdateTranslationOptions(Builder builder) {
        this.id = builder.id;
        this.text = builder.text;
        this.translatedText = builder.translatedText;
    }

    public static class Builder {
        private final String id;
        private String text;
        private String translatedText;

        public Builder(String id) {
            this.id = id;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder translatedText(String translatedText) {
            this.translatedText = translatedText;
            return this;
        }

        public UpdateTranslationOptions build() {
            return new UpdateTranslationOptions(this);
        }
    }
}
