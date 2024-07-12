package com.grinder.security.filter;

import com.grinder.repository.RefreshRepository;
import com.grinder.security.exception.RefreshTokenException;
import com.grinder.utils.JWTUtil;
import com.grinder.utils.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RefreshTokenFilterTest {

    private JWTUtil jwtUtil;
    private RefreshRepository refreshRepository;
    private RedisUtil redisUtil;
    private RefreshTokenFilter refreshTokenFilter;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JWTUtil.class);
        refreshRepository = mock(RefreshRepository.class);
        redisUtil = mock(RedisUtil.class);
        refreshTokenFilter = Mockito.spy(new RefreshTokenFilter("/api/refresh", jwtUtil, refreshRepository, redisUtil));
    }

    @Test
    @DisplayName("유효한 리프레시 토큰으로 토큰 갱신 성공")
    void validRefreshTokenShouldRefreshTokens() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        request.setRequestURI("/api/refresh");
        request.setMethod("POST");

        Cookie refreshCookie = new Cookie("refresh", "valid-refresh-token");
        request.setCookies(refreshCookie);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "test@example.com");
        claims.put("exp", (int) (System.currentTimeMillis() / 1000 + 3600));
        when(jwtUtil.validateToken(anyString())).thenReturn(claims);
        when(jwtUtil.getEmail(anyString())).thenReturn("test@example.com");
        when(jwtUtil.generateToken(anyMap(), anyInt())).thenReturn("new-access-token");

        refreshTokenFilter.doFilterInternal(request, response, filterChain);

        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("리프레시 토큰 없음 테스트")
    void noRefreshTokenShouldThrowException() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        Cookie accessCookie = new Cookie("access", "valid-access-token");
        request.setCookies(accessCookie);

        request.setRequestURI("/api/refresh");
        request.setMethod("POST");
    }

    @Test
    @DisplayName("만료된 리프레시 토큰 테스트")
    void expiredRefreshTokenShouldThrowException() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setRequestURI("/api/refresh");
        request.setMethod("POST");

        Cookie refreshCookie = new Cookie("refresh", "expired-refresh-token");
        request.setCookies(refreshCookie);
        doReturn("access-token").when(refreshTokenFilter).cutOffBearer(any());

        // Mock jwtUtil.validateToken to throw ExpiredJwtException
        doThrow(new ExpiredJwtException(null, null, "Token expired")).when(jwtUtil).validateToken(anyString());
    }

    @Test
    @DisplayName("변형된 리프레시 토큰 테스트")
    void malformedRefreshTokenShouldThrowException() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setRequestURI("/api/refresh");
        request.setMethod("POST");

        Cookie refreshCookie = new Cookie("refresh", "malformed-refresh-token");
        request.setCookies(refreshCookie);

        doThrow(new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH)).when(jwtUtil).validateToken(anyString());
    }

    @Test
    @DisplayName("존재하지 않는 리프레시 토큰 테스트")
    void nonExistingRefreshTokenShouldThrowException() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setRequestURI("/api/refresh");
        request.setMethod("POST");

        Cookie refreshCookie = new Cookie("refresh", "non-existing-refresh-token");
        request.setCookies(refreshCookie);

        when(jwtUtil.validateToken(anyString())).thenReturn(new HashMap<>());
        when(refreshRepository.existsByRefresh(anyString())).thenReturn(false);
    }
}