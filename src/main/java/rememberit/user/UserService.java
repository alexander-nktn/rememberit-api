package rememberit.user;
import rememberit.card.Card;
import rememberit.config.ServiceMethodContext;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rememberit.role.Role;
import rememberit.role.types.RoleType;
import rememberit.user.types.service.CreateUserOptions;
import rememberit.user.types.service.UpdateUserOptions;

import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public Optional<User> getOne(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getOneByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getOneOrFail(String id, ServiceMethodContext ctx) {
        ctx.addProperty("id", id);
        Optional<User> user  = this.getOne(id);

        if (user.isEmpty()) {
            throw new EntityNotFoundException(String.format("User with id: %s not found", id));
        }

        return user.get();
    }

    public User create(CreateUserOptions opts, ServiceMethodContext ctx) {
        Role role = new Role();
        role.setId("9cd1ebb8-7036-4f5a-bc38-b52a3a71b4bf");
        role.setName(RoleType.USER);

        User user = User.builder()
                    .firstName(opts.firstName)
                    .lastName(opts.lastName)
                    .email(opts.email)
                    .password(opts.password)
                    .role(role)
                    .build();

        try {
            return userRepository.save(user);
        } catch (Exception error) {
            throw new RuntimeException("Failed to create user", error);
        }
    }

    public User update(UpdateUserOptions opts, ServiceMethodContext ctx) {
        User user = this.getOneOrFail(opts.id, ctx);

        System.out.println("opts.firstName: " + opts.firstName);

        if (opts.firstName != null) {
            user.setFirstName(opts.firstName);
        }

        if (opts.lastName != null) {
            user.setLastName(opts.lastName);
        }

        if (opts.email != null) {
            // Check if email is already taken
            Optional<User> existingUser = this.getOneByEmail(opts.email);

            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                throw new RuntimeException("Email is already taken");
            }
            user.setEmail(opts.email);
        }

        System.out.println("User: " + user);

        try {
            return userRepository.save(user);
        } catch (Exception error) {
            throw new RuntimeException("Failed to update translation", error);
        }

    }

    public void delete(String id, ServiceMethodContext ctx) {
        ctx.addProperty("id", id);
        Optional<User> card = this.getOne(id);

        if (card.isEmpty()) {
            return;
        }

        try {
            userRepository.deleteById(id);
        } catch (Exception error) {
            throw new RuntimeException("Failed to delete user", error);
        }
    }
}
