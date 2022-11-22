package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(String authHeader, Long userId, ItemDto itemDto) {
        return post("", userId, itemDto, authHeader);
    }

    public ResponseEntity<Object> getItemById(String authHeader, Long userId, Long itemId) {
        return get("/" + itemId, userId, authHeader);
    }

    public ResponseEntity<Object> getItemsByOwner(String authHeader, Long userId, Integer from, Integer size) {
        String path = "?from=" + from;
        if (size != null) {
            path += "&size=" + size;
        }
        return get(path, userId, authHeader);
    }

    public ResponseEntity<Object> update(String authHeader, ItemDto itemDto, Long itemId, Long userId) {
        return patch("/" + itemId, userId, itemDto, authHeader);
    }

    public ResponseEntity<Object> delete(String authHeader, Long itemId, Long userId) {
        return delete("/" + itemId, userId, authHeader);
    }

    public ResponseEntity<Object> getItemsBySearchQuery(String authHeader, String text, Integer from, Integer size) {
        String path = "/search?text=" + text + "&from=" + from;
        if (size != null) {
            path += "&size=" + size;
        }
        return get(path, authHeader);
    }

    public ResponseEntity<Object> createComment(String authHeader, CommentDto commentDto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDto, authHeader);
    }
}