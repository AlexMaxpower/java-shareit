package ru.practicum.shareit.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {
    @GetMapping("/login")
    public String login() {
        return "login work";
    }

    @GetMapping("/internal")
    public String internal() {
        return "internal work";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('admin')")
    public String add(@AuthenticationPrincipal Jwt jwt) {
        System.out.println(jwt.toString());
        log.info("realm_access = {}", jwt.getClaim("realm_access").toString());
        return "add work";
    }

    @GetMapping("/view")
    @PreAuthorize("hasRole('user')")
    public String view(@AuthenticationPrincipal Jwt jwt) {
        System.out.println(jwt.toString());
        log.info("UserName = {}", jwt.getClaim("name").toString());
        return "view work";
    }
}