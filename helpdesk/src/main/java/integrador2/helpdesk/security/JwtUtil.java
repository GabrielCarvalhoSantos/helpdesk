package integrador2.helpdesk.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key   key;
    private final long  expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-in-ms}") long exp) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = exp;
    }

    public String generate(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()            // ↙ cria o builder
                .setSigningKey(key)  // ↙ configura a chave
                .build()             // ↙ constrói o parser real
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
