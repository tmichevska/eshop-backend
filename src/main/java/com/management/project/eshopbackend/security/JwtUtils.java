package com.management.project.eshopbackend.security;

import com.management.project.eshopbackend.service.impl.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token.validity}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date()).setExpiration(
                new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            System.out.printf("Invalid JWT signature: %s ", e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.printf("Invalid JWT token: %s ", e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.printf("JWT token is expired: %s ", e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.printf("JWT token is unsupported: %s ", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.printf("JWT claims string is empty: %s ", e.getMessage());
        }

        return false;
    }
}
