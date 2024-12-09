package rememberit.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private final Key key;
    private final long jwtExpirationMs;
    private final long refreshTokenExpirationMs; // Expiration for refresh token

    public JwtUtils(
            @Value("${spring.jwt.secret}") String jwtSecret,
            @Value("${spring.jwt.expiration}") long jwtExpirationMs,
            @Value("${spring.jwt.refreshExpiration}") long refreshTokenExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    // Generate Access Token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))  // Access token expiration
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Generate Refresh Token (longer expiration time)
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))  // Refresh token expiration
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Extract email from token (for both access and refresh tokens)
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Validate the access token (and refresh token if needed)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);  // Attempt to parse and validate the token
            return true;
        } catch (JwtException e) {
            return false;  // Return false if the token is invalid or expired
        }
    }

    // Optionally, you could add methods for checking token expiration
    public boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expirationDate.before(new Date());  // Checks if the token has expired
    }
}