package rememberit.user.types.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CreateUserOptions {
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String roleId;
}
