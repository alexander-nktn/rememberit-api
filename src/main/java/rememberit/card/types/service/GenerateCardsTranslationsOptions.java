package rememberit.card.types.service;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GenerateCardsTranslationsOptions {
    public String text;

    public String translatedText;
}