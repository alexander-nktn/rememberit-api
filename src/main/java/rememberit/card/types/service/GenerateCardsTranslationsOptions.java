package rememberit.card.types.service;

import lombok.Builder;

@Builder
public class GenerateCardsTranslationsOptions {
    public String text;

    public String translatedText;
}