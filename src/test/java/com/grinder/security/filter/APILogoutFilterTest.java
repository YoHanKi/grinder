package com.grinder.security.filter;

import com.grinder.repository.RefreshRepository;
import com.grinder.security.exception.AccessTokenException;
import com.grinder.security.exception.RefreshTokenException;
import com.grinder.utils.JWTUtil;
import com.grinder.utils.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class APILogoutFilterTest {

    private JWTUtil jwtUtil;
    private RefreshRepository refreshRepository;
    private RedisUtil redisUtil;
    private APILogoutFilter logoutFilter;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JWTUtil.class);
        refreshRepository = mock(RefreshRepository.class);
        redisUtil = mock(RedisUtil.class);
        logoutFilter = new APILogoutFilter(jwtUtil, refreshRepository, redisUtil);
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void successfulLogout() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        request.setRequestURI("/api/logout");
        request.setMethod("POST");

        Cookie refreshCookie = new Cookie("refresh", "refresh-token-value");
        Cookie accessCookie = new Cookie("access", "access-token-value");
        request.setCookies(refreshCookie, accessCookie);

        when(refreshRepository.existsByRefresh(anyString())).thenReturn(true);

        logoutFilter.doFilter(request, response, filterChain);

        verify(refreshRepository, times(1)).deleteByRefresh(anyString());
        assertEquals(200, response.getStatus());
        assertEquals(0, response.getCookie("refresh").getMaxAge());
        assertEquals(0, response.getCookie("access").getMaxAge());
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰 테스트")
    void invalidRefreshToken() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        request.setRequestURI("/api/logout");
        request.setMethod("POST");

        Cookie refreshCookie = new Cookie("refresh", "invalid-refresh-token");
        request.setCookies(refreshCookie);

        when(refreshRepository.existsByRefresh(anyString())).thenReturn(false);

        assertThrows(RefreshTokenException.class, () -> {
            logoutFilter.doFilter(request, response, filterChain);
        });

        assertEquals(0, response.getCookie("refresh").getMaxAge());
        assertEquals(0, response.getCookie("access").getMaxAge());
    }

    @Test
    @DisplayName("유효하지 않은 액세스 토큰 테스트")
    void invalidAccessToken() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        request.setRequestURI("/api/logout");
        request.setMethod("POST");

        Cookie refreshCookie = new Cookie("refresh", "refresh-token-value");
        request.setCookies(refreshCookie);

        when(refreshRepository.existsByRefresh(anyString())).thenReturn(true);

        assertThrows(AccessTokenException.class, () -> {
            logoutFilter.doFilter(request, response, filterChain);
        });
    }
}