package rememberit.translation.types.service;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import rememberit.translation.types.common.Language;

@Builder
public class CreateTranslationOptions {

    @NotNull
    public final String text;

    @NotNull
    public final String translatedText;

    @NotNull
    public final Language sourceLanguage;

    @NotNull
    public final Language targetLanguage;
}