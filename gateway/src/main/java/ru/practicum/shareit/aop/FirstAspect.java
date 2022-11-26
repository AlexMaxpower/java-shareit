package ru.practicum.shareit.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.kafka.Sender;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class FirstAspect {

    private final Sender sender;

    @Pointcut("@within(org.springframework.stereotype.Controller)")
    public void isControllerLayer() {
    }

    @Before("isControllerLayer() && args(.., request, authentication)")
    public void addLogging(HttpServletRequest request, Authentication authentication) {
        sender.sendMessage(request, authentication);
    }

}
