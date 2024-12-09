package rememberit.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(String userId);

    Optional<RefreshToken> findByUserEmail(String email);
}