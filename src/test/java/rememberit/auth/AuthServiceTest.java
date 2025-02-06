package rememberit.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import rememberit.auth.types.service.SignInOptions;
import rememberit.auth.types.service.SignInResponse;
import rememberit.auth.types.service.SignUpOptions;
import rememberit.config.ServiceMethodContext;
import rememberit.exception.ApplicationException;
import rememberit.exception.ErrorCode;
import rememberit.translation.TranslationService;
import rememberit.user.User;
import rememberit.user.UserService;
import rememberit.user.types.service.CreateUserOptions;
import rememberit.util.JwtUtils;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtils jwtUtils;


    @Mock
    private TranslationService translationService;

    @InjectMocks
    private AuthService authService;

    private ServiceMethodContext ctx;

    @BeforeEach
    void setUp() {
        ctx = new ServiceMethodContext();
    }

    @Test
    void signUp_WhenEmailAlreadyExists_ThrowsException() {
        // Arrange
        SignUpOptions signUpOptions = SignUpOptions.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .password("password123")
                .build();

        // Simulate an existing user
        when(userService.getOneByEmail(signUpOptions.email))
                .thenReturn(Optional.of(new User()));

        // Act & Assert
        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                authService.signUp(signUpOptions, ctx)
        );

        // Verify error code or message
        assertEquals(ErrorCode.USER_EMAIL_ALREADY_EXISTS, ex.getErrorCode());

        // Verify we never call userService.create
        verify(userService, never()).create(any(CreateUserOptions.class), any(ServiceMethodContext.class));
    }

    @Test
    void signUp_WhenEmailIsAvailable_CreatesUser() {
        // Arrange
        SignUpOptions signUpOptions = SignUpOptions.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .password("password123")
                .build();

        when(userService.getOneByEmail(signUpOptions.email))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(signUpOptions.password))
                .thenReturn("encoded-password");

        // Act
        authService.signUp(signUpOptions, ctx);

        // Assert
        // Ensure userService.create was called once with matching options
        verify(userService).create(
                argThat(opts ->
                        opts.getEmail().equals("johndoe@example.com") &&
                                opts.getFirstName().equals("John") &&
                                opts.getLastName().equals("Doe") &&
                                opts.getPassword().equals("encoded-password")
                ),
                org.mockito.ArgumentMatchers.eq(ctx)
        );
    }

    @Test
    void signIn_WhenEmailNotFound_ThrowsException() {
        // Arrange
        SignInOptions options = SignInOptions.builder()
                .email("nonexistent@example.com")
                .password("1234")
                .build();

        when(userService.getOneByEmail(options.email)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException ex = assertThrows(
                ApplicationException.class,
                () -> authService.signIn(options, ctx)
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        verify(userService).getOneByEmail(options.email);
        verifyNoMoreInteractions(userService, passwordEncoder, jwtUtils, refreshTokenRepository);
    }

    @Test
    void signIn_WhenPasswordMismatch_ThrowsException() {
        // Arrange
        SignInOptions options = SignInOptions.builder()
                .email("user@example.com")
                .password("wrongpassword")
                .build();

        User mockUser = new User();
        mockUser.setEmail("user@example.com");
        mockUser.setPassword("realEncodedPassword");

        when(userService.getOneByEmail(options.email)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(options.password, mockUser.getPassword())).thenReturn(false);

        // Act & Assert
        ApplicationException ex = assertThrows(
                ApplicationException.class,
                () -> authService.signIn(options, ctx)
        );
        assertEquals(ErrorCode.AUTH_INVALID_CREDENTIALS, ex.getErrorCode());
        verify(userService).getOneByEmail(options.email);
        verify(passwordEncoder).matches(options.password, mockUser.getPassword());
        verifyNoMoreInteractions(userService, jwtUtils, refreshTokenRepository);
    }

    @Test
    void refreshToken_NotFound_ThrowsException() {
        // Arrange
        String invalidRefreshToken = "invalid-refresh-token";
        when(refreshTokenRepository.findByToken(invalidRefreshToken))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> authService.refreshToken(invalidRefreshToken, ctx)
        );

        assertEquals(ErrorCode.AUTH_TOKEN_NOT_FOUND, ex.getErrorCode());
        verify(refreshTokenRepository).findByToken(invalidRefreshToken);
        verifyNoMoreInteractions(jwtUtils);
    }

    @Test
    void refreshToken_Expired_ThrowsException() {
        // Arrange
        String expiredRefreshToken = "expired-token";
        User mockUser = new User();
        mockUser.setEmail("user@example.com");

        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setToken(expiredRefreshToken);
        expiredToken.setExpiryDate(new Date(System.currentTimeMillis() - 1000L)); // Past date
        expiredToken.setUser(mockUser);

        when(refreshTokenRepository.findByToken(expiredRefreshToken))
                .thenReturn(Optional.of(expiredToken));

        // Act & Assert
        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> authService.refreshToken(expiredRefreshToken, ctx)
        );

        assertEquals(ErrorCode.AUTH_TOKEN_EXPIRED, ex.getErrorCode());
        verify(refreshTokenRepository).findByToken(expiredRefreshToken);
        verifyNoMoreInteractions(jwtUtils);
    }

    @Test
    void refreshToken_Success_ReturnsNewTokens() {
        // Arrange
        String validToken = "valid-token";
        User mockUser = new User();
        mockUser.setEmail("user@example.com");

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(validToken);
        refreshTokenEntity.setExpiryDate(new Date(System.currentTimeMillis() + 60_000L)); // Future date
        refreshTokenEntity.setUser(mockUser);

        when(refreshTokenRepository.findByToken(validToken))
                .thenReturn(Optional.of(refreshTokenEntity));

        // Mock new token generation
        when(jwtUtils.generateToken(mockUser.getEmail())).thenReturn("newAccessToken");
        when(jwtUtils.generateRefreshToken(mockUser.getEmail())).thenReturn("newRefreshToken");

        // Act
        SignInResponse result = authService.refreshToken(validToken, ctx);

        // Assert
        assertNotNull(result);
        assertEquals("newAccessToken", result.accessToken());
        assertEquals("newRefreshToken", result.refreshToken());
        verify(refreshTokenRepository).findByToken(validToken);
        verify(refreshTokenRepository).save(refreshTokenEntity);
        verify(jwtUtils).generateToken(mockUser.getEmail());
        verify(jwtUtils).generateRefreshToken(mockUser.getEmail());
    }

    @Test
    void refreshToken_TokenGenerationFails_ThrowsException() {
        // Arrange
        String validToken = "valid-token";
        User mockUser = new User();
        mockUser.setEmail("user@example.com");

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(validToken);
        refreshTokenEntity.setExpiryDate(new Date(System.currentTimeMillis() + 60_000L)); // Future date
        refreshTokenEntity.setUser(mockUser);

        when(refreshTokenRepository.findByToken(validToken))
                .thenReturn(Optional.of(refreshTokenEntity));

        // Simulate token generation error
        when(jwtUtils.generateToken(mockUser.getEmail()))
                .thenThrow(new RuntimeException("JWT generation error"));

        // Act & Assert
        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> authService.refreshToken(validToken, ctx)
        );

        assertEquals(ErrorCode.AUTH_FAILED_TO_REFRESH_TOKEN, ex.getErrorCode());
        verify(refreshTokenRepository).findByToken(validToken);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }
}