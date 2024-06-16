package project.tikklemoa_back.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.tikklemoa_back.config.jwt.JwtProperties;
import project.tikklemoa_back.entity.UserEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class TokenProvider {
    @Autowired
    private JwtProperties jwtProperties;

    public String create(UserEntity user) {
        Date expiredDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                .setSubject(String.valueOf(user.getId()))
                .setIssuer(jwtProperties.getIssure())
                .setExpiration(expiredDate)
                .setIssuedAt(new Date())
                .compact();
    }

    public String validateAndGetUserId(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
