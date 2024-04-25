package com.boost.memory.types.card.service;

public class CardUpdateOptions {
    public String id;
    public String userId;
    public String translationId;
    public String imageUrl;

    public CardUpdateOptions() {}

    public CardUpdateOptions(
            String id,
            String userId,
            String translationId,
            String imageUrl
    ) {
        this.id = id;
        this.userId = userId;
        this.translationId = translationId;
        this.imageUrl = imageUrl;
    }
}
