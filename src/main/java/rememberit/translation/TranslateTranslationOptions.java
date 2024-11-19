package rememberit.translation;

public class TranslateTranslationOptions {
    public String text;
    public String sourceLanguage;
    public String targetLanguage;

    public TranslateTranslationOptions(
            String text,
            String sourceLanguage,
            String targetLanguage
    ) {
        this.text = text;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }
}
