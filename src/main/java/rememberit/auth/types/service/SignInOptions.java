package rememberit.auth.types.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class SignInOptions {
    public String email;
    public String password;
}
