package rememberit.auth.types.resolver;

import rememberit.auth.types.service.SignUpOptions;

//public class SignUpInput extends SignUpOptions {
//    public SignUpInput(Builder builder) {
//        super(builder);
//    }
//
//    public static class Builder extends SignUpOptions.Builder {
//        public SignUpInput build() {
//            return new SignUpInput(this);
//        }
//    }
//}

public class SignUpInput {
    public String firstName;
    public String lastName;
    public String email;
    public String password;
}

