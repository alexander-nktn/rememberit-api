package rememberit.card.types.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import rememberit.translation.types.common.Language;

import java.util.List;

@Builder
@Getter
@Setter
public class GenerateCardsOptions {
    public List<GenerateCardsTranslationsOptions> translations;
    public String spreadsheetUrl;
    public String backgroundColor;
    public String textColor;
    public String translatedTextColor;
    public Language sourceLanguage;
    public Language targetLanguage;
}

