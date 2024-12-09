package rememberit.user;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import rememberit.config.ServiceMethodContext;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import graphql.schema.DataFetchingEnvironment;
import rememberit.user.types.resolver.UpdateUserInput;
import rememberit.user.types.service.UpdateUserOptions;

@Controller
public class UserResolver {
    private final UserService userService;

    public UserResolver(
            UserService userService
    ) {
        this.userService = userService;
    }

    @QueryMapping
    public User getMe(DataFetchingEnvironment env) {
        ServiceMethodContext ctx = env.getGraphQlContext().get("serviceMethodContext");
        return ctx.getUser();
    }

    @MutationMapping
    public User updateUser(
            @Argument UpdateUserInput input,
            DataFetchingEnvironment env
    ) {
        ServiceMethodContext ctx = env.getGraphQlContext().get("serviceMethodContext");

        UpdateUserOptions options = new UpdateUserOptions.Builder(input.id)
                .firstName(input.firstName)
                .lastName(input.lastName)
                .email(input.email)
                .build();

        return userService.update(options, ctx);
    }


}