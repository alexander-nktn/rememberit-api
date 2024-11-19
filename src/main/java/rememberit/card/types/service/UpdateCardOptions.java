package rememberit.card.types.service;

public class UpdateCardOptions {
    public String id;
    public String userId;
    public String translationId;
    public String imageUrl;

    public UpdateCardOptions() {}

    public UpdateCardOptions(
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
