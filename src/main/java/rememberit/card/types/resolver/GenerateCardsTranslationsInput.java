package rememberit.card.types.resolver;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateCardsTranslationsInput {
        @NotBlank(message = "Text is required")
        public String text;

        public String translatedText;
}