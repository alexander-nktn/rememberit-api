package rememberit.auth.types.resolver;

import rememberit.auth.types.service.SignInOptions;

//public class SignInInput extends SignInOptions {
//    public SignInInput(SignInInput.Builder builder) {
//        super(builder);
//    }
//
//    public static class Builder extends SignInOptions.Builder {
//        public SignInInput build() {
//            return new SignInInput(this);
//        }
//    }
//}

public class SignInInput {
    public String email;
    public String password;
}
