package rememberit.card;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import rememberit.translation.Translation;
import rememberit.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "cards")
@Getter
@Setter
public class Card {

    public Card(
            String imageUrl,
            String backgroundColor,
            String textColor,
            String translatedTextColor,
            Translation translation,
            User user
    ) {
        this.imageUrl = imageUrl;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.translatedTextColor = translatedTextColor;
        this.translation = translation;
        this.user = user;
    }

    public Card() {}

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    public String imageUrl;
    public String backgroundColor;
    public String textColor;
    public String translatedTextColor;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public User user;

    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @OneToOne(cascade = CascadeType.MERGE, orphanRemoval = true)
    @JoinColumn(name = "translation_id", nullable = false)
    public Translation translation;
}