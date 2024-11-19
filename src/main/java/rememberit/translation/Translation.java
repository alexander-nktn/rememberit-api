package rememberit.translation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "translations")
@Getter
@Setter
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    public String text;
    public String translatedText;
    public String sourceLanguage;
    public String targetLanguage;


    public Translation(
            String text,
            String translatedText,
            String sourceLanguage,
            String targetLanguage
    ) {
        this.text = text;
        this.translatedText = translatedText;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }
    public Translation() {}
}