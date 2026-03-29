package com.hexaware.cms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Secret key — keep this private, don't share it!
    private final String SECRET = "myCmsSecretKey12345678901234567890AbCdEfGhIjKlMnOpQrStUvWxYz";

    // Token is valid for 1 hour (in milliseconds)
    private final long EXPIRY = 1000 * 60 * 60;

    // Converts secret string to a Key object
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Creates a JWT token with username and role inside
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)                          // username goes here
                .claim("role", role)                           // role goes as custom claim
                .setIssuedAt(new Date())                       // token created now
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRY)) // expires in 1 hour
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // sign with our secret
                .compact();
    }

    // Reads all claims (data) from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Gets the username from the token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Gets the role from the token
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Checks if the token is expired
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Returns true if token is valid (username matches + not expired)
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }
}