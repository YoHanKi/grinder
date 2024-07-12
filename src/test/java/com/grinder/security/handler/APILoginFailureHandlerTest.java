package com.grinder.security.handler;

import com.google.gson.Gson;
import com.grinder.exception.UserRegistrationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class APILoginFailureHandlerTest {

    private APILoginFailureHandler loginFailureHandler;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        loginFailureHandler = new APILoginFailureHandler();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("BadCredentialsException handling")
    void handleBadCredentialsException() throws IOException, ServletException {
        AuthenticationException exception = new BadCredentialsException("Bad credentials");

        loginFailureHandler.onAuthenticationFailure(request, response, exception);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentType().contains("application/json"));
        String jsonResponse = response.getContentAsString();
        Gson gson = new Gson();
        Map<String, String> responseMap = gson.fromJson(jsonResponse, Map.class);
        assertEquals("로그인 실패", responseMap.get("code"));
        assertEquals("아이디 또는 비밀번호가 일치하지 않습니다.", responseMap.get("message"));
    }

    @Test
    @DisplayName("InternalAuthenticationServiceException handling")
    void handleInternalAuthenticationServiceException() throws IOException, ServletException {
        AuthenticationException exception = new InternalAuthenticationServiceException("Internal error");

        loginFailureHandler.onAuthenticationFailure(request, response, exception);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentType().contains("application/json"));
        String jsonResponse = response.getContentAsString();
        Gson gson = new Gson();
        Map<String, String> responseMap = gson.fromJson(jsonResponse, Map.class);
        assertEquals("계정 비활성화", responseMap.get("code"));
        assertEquals("계정이 비활성화되었습니다. 관리자에게 문의하세요.", responseMap.get("message"));
    }

    @Test
    @DisplayName("Other AuthenticationException handling")
    void handleOtherAuthenticationException() throws IOException, ServletException {
        AuthenticationException exception = mock(AuthenticationException.class);
        when(exception.getMessage()).thenReturn("Other error");

        loginFailureHandler.onAuthenticationFailure(request, response, exception);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentType().contains("application/json"));
        String jsonResponse = response.getContentAsString();
        Gson gson = new Gson();
        Map<String, String> responseMap = gson.fromJson(jsonResponse, Map.class);
        assertEquals("인증 실패", responseMap.get("code"));
        assertEquals("인증 과정에서 오류가 발생했습니다.", responseMap.get("message"));
    }
}