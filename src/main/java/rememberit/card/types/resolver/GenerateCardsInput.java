package rememberit.card.types.resolver;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class GenerateCardsInput {
    public List<GenerateCardsTranslationsInput> translations;
    public String spreadsheetUrl;
    public String sourceLanguage;
    public String targetLanguage;
    public String backgroundColor;
    public String textColor;
    public String translatedTextColor;
}
