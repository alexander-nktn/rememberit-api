package rememberit.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import rememberit.user.User;
import rememberit.user.UserService;

@Component
public class CustomGraphQlInterceptor implements WebGraphQlInterceptor {

    private final UserService userService;

    public CustomGraphQlInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ServiceMethodContext serviceMethodContext = new ServiceMethodContext();

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.getOneByEmail(email).orElse(null);
            serviceMethodContext.setUser(user);
        }

        request.configureExecutionInput((executionInput, builder) ->
                builder.graphQLContext(contextBuilder ->
                        contextBuilder.put("serviceMethodContext", serviceMethodContext)
                ).build()
        );

        return chain.next(request);
    }
}