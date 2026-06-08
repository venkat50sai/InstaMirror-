package com.app.utility;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secret = "mySuperSecureLongSecretKeyThatIsAtLeast32Chars!";
    private final long expirationMs = 360000000;

    public String generateToken(Integer userId, String role) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Integer getUserId(String token) {
        return extractClaims(token).get("userId", Integer.class);
    }

    public String getRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

}
