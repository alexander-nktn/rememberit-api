package rememberit.user;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('USER_CAN_GET')")
    public User getMe(DataFetchingEnvironment env) {
        ServiceMethodContext ctx = env.getGraphQlContext().get("serviceMethodContext");
        return ctx.getUser();
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER_CAN_UPDATE')")
    public User updateUser(
            @Argument UpdateUserInput input,
            DataFetchingEnvironment env
    ) {
        ServiceMethodContext ctx = env.getGraphQlContext().get("serviceMethodContext");

        System.out.println("input: " + input.firstName);

        UpdateUserOptions options = UpdateUserOptions.builder()
                .id(input.id)
                .firstName(input.firstName)
                .lastName(input.lastName)
                .email(input.email)
                .build();

        return userService.update(options, ctx);
    }


    @MutationMapping
    @PreAuthorize("hasAuthority('USER_CAN_DELETE')")
    public String deleteUser(
            @Argument String id,
            DataFetchingEnvironment env
    ) {
        ServiceMethodContext ctx = env.getGraphQlContext().get("serviceMethodContext");
        userService.delete(id, ctx);

        return id;
    }


}