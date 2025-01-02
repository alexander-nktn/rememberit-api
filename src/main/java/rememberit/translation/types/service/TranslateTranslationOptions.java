package rememberit.translation.types.service;

import lombok.Builder;
import rememberit.translation.types.common.Language;

@Builder
public class TranslateTranslationOptions {
    public String text;
    public Language sourceLanguage;
    public Language targetLanguage;
}
