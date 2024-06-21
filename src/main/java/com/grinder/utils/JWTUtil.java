package com.grinder.utils;

import com.grinder.security.exception.AccessTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTUtil {

    @Value("${jwt.secret}")
    private String key;

    private final RedisUtil redisUtil;


    // 인증 완료시 토큰 발급하는 메소드
    public String generateToken(Map<String,Object> valueMap, int hours){

        // JWT 헤더
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ","JWT");
        headers.put("alg","HS256");

        // JWT 페이로드 (내용) : 인증 정보 반환)
        Map<String,Object> payloads = new HashMap<>();
        payloads.putAll(valueMap);

        int time = (60) * hours;

        String jwtStr = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();

        return jwtStr;
    }
    // 토큰 검증 기능
    public Map<String, Object> validateToken(String token) throws JwtException{
        Map<String, Object> claim = null;

        claim = Jwts.parserBuilder()
                .setSigningKey(key.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
        // blacklist에 있는 token(로그아웃한 token)인지 확인
//        if(redisUtil.hasKeyBlackList(token)){
//            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BLACKLISTED);
//        }
        return claim;
    }
    public String getEmail(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

}
