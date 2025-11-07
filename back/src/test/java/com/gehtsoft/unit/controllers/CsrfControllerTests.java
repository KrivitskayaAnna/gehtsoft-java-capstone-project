package com.gehtsoft.unit.controllers;

import com.gehtsoft.quiz.CsrfController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsrfControllerTests {
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CsrfController csrfController;

    @Test
    void returnTokenIfExists() {
        CsrfToken csrfToken = mock(CsrfToken.class);
        String expectedToken = "csrf-token-1";

        when(request.getAttribute("_csrf")).thenReturn(csrfToken);
        when(csrfToken.getToken()).thenReturn(expectedToken);

        ResponseEntity<String> response = csrfController.getCsrf(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedToken);
    }

    @Test
    void returnNotFoundIfNoToken() {
        when(request.getAttribute("_csrf")).thenReturn(null);

        ResponseEntity<String> response = csrfController.getCsrf(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void returnEmptyStringIfTokenEmpty() {
        CsrfToken csrfToken = mock(CsrfToken.class);
        when(request.getAttribute("_csrf")).thenReturn(csrfToken);
        when(csrfToken.getToken()).thenReturn("");

        ResponseEntity<String> response = csrfController.getCsrf(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void returnTokenWithSpecialChars() {
        CsrfToken csrfToken = mock(CsrfToken.class);
        String specialToken = "csrf-token-1,./ !@#$123--_";
        when(request.getAttribute("_csrf")).thenReturn(csrfToken);
        when(csrfToken.getToken()).thenReturn(specialToken);

        ResponseEntity<String> response = csrfController.getCsrf(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(specialToken);
    }

    @Test
    void returnLongToken() {
        CsrfToken csrfToken = mock(CsrfToken.class);
        String longToken = "a".repeat(999_999_999); //out of memory for longer
        when(request.getAttribute("_csrf")).thenReturn(csrfToken);
        when(csrfToken.getToken()).thenReturn(longToken);

        ResponseEntity<String> response = csrfController.getCsrf(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(longToken);
        assertThat(response.getBody()).hasSize(999_999_999);
    }

    @Test
    void returnExceptionIfGetAttributeThrowsException() {
        when(request.getAttribute("_csrf")).thenThrow(new RuntimeException("Cannot get _csrf"));

        try {
            csrfController.getCsrf(request);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Cannot get _csrf");
        }
    }

    @Test
    void returnExceptionIfGetTokenThrowsException() {
        CsrfToken csrfToken = mock(CsrfToken.class);
        when(request.getAttribute("_csrf")).thenReturn(csrfToken);
        when(csrfToken.getToken()).thenThrow(new RuntimeException("Cannot get token"));

        try {
            csrfController.getCsrf(request);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Cannot get token");
        }
    }

    @Test
    void returnSameTokenForSeveralRequests() {
        CsrfToken csrfToken = mock(CsrfToken.class);
        String tokenValue = "atoken";
        when(request.getAttribute("_csrf")).thenReturn(csrfToken);
        when(csrfToken.getToken()).thenReturn(tokenValue);

        ResponseEntity<String> response1 = csrfController.getCsrf(request);
        ResponseEntity<String> response2 = csrfController.getCsrf(request);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response1.getBody()).isEqualTo(tokenValue);
        assertThat(response2.getBody()).isEqualTo(tokenValue);
    }
}