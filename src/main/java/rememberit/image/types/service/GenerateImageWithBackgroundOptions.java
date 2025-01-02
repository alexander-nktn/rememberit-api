package rememberit.image.types.service;

import lombok.Builder;

@Builder
public class GenerateImageWithBackgroundOptions {
    public String text;
    public String translatedText;
    public String backgroundColor;
    public String textColor;
    public String translatedTextColor;
}
