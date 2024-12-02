package rememberit.translation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rememberit.translation.types.common.Language;

@Entity(name = "translations")
@Getter
@Setter
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;
    public String text;
    public String translatedText;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_language")
    private Language sourceLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_language")
    private Language targetLanguage;


    public Translation(
            String text,
            String translatedText,
            Language sourceLanguage,
            Language targetLanguage
    ) {
        this.text = text;
        this.translatedText = translatedText;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }
    public Translation() {}
}