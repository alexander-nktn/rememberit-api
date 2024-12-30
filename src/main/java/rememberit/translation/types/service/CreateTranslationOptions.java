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

    public static class CreateTranslationOptionsBuilder {
        public CreateTranslationOptions build() {
            if (text == null) throw new IllegalStateException("Field 'text' must not be null");
            if (translatedText == null) throw new IllegalStateException("Field 'translatedText' must not be null");
            if (sourceLanguage == null) throw new IllegalStateException("Field 'sourceLanguage' must not be null");
            if (targetLanguage == null) throw new IllegalStateException("Field 'targetLanguage' must not be null");
            return new CreateTranslationOptions(text, translatedText, sourceLanguage, targetLanguage);
        }
    }
}