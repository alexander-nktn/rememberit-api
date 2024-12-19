package rememberit.auth;

import graphql.schema.DataFetchingEnvironment;
import rememberit.auth.types.resolver.SignInInput;
import rememberit.auth.types.resolver.SignUpInput;
import rememberit.auth.types.service.SignInOptions;
import rememberit.auth.types.service.SignInResponse;
import rememberit.auth.types.service.SignUpOptions;
import rememberit.config.ServiceMethodContext;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthResolver {
    private final AuthService authService;

    public AuthResolver(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @MutationMapping
    public SignInResponse signIn(@Argument SignInInput input) {
        SignInOptions options = SignInOptions.builder()
                .email(input.email)
                .password(input.password)
                .build();
        return authService.signIn(options);
    }

    @MutationMapping
    public String signUp(
            @Argument SignUpInput input,
            DataFetchingEnvironment env
    ) {
        ServiceMethodContext ctx = env.getGraphQlContext().get("serviceMethodContext");

        SignUpOptions options = SignUpOptions.builder()
                .email(input.email)
                .firstName(input.firstName)
                .lastName(input.lastName)
                .password(input.password)
                .build();

        authService.signUp(options, ctx);
        return "User signed up successfully";
    }

    @MutationMapping
    public SignInResponse refreshToken(@Argument String refreshToken) {
        return authService.refreshToken(refreshToken);
    }
}