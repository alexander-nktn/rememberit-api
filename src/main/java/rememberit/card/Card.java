package rememberit.card;

import jakarta.annotation.Nullable;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import rememberit.translation.Translation;
import rememberit.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Nullable
    @Column(nullable = true, name = "image_url")
    private String imageUrl;

    @Nullable
    @Column(nullable = true, name = "background_color")
    private String backgroundColor;


    @Nullable
    @Column(nullable = true, name = "text_color")
    private String textColor;

    @Nullable
    @Column(nullable = true, name = "translated_text_color")
    private String translatedTextColor;

    @NotNull(message = "User cannot be null")
    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, name = "created_at")
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @NotNull(message = "Translation cannot be null")
    @OneToOne(cascade = CascadeType.MERGE, orphanRemoval = true)
    @JoinColumn(name = "translation_id", nullable = false)
    private Translation translation;
}