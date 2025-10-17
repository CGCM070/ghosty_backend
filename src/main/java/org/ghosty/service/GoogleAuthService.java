package org.ghosty.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.ghosty.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final RestTemplate restTemplate;

    public GoogleAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Verify Google ID Token (JWT) from Google Sign-In
     * Este método verifica el token usando el endpoint público de Google
     */
    public Map<String, Object> verifyGoogleToken(String idToken) {
        try {
            // Usar el endpoint de tokeninfo de Google para verificar el token
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> tokenInfo = response.getBody();
                
                // Verificar que el token es para nuestra aplicación
                String audience = (String) tokenInfo.get("aud");
                if (!googleClientId.equals(audience)) {
                    throw new BadRequestException("El token no es para esta aplicación");
                }
                
                // Verificar que el token tiene la información necesaria
                if (!tokenInfo.containsKey("email") || !tokenInfo.containsKey("sub")) {
                    throw new BadRequestException("Token de Google no contiene información de usuario");
                }
                
                // Verificar que el email está verificado
                Object emailVerified = tokenInfo.get("email_verified");
                if (emailVerified == null || !(emailVerified instanceof Boolean) || !(Boolean) emailVerified) {
                    if (emailVerified instanceof String && !emailVerified.equals("true")) {
                        throw new BadRequestException("El email no está verificado en Google");
                    }
                }
                
                return tokenInfo;
            }
            throw new BadRequestException("Token de Google inválido");
        } catch (HttpClientErrorException e) {
            // Decodificar el JWT sin verificar solo para debug (NO USAR EN PROD sin verificar)
            String errorDetails = "";
            try {
                int firstDot = idToken.indexOf('.');
                int secondDot = idToken.indexOf('.', firstDot + 1);
                if (firstDot > 0 && secondDot > firstDot) {
                    Claims claims = Jwts.parserBuilder()
                        .build()
                        .parseClaimsJwt(idToken.substring(0, secondDot + 1))
                        .getBody();
                    errorDetails = " (aud: " + claims.getAudience() + ")";
                }
            } catch (Exception ignored) {}
            
            throw new BadRequestException("Error al verificar el token de Google: " + e.getMessage() + errorDetails);
        } catch (Exception e) {
            throw new BadRequestException("Error inesperado al verificar el token de Google: " + e.getMessage());
        }
    }
}
