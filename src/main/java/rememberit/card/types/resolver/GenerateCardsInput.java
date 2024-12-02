package rememberit.card.types.resolver;
import lombok.Getter;
import lombok.Setter;
import rememberit.translation.types.common.Language;

import java.util.List;

@Getter
@Setter
public class GenerateCardsInput {
    public List<GenerateCardsTranslationsInput> translations;
    public String spreadsheetUrl;
    public Language sourceLanguage;
    public Language  targetLanguage;
    public String backgroundColor;
    public String textColor;
    public String translatedTextColor;
}
