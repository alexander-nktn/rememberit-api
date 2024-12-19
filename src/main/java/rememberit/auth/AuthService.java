package rememberit.auth;

import rememberit.auth.types.service.SignInOptions;
import rememberit.auth.types.service.SignInResponse;
import rememberit.auth.types.service.SignUpOptions;
import rememberit.user.User;
import rememberit.user.UserService;
import rememberit.util.JwtUtils;
import rememberit.config.ServiceMethodContext;
import rememberit.user.types.service.CreateUserOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Date;

@Service
@Transactional
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void signUp(SignUpOptions options, ServiceMethodContext ctx) {
        if (userService.getOneByEmail(options.email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        CreateUserOptions createOptions = CreateUserOptions.builder()
                .email(options.email)
                .firstName(options.firstName)
                .lastName(options.lastName)
                .password(passwordEncoder.encode(options.password))
                .build();

        userService.create(createOptions, ctx);
    }

    public SignInResponse signIn(SignInOptions options) {
        User user = userService.getOneByEmail(options.email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(options.password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate JWT access token
        String accessToken = jwtUtils.generateToken(user.getEmail());

        // Generate refresh token
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        // Save refresh token in the database
        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, user, new Date(System.currentTimeMillis() + 86400000L)); // 1 day expiration
        refreshTokenRepository.save(refreshTokenEntity);

        return new SignInResponse(accessToken, refreshToken);
    }

    public SignInResponse refreshToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (storedToken.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Refresh token has expired");
        }

        // Generate new tokens
        String newAccessToken = jwtUtils.generateToken(storedToken.getUser().getEmail());
        String newRefreshToken = jwtUtils.generateRefreshToken(storedToken.getUser().getEmail());

        // Optionally update the stored refresh token
        storedToken.setExpiryDate(new Date(System.currentTimeMillis() + 86400000L)); // New expiration
        refreshTokenRepository.save(storedToken);

        return new SignInResponse(newAccessToken, newRefreshToken);
    }
}