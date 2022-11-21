package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(String authHeader, Long userId, BookingState state, Integer from, Integer size) {
        String path = "?state=" + state.name() + "&from=" + from;
        if (size != null) {
            path += "&size=" + size;
        }
        return get(path, userId, authHeader);
    }

    public ResponseEntity<Object> getBookingsOwner(String authHeader, Long userId, BookingState state, Integer from, Integer size) {
        String path = "/owner?state=" + state.name() + "&from=" + from;
        if (size != null) {
            path += "&size=" + size;
        }
        return get(path, userId, authHeader);
    }


    public ResponseEntity<Object> create(String authHeader, Long userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto, authHeader);
    }

    public ResponseEntity<Object> getBooking(String authHeader, Long userId, Long bookingId) {
        return get("/" + bookingId, userId, authHeader);
    }

    public ResponseEntity<Object> update(String authHeader, Long bookingId, Long userId, Boolean approved) {
        String path = "/" + bookingId + "?approved=" + approved;
        return patch(path, userId, null, authHeader);
    }
}
