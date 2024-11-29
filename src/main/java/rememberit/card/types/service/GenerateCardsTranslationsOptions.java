package rememberit.card.types.service;

public class GenerateCardsTranslationsOptions {
        public String text;
        public String translatedText;

        public GenerateCardsTranslationsOptions(Builder builder) {
            this.text = builder.text;
            this.translatedText = builder.translatedText;
        }

        public static class Builder {
            private String text;
            private String translatedText;

            public Builder text(String text) {
                this.text = text;
                return this;
            }

            public Builder translatedText(String translatedText) {
                this.translatedText = translatedText;
                return this;
            }

            public GenerateCardsTranslationsOptions build() {
                return new GenerateCardsTranslationsOptions(this);
            }
        }
    }