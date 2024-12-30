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
    private String id;

    @NotNull
    @Column(nullable = false)
    private String text;

    @NotNull
    @Column(nullable = false)
    private String translatedText;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source_language")
    private Language sourceLanguage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "target_language")
    private Language targetLanguage;
}