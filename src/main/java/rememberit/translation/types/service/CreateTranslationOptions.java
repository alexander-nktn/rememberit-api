package rememberit.translation.types.service;

public class CreateTranslationOptions {
    public String text;

    public String translatedText;

    public String sourceLanguage;

    public String targetLanguage;

    public CreateTranslationOptions(
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
}
