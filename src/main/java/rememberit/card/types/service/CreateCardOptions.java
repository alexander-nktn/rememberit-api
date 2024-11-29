package rememberit.card.types.service;

import rememberit.translation.Translation;
import rememberit.user.User;

public class CreateCardOptions {
    public User user;
    public Translation translation;
    public String imageUrl;
    public String backgroundColor;
    public String textColor;
    public String translatedTextColor;

    public CreateCardOptions(Builder builder) {
        this.user = user;
        this.translation = translation;
        this.imageUrl = imageUrl;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.translatedTextColor = translatedTextColor;
    }

    public static class Builder {
        private User user;
        private Translation translation;
        private String imageUrl;
        private String backgroundColor;
        private String textColor;
        private String translatedTextColor;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder translation(Translation translation) {
            this.translation = translation;
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

        public CreateCardOptions build() {
            return new CreateCardOptions(this);
        }
    }
}
