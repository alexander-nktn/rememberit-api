package com.boost.memory.types.card.service;

import com.boost.memory.models.Translation;
import com.boost.memory.models.User;

public class CardCreateOptions {
    public User user;
    public Translation translation;
    public String imageUrl;

    public CardCreateOptions(
            User user
    ) {
        this.user = user;
    }
}
