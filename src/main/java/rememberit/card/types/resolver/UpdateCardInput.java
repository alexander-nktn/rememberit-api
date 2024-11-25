package rememberit.card.types.resolver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCardInput {
    public String id;
    public String imageUrl;
    public String backgroundColor;
    public String textColor;
    public String translatedTextColor;
    public UpdateTranslationInput translation;

    @Getter
    @Setter
    public static class UpdateTranslationInput {
        public String id;
        public String text;
        public String translatedText;
    }
}