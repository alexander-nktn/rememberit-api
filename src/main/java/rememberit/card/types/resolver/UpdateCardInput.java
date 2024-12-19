package rememberit.card.types.resolver;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdateCardInput {

    @NotBlank(message = "Card ID is required")
    public final String id;

    @Pattern(regexp = "^(http|https)://.*$", message = "Image URL must be a valid URL")
    public String imageUrl;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Background color must be a valid hex color (e.g., #FFFFFF)")
    public String backgroundColor;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Text color must be a valid hex color (e.g., #FFFFFF)")
    public String textColor;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Translated text color must be a valid hex color (e.g., #FFFFFF)")
    public String translatedTextColor;

    @Valid
    public UpdateTranslationInput translation; // Optional, but validate if present

    @Builder
    @Getter
    @Setter
    public static class UpdateTranslationInput {

        @NotBlank(message = "Translation ID is required")
        public final String id;
        public String text;

        public String translatedText;
    }
}