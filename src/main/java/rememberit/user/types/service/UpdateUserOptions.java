package rememberit.user.types.service;

import lombok.Builder;

@Builder
public class UpdateUserOptions {
    public final String id;
    public String firstName;
    public String lastName;
    public String email;
}