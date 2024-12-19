package rememberit.card.types.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateCardOptions {
    public final String id;

    public String userId;

    public String translationId;

    public String imageUrl;

    public String backgroundColor;

    public String textColor;

    public String translatedTextColor;
}
