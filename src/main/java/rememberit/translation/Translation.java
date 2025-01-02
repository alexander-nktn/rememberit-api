package rememberit.translation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import rememberit.translation.types.common.Language;

@Entity(name = "translations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    @NotNull
    @Column(nullable = false)
    public String text;

    @NotNull
    @Column(nullable = false)
    public String translatedText;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(
            name = "source_language",
            nullable = false
    )
    public Language sourceLanguage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(
            name = "target_language",
            nullable = false
    )
    public Language targetLanguage;
}