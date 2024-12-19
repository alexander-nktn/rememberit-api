package rememberit.card.types.resolver;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import rememberit.translation.types.common.Language;

import java.util.List;

@Getter
@Setter
public class GenerateCardsInput {
    @Valid
    public List<@Valid GenerateCardsTranslationsInput> translations;

    @Pattern(
            regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
            message = "Spreadsheet URL must be a valid URL"
    )
    public String spreadsheetUrl;

    @NotNull(message = "Source language is required")
    public Language sourceLanguage;

    @NotNull(message = "Target language is required")
    public Language targetLanguage;

    @NotBlank(message = "Background color must not be blank")
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Invalid background color format. Must be a hex color code (e.g., #ffffff).")
    public String backgroundColor;

    @NotBlank(message = "Text color must not be blank")
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Invalid text color format. Must be a hex color code (e.g., #000000).")
    public String textColor;

    @NotBlank(message = "Translated text color must not be blank")
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Invalid translated text color format. Must be a hex color code (e.g., #ff0000).")
    public String translatedTextColor;
}