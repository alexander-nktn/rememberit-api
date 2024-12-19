package rememberit.auth.types.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SignUpOptions {
    public String firstName;
    public String lastName;
    public String email;
    public String password;
}
