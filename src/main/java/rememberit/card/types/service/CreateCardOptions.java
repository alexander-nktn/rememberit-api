package rememberit.card.types.service;

import rememberit.translation.Translation;
import rememberit.user.User;

public class CreateCardOptions {
    public User user;
    public Translation translation;
    public String imageUrl;
    public String backgroundColor;
    public String textColor;
    public String translatedTextColor;

    public CreateCardOptions(
            User user,
            Translation translation,
            String imageUrl,
            String backgroundColor,
            String textColor,
            String translatedTextColor
    ) {
        this.user = user;
        this.translation = translation;
        this.imageUrl = imageUrl;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.translatedTextColor = translatedTextColor;
    }
}
