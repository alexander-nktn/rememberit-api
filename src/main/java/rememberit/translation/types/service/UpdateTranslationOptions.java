package rememberit.translation.types.service;

import lombok.Builder;

@Builder
public class UpdateTranslationOptions {
    public String id;
    public String text;
    public String translatedText;

    public UpdateTranslationOptions(
        String id,
        String text,
        String translatedText
    ) {
        this.id = id;
        this.text = text;
        this.translatedText = translatedText;
    }
}
