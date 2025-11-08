package com.gehtsoft.quiz;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {
    @GetMapping("/api/quiz/csrf")
    public ResponseEntity<String> getCsrf(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        if (token != null) {
            return ResponseEntity.ok(String.format(token.getToken()));
        }
        return ResponseEntity.notFound().build();
    }
}