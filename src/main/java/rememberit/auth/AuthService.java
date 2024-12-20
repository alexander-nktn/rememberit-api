package rememberit.auth;

import rememberit.auth.types.service.SignInOptions;
import rememberit.auth.types.service.SignInResponse;
import rememberit.auth.types.service.SignUpOptions;
import rememberit.config.ServiceMethodContext;
import rememberit.exception.ApplicationException;
import rememberit.exception.ErrorCode;
import rememberit.user.User;
import rememberit.user.UserService;
import rememberit.user.types.service.CreateUserOptions;
import rememberit.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

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
        ctx.addProperty("email", options.email);
        logger.info("Attempting to sign up user with email: {}", options.email);

        if (userService.getOneByEmail(options.email).isPresent()) {
            logger.warn("Sign up failed: Email {} is already in use", options.email);
            throw new ApplicationException(
                    String.format("Email %s is already in use", options.email),
                    ErrorCode.USER_EMAIL_ALREADY_EXISTS,
                    ctx
            );
        }

        CreateUserOptions createOptions = CreateUserOptions.builder()
                .email(options.email)
                .firstName(options.firstName)
                .lastName(options.lastName)
                .password(passwordEncoder.encode(options.password))
                .build();

        try {
            userService.create(createOptions, ctx);
            logger.info("Successfully signed up user with email: {}", options.email);
        } catch (Exception ex) {
            logger.error("Error during sign-up for email {}: {}", options.email, ex.getMessage(), ex);
            throw new ApplicationException(
                    "An unexpected error occurred during sign-up",
                    ErrorCode.USER_FAILED_TO_CREATE,
                    ctx,
                    ex
            );
        }
    }

    public SignInResponse signIn(SignInOptions options, ServiceMethodContext ctx) {
        ctx.addProperty("email", options.email);
        logger.info("Attempting to sign in user with email: {}", options.email);

        User user = userService.getOneByEmail(options.email)
                .orElseThrow(() -> {
                    logger.warn("Sign in failed: User with email {} not found", options.email);
                    return new ApplicationException(
                            "User not found",
                            ErrorCode.USER_NOT_FOUND,
                            ctx
                    );
                });

        if (!passwordEncoder.matches(options.password, user.getPassword())) {
            logger.warn("Sign in failed: Invalid credentials for email {}", options.email);
            throw new ApplicationException(
                    "Invalid credentials",
                    ErrorCode.AUTH_INVALID_CREDENTIALS,
                    ctx
            );
        }

        try {
            String accessToken = jwtUtils.generateToken(user.getEmail());
            String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

            RefreshToken refreshTokenEntity = new RefreshToken(
                    refreshToken, user, new Date(System.currentTimeMillis() + 86400000L) // 1 day expiration
            );
            refreshTokenRepository.save(refreshTokenEntity);

            logger.info("Successfully signed in user with email: {}", options.email);
            return new SignInResponse(accessToken, refreshToken);
        } catch (Exception ex) {
            logger.error("Error during sign-in for email {}: {}", options.email, ex.getMessage(), ex);
            throw new ApplicationException(
                    "Failed to generate authentication tokens",
                    ErrorCode.AUTH_FAILED_TO_GENERATE_TOKEN,
                    ctx,
                    ex
            );
        }
    }

    public SignInResponse refreshToken(String refreshToken, ServiceMethodContext ctx) {
        ctx.addProperty("refreshToken", refreshToken);
        logger.info("Attempting to refresh token");

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    logger.warn("Refresh token not found");
                    return new ApplicationException(
                            "Refresh token is invalid",
                            ErrorCode.AUTH_TOKEN_NOT_FOUND,
                            ctx
                    );
                });

        if (storedToken.getExpiryDate().before(new Date())) {
            logger.warn("Refresh token has expired");
            throw new ApplicationException(
                    "Refresh token has expired",
                    ErrorCode.AUTH_TOKEN_EXPIRED,
                    ctx
            );
        }

        try {
            String newAccessToken = jwtUtils.generateToken(storedToken.getUser().getEmail());
            String newRefreshToken = jwtUtils.generateRefreshToken(storedToken.getUser().getEmail());

            storedToken.setExpiryDate(new Date(System.currentTimeMillis() + 86400000L)); // New expiration
            refreshTokenRepository.save(storedToken);

            logger.info("Successfully refreshed tokens for user: {}", storedToken.getUser().getEmail());
            return new SignInResponse(newAccessToken, newRefreshToken);
        } catch (Exception ex) {
            logger.error("Error during token refresh: {}", ex.getMessage(), ex);
            throw new ApplicationException(
                    "An unexpected error occurred while refreshing the token",
                    ErrorCode.AUTH_FAILED_TO_REFRESH_TOKEN,
                    ctx,
                    ex
            );
        }
    }
}