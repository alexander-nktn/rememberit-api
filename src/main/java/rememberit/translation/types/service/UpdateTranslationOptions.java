package rememberit.translation.types.service;

import lombok.Builder;

@Builder
public class UpdateTranslationOptions {
    public String id;
    public String text;
    public String translatedText;
}
