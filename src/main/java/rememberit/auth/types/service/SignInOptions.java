package rememberit.auth.types.service;

public class SignInOptions {
    public String email;
    public String password;

    public SignInOptions(Builder builder) {
        this.email = builder.email;
        this.password = builder.password;
    }

    public static class Builder {
        private String email;
        private String password;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public SignInOptions build() {
            return new SignInOptions(this);
        }
    }
}
