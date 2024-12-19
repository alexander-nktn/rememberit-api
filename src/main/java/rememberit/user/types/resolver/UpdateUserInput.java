package rememberit.user.types.resolver;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserInput {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
}