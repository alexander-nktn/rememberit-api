package rememberit.card.types.service;

import rememberit.card.types.resolver.GenerateCardsInput;

import java.util.List;

public class GenerateCardsOptions extends GenerateCardsInput {
    public GenerateCardsOptions(
            List<String> texts,
            String spreadsheetUrl,
            String sourceLanguage,
            String targetLanguage,
            String backgroundColor,
            String textColor,
            String translatedTextColor
    ) {
        this.texts = texts;
        this.spreadsheetUrl = spreadsheetUrl;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.translatedTextColor = translatedTextColor;
    }
}
