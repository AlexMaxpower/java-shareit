package ru.practicum.shareit.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageService {
    @KafkaListener(topics = "gateway", groupId = "group_id")
    public void consume(String msg) {
        log.info(msg);
    }
}
