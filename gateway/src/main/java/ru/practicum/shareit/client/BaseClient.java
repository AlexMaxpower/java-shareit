package ru.practicum.shareit.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path, String authHeader) {
        return get(path, null, null, authHeader);
    }

    protected ResponseEntity<Object> get(String path, long userId, String authHeader) {
        return get(path, userId, null, authHeader);
    }

    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters,
                                         String authHeader) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null, authHeader);
    }

    protected <T> ResponseEntity<Object> post(String path, T body, String authHeader) {
        return post(path, null, null, body, authHeader);
    }

    protected <T> ResponseEntity<Object> post(String path, long userId, T body, String authHeader) {
        return post(path, userId, null, body, authHeader);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> parameters,
                                              T body, String authHeader) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body, authHeader);
    }

    protected <T> ResponseEntity<Object> put(String path, long userId, T body, String authHeader) {
        return put(path, userId, null, body, authHeader);
    }

    protected <T> ResponseEntity<Object> put(String path, long userId, @Nullable Map<String, Object> parameters,
                                             T body, String authHeader) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body, authHeader);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body, String authHeader) {
        return patch(path, null, null, body, authHeader);
    }

    protected <T> ResponseEntity<Object> patch(String path, long userId, String authHeader) {
        return patch(path, userId, null, null, authHeader);
    }

    protected <T> ResponseEntity<Object> patch(String path, long userId, T body, String authHeader) {
        return patch(path, userId, null, body, authHeader);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId,
                                               @Nullable Map<String, Object> parameters,
                                               T body, String authHeader) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body, authHeader);
    }

    protected ResponseEntity<Object> delete(String path, String authHeader) {
        return delete(path, null, null, authHeader);
    }

    protected ResponseEntity<Object> delete(String path, long userId, String authHeader) {
        return delete(path, userId, null, authHeader);
    }

    protected ResponseEntity<Object> delete(String path, Long userId,
                                            @Nullable Map<String, Object> parameters, String authHeader) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null, authHeader);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path, Long userId,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body,
                                                          String authHeader) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId, authHeader));

        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private HttpHeaders defaultHeaders(Long userId, String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authHeader);
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
