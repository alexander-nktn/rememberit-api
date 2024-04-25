package com.boost.memory.exception;

import com.boost.memory.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;
import java.util.HashMap;

@Data
@AllArgsConstructor
public class ServiceMethodContext {
    public User user = new User("first-user", "Oleksandr", "Nikitin", "nikitin030686@gmail.com");
    private Map<String, Object> properties;

    public ServiceMethodContext() {
        this.properties = new HashMap<>();
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }
}
