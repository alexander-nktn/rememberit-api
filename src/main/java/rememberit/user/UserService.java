package rememberit.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rememberit.config.ServiceMethodContext;
import rememberit.exception.ApplicationException;
import rememberit.exception.ErrorCode;
import rememberit.role.Role;
import rememberit.role.types.RoleType;
import rememberit.user.types.service.CreateUserOptions;
import rememberit.user.types.service.UpdateUserOptions;

import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getOne(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getOneByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getOneOrFail(String id, ServiceMethodContext ctx) {
        ctx.addProperty("userId", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        String.format("User with id %s not found", id),
                        ErrorCode.USER_NOT_FOUND,
                        ctx
                ));
    }

    public User create(CreateUserOptions opts, ServiceMethodContext ctx) {
        ctx.addProperty("email", opts.email);

        if (getOneByEmail(opts.email).isPresent()) {
            throw new ApplicationException(
                    String.format("Email %s is already in use", opts.email),
                    ErrorCode.USER_EMAIL_ALREADY_EXISTS,
                    ctx
            );
        }

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
        } catch (Exception ex) {
            logger.error("Error creating user: {}", ex.getMessage(), ex);
            throw new ApplicationException(
                    "An unexpected error occurred while creating the user",
                    ErrorCode.USER_FAILED_TO_CREATE,
                    ctx,
                    ex
            );
        }
    }

    public User update(UpdateUserOptions opts, ServiceMethodContext ctx) {
        ctx.addProperty("updateUserId", opts.id);
        ctx.addProperty("updateEmail", opts.email);

        if (opts.id == null) {
            throw new ApplicationException(
                    "User ID must not be null",
                    ErrorCode.USER_FAILED_TO_UPDATE,
                    ctx
            );
        }

        User user = getOneOrFail(opts.id, ctx);

        if (opts.firstName != null) {
            user.setFirstName(opts.firstName);
        }

        if (opts.lastName != null) {
            user.setLastName(opts.lastName);
        }

        if (opts.email != null) {
            Optional<User> existingUser = getOneByEmail(opts.email);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                throw new ApplicationException(
                        String.format("Email %s is already in use", opts.email),
                        ErrorCode.USER_EMAIL_ALREADY_EXISTS,
                        ctx
                );
            }
            user.setEmail(opts.email);
        }

        try {
            return userRepository.save(user);
        } catch (Exception ex) {
            logger.error("Error updating user: {}", ex.getMessage(), ex);
            throw new ApplicationException(
                    "An unexpected error occurred while updating the user",
                    ErrorCode.USER_FAILED_TO_UPDATE,
                    ctx,
                    ex
            );
        }
    }

    public void delete(String id, ServiceMethodContext ctx) {
        ctx.addProperty("deleteUserId", id);

        User user = getOneOrFail(id, ctx);

        try {
            userRepository.delete(user);
        } catch (Exception ex) {
            logger.error("Error deleting user with ID {}: {}", id, ex.getMessage(), ex);
            throw new ApplicationException(
                    "An unexpected error occurred while deleting the user",
                    ErrorCode.USER_FAILED_TO_DELETE,
                    ctx,
                    ex
            );
        }
    }
}