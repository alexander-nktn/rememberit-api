package rememberit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rememberit.user.User;
import rememberit.user.UserService;
import rememberit.util.JwtUtils;
import rememberit.auth.RefreshTokenRepository;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserService userService, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Skip token validation for the local development environment if needed
//          String host = request.getHeader("Host");
//        if (host.equals("localhost:8080")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtils.validateToken(token)) {
                String email = jwtUtils.getEmailFromToken(token);
                User user = userService.getOneByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                if (user != null) {
                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                            .withUsername(user.getEmail())
                            .password(user.getPassword()) // hashed password from DB
                            .authorities("USER") // adjust if you implement roles
                            .build();

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } else {
                // Token is invalid or expired, check if a refresh token is provided
                String refreshToken = request.getHeader("Refresh-Token");
                if (refreshToken != null && refreshTokenRepository.findByToken(refreshToken).isPresent()) {
                    // Validate refresh token and issue a new access token
                    String email = jwtUtils.getEmailFromToken(refreshToken);
                    User user = userService.getOneByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    // Generate new access token
                    String newAccessToken = jwtUtils.generateToken(user.getEmail());
                    response.setHeader("Access-Token", newAccessToken);  // Send new access token to the client
                    return;  // Continue the filter chain without further processing the old token
                } else {
                    // Invalid token or missing refresh token, reject the request
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // Return 401 Unauthorized
                    response.getWriter().write("Invalid or missing token");
                    return;  // Do not proceed with the filter chain
                }
            }
        } else {
            // Missing token, reject the request
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // Return 401 Unauthorized
            response.getWriter().write("Missing token");
            return;  // Do not proceed with the filter chain
        }

        filterChain.doFilter(request, response);  // Continue filter chain if token is valid
    }
}