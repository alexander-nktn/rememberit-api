package rememberit.auth.types.service;

public class SignUpOptions {
    public String firstName;
    public String lastName;
    public String email;
    public String password;

    public SignUpOptions(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.password = builder.password;
    }

    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String password;

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public SignUpOptions build() {
            return new SignUpOptions(this);
        }
    }
}
