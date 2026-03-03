package com.beedigital.educenter.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JwtUtil - Utilitaire pour générer et valider les JWT
 *
 * VERSION: 4.0 - JJWT 0.12.3 COMPATIBLE
 *
 * TOKENS:
 * - Access Token: 1h (pour les requêtes API)
 * - Refresh Token: 7j (pour obtenir nouveau access token)
 *
 * @author Équipe Développement
 * @version 4.0
 */
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * Obtenir la clé secrète
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * GÉNÉRER ACCESS TOKEN - 1h
     */
    public String generateAccessToken(com.beedigital.educenter.entity.User user) {
        long now = System.currentTimeMillis();
        long expiryTime = now + accessTokenExpiration;

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("username", user.getUsername())
                .claim("role", user.getRole().getCode().toString())
                .claim("userId", user.getId())
                .claim("tokenType", "ACCESS")
                .issuedAt(new Date(now))
                .expiration(new Date(expiryTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * GÉNÉRER REFRESH TOKEN - 7j
     */
    public String generateRefreshToken(com.beedigital.educenter.entity.User user) {
        long now = System.currentTimeMillis();
        long expiryTime = now + refreshTokenExpiration;

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("tokenType", "REFRESH")
                .issuedAt(new Date(now))
                .expiration(new Date(expiryTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * VALIDER TOKEN
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Token invalide: " + e.getMessage());
            return false;
        }
    }

    /**
     * EXTRAIRE EMAIL DU TOKEN
     */
    public String extractEmail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            System.out.println("❌ Erreur extraction email: " + e.getMessage());
            return null;
        }
    }

    /**
     * EXTRAIRE RÔLE DU TOKEN
     */
    public String extractRole(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return (String) claims.get("role");
        } catch (Exception e) {
            System.out.println("❌ Erreur extraction rôle: " + e.getMessage());
            return null;
        }
    }

    /**
     * EXTRAIRE USER ID DU TOKEN
     */
    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return ((Number) claims.get("userId")).longValue();
        } catch (Exception e) {
            System.out.println("❌ Erreur extraction userId: " + e.getMessage());
            return null;
        }
    }

    /**
     * EXTRAIRE TYPE DE TOKEN
     */
    public String extractTokenType(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return (String) claims.get("tokenType");
        } catch (Exception e) {
            System.out.println("❌ Erreur extraction tokenType: " + e.getMessage());
            return null;
        }
    }

    /**
     * OBTENIR TEMPS D'EXPIRATION RESTANT
     */
    public long getExpirationTime(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            long expiration = claims.getExpiration().getTime();
            long now = System.currentTimeMillis();

            return (expiration - now) / 1000; // Retourner en secondes
        } catch (Exception e) {
            System.out.println("❌ Erreur extraction expiration: " + e.getMessage());
            return 0;
        }
    }
}