package rememberit.user.types.resolver;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserInput {
    @NotBlank(message = "Card ID is required")
    public String id;

    public String firstName;

    public String lastName;

    public String email;
}