package com.grinder.security.filter;

import com.google.gson.Gson;
import com.grinder.repository.RefreshRepository;
import com.grinder.security.exception.AccessTokenException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final String refreshPath;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String path = request.getRequestURI();

        // 토큰 갱신 경로 아닐 경우
        if(!path.equals(refreshPath)){
            filterChain.doFilter(request,response);
            return;
        }
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if (cookie.getName().equals("refresh")) {

                refreshToken = cookie.getValue();
            }
        }

        Map<String,Object> refreshClaims = null;

        try{
            // accessToken은 무조건 새로 발행
            // refreshToken은 만료일이 얼마 남지 않은 경우에 새로 발행
            refreshClaims = checkRefreshToken(refreshToken, request, response);

            Integer exp = (Integer) refreshClaims.get("exp");

            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli()*1000);

            Date current = new Date(System.currentTimeMillis());

            long gapTime = (expTime.getTime()-current.getTime());

            String email = jwtUtil.getEmail(refreshToken);
            Map<String, Object> mid = Map.of("email", email);
            //이 상태 도달 시 무조건 accessToken은 새로 생성
            String accessToken = jwtUtil.generateToken(mid,1);
            //refreshToken이 3일도 안남았을때
            if(gapTime < (1000*60*60*24*3)){
                refreshToken = jwtUtil.generateToken(mid,24*7);
            }

            sendToken(accessToken,refreshToken,response);

        }catch (RefreshTokenException refreshTokenException){
            refreshTokenException.sendResponseError(response);
            return;
        }
    }


    // refreshToken 검사 -> 토큰이 없거나 잘못된 토큰인 경우 에러메세지 전송
    private Map<String,Object> checkRefreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response)throws RefreshTokenException{
        try{
            Map<String,Object> values = jwtUtil.validateToken(refreshToken);
            return values;
        }catch (ExpiredJwtException expiredJwtException){
            deleteCookie(refreshToken, request, response);
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
        }catch (MalformedJwtException malformedJwtException){
            log.error("MalformedJwtException------------------------");
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }catch (Exception exception){
            new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }
        return null;
    }

    //만들어진 토큰들 전송
    private void sendToken(String accessTokenValue, String refreshTokenValue,HttpServletResponse response){
        //accessToken은 로컬 스토리지 , refreshToken은 httpOnly 쿠키에 저장
        response.addCookie(createCookie("access",accessTokenValue));
        response.addCookie(createCookie("refresh",refreshTokenValue));
        response.setStatus(HttpStatus.OK.value());
    }
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    protected void deleteCookie(String refresh, HttpServletRequest request, HttpServletResponse response) {
        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshRepository.deleteByRefresh(refresh);

        // accessToken blacklist에 추가
        String accessToken = cutOffBearer(getAccess(request, response));
//        redisUtil.setBlackList(accessToken,"accessToken",60);

        //Refresh 토큰 Cookie 값 0
        Cookie reCookie = new Cookie("refresh", null);
        reCookie.setMaxAge(0);
        reCookie.setPath("/");

        Cookie acCookie = new Cookie("access", null);
        acCookie.setMaxAge(0);
        acCookie.setPath("/");

        response.addCookie(reCookie);
        response.addCookie(acCookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private String getAccess(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for(Cookie cookie : cookies){
            if (cookie.getName().equals("access")) {
                accessToken = "Bearer " + cookie.getValue();
                return accessToken;
            }
        }
        return null;
    }

    protected String cutOffBearer(String accessToken) {
        if(accessToken == null||accessToken.length() < 8){
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }
        String tokenStr = accessToken.substring(7);

        return tokenStr;
    }
}
