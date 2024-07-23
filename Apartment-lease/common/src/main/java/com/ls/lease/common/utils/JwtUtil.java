package com.ls.lease.common.utils;

import com.ls.lease.common.exception.LeaseException;
import com.ls.lease.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static long tokenExpiration = 60 * 60 * 1000L;
    private static SecretKey secretKey = Keys.hmacShaKeyFor("M0PKKI6pYGVWWfDZw90a0lTpGYX1d4AQ".getBytes());

    public static String createToken(Long userId, String username) {
        String token = Jwts.builder().
                setSubject("USER_INFO").
                setExpiration(new Date(System.currentTimeMillis() + tokenExpiration)).
                claim("userId", userId).
                claim("username", username).
                signWith(secretKey, SignatureAlgorithm.HS256).
                compact();
        return token;
    }

    /**
     * 校验token，并返回token的pload值
     *
     * @param token
     * @return
     */
    public static Claims parseToken(String token){
        if (token ==null){
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }
        try{
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build();
             return jwtParser.parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e){
            throw  new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
        }catch (JwtException e){
            throw  new LeaseException(ResultCodeEnum.TOKEN_INVALID);

        }
    }
    /**
     * 因为配置了拦截器，校验token，测试不方便，所以生成一个长期的token方便接口测试，
     */
    public static void main(String[] args) {
        System.out.println(createToken(8L,"user"));
    }

}
