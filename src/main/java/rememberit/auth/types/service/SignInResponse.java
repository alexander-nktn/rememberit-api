package rememberit.auth.types.service;

public record SignInResponse(String accessToken, String refreshToken) {}