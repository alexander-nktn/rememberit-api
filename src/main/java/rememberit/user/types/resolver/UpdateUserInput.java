package rememberit.user.types.resolver;

import rememberit.user.types.service.UpdateUserOptions;

//public class UpdateUserInput extends UpdateUserOptions {
//    public UpdateUserInput(Builder builder) {
//        super(builder);
//    }
//
//    public static class Builder extends UpdateUserOptions.Builder {
//        public Builder() {
//            super();
//        }
//
//        public UpdateUserInput build() {
//            return new UpdateUserInput(this);
//        }
//    }
//}

public class UpdateUserInput {
    public final String id;
    public String firstName;
    public String lastName;
    public String email;

    public UpdateUserInput(String id) {
        this.id = id;
    }
}