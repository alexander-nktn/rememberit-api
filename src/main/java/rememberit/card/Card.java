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
    @Column(nullable = true)
    private String imageUrl;

    @Nullable
    @Column(nullable = true)
    private String backgroundColor;


    @Nullable
    @Column(nullable = true)
    private String textColor;

    private String translatedTextColor;

    @NotNull(message = "User cannot be null")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
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