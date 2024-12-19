package rememberit.card.types.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import rememberit.translation.Translation;
import rememberit.user.User;

@Builder
@Getter
@Setter
public class CreateCardOptions {
    public User user;
    public Translation translation;
    public String imageUrl;
    public String backgroundColor;
    public String textColor;
    public String translatedTextColor;
}
