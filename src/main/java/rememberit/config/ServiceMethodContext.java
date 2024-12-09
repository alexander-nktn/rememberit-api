package rememberit.config;

import rememberit.user.User;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ServiceMethodContext {
    private User user;
    private final Map<String, Object> properties = new HashMap<>();

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }
}