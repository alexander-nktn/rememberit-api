package rememberit.card.types.service;

public class UpdateCardOptions {
    public String id;
    public String userId;
    public String translationId;
    public String imageUrl;
    public String backgroundColor;
    public String textColor;
    public String translatedTextColor;

    public UpdateCardOptions(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.translationId = builder.translationId;
        this.imageUrl = builder.imageUrl;
        this.backgroundColor = builder.backgroundColor;
        this.textColor = builder.textColor;
        this.translatedTextColor = builder.translatedTextColor;
    }

    public static class Builder {
        private final String id;
        private String userId;
        private String translationId;
        private String imageUrl;
        private String backgroundColor;
        private String textColor;
        private String translatedTextColor;

        public Builder(String id) {
            this.id = id;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder translationId(String translationId) {
            this.translationId = translationId;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder backgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder textColor(String textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder translatedTextColor(String translatedTextColor) {
            this.translatedTextColor = translatedTextColor;
            return this;
        }

        public UpdateCardOptions build() {
            return new UpdateCardOptions(this);
        }
    }
}
