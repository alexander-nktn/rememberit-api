package com.boost.memory.types.card.service;

import com.boost.memory.types.card.dto.CardGenerateDTO;

import java.awt.Color;

public class CardGenerateOptions extends CardGenerateDTO {
    public CardGenerateOptions(
            String[] texts,
            String sourceLanguage,
            String targetLanguage,
            Color backgroundColor,
            Color textColor,
            Color translatedTextColor
    ) {
        this.texts = texts;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.translatedTextColor = translatedTextColor;
    }
}
