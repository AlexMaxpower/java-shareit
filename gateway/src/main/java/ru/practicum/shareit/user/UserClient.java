package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(String authHeader, UserDto userDto) {
        return post("", userDto, authHeader);
    }

    public ResponseEntity<Object> getUserById(String authHeader, Long userId) {
        return get("/" + userId, authHeader);
    }

    public ResponseEntity<Object> getUsers(String authHeader) {
        return get("", authHeader);
    }

    public ResponseEntity<Object> update(String authHeader, UserDto userDto, Long userId) {
        return patch("/" + userId, userDto, authHeader);
    }

    public ResponseEntity<Object> delete(String authHeader, Long userId) {
        return delete("/" + userId, authHeader);
    }
}