package rememberit.card.types.resolver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCardInput {
    public final String id;
    public String imageUrl;
    public String backgroundColor;
    public String textColor;
    public String translatedTextColor;
    public UpdateTranslationInput translation;

    public UpdateCardInput(String id) {
        this.id = id;
    }

    @Getter
    @Setter
    public static class UpdateTranslationInput {
        public final String id;
        public String text;
        public String translatedText;

        public UpdateTranslationInput(String id) {
            this.id = id;
        }
    }
}