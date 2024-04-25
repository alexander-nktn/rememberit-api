package com.boost.memory.services;
import com.boost.memory.exception.ServiceMethodContext;
import com.boost.memory.models.User;
import com.boost.memory.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getOne(String id) {
        return userRepository.findById(id);
    }

    public User getOneOrFail(String id, ServiceMethodContext ctx) {
        ctx.addProperty("id", id);
        Optional<User> user  = this.getOne(id);

        if (user.isEmpty()) {
            throw new EntityNotFoundException(String.format("User with id: %s not found", id));
        }

        return user.get();
    }
}
