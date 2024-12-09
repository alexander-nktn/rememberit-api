package rememberit.user.types.service;

public class UpdateUserOptions {
    public final String id;
    public String firstName;
    public String lastName;
    public String email;

    public UpdateUserOptions(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
    }

    public static class Builder {
        private String id;
        private String firstName;
        private String lastName;
        private String email;

        // Parameterless constructor
        public Builder() {}

        // Constructor with id for required field
        public Builder(String id) {
            this.id = id;
        }

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

        public UpdateUserOptions build() {
            return new UpdateUserOptions(this);
        }
    }
}