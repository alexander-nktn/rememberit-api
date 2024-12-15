package rememberit.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rememberit.user.User;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String token;
    @ManyToOne
    private User user;
    private Date expiryDate;

    public RefreshToken() {
    }

    public RefreshToken(String token, User user, Date expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

}