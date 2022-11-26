package ru.practicum.shareit.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class Sender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(HttpServletRequest request, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String msg = request.getRemoteAddr() + ": " + request.getRemoteUser() +
                " - " + request.getMethod() + " " + request.getRequestURI() + " " +
                jwt.getSubject() + " " + jwt.getClaim("preferred_username").toString();
        kafkaTemplate.send("gateway", msg);
    }
}

