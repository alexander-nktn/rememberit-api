package com.boost.memory.models;
import com.boost.memory.types.card.service.CardCreateOptions;
import jakarta.persistence.*;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import java.util.Date;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "cards")
@EntityListeners(AuditingEntityListener.class)
public class Card {
    public Card() {}

    public Card(CardCreateOptions opts) {
        this.translation = opts.translation;
        this.user = opts.user;
        this.imageUrl = opts.imageUrl;
        this.createdAt = new Date();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    @Column(nullable = false)
    public String imageUrl;

    @CreatedDate()
    public Date createdAt;

    @Setter
    @OneToOne()
    public Translation translation;

    @Setter
    @ManyToOne()
    public User user;

}
