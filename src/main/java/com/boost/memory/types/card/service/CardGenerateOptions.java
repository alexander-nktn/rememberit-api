package com.boost.memory.types.card.service;

import com.boost.memory.types.card.dto.CardGenerateDTO;

public class CardGenerateOptions extends CardGenerateDTO {
    public CardGenerateOptions(
            String[] words,
            String language
    ) {
        this.words = words;
        this.language = language;
    }
}
