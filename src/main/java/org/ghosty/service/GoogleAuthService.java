package org.ghosty.service;

import org.ghosty.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@Service
public class GoogleAuthService {

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri:https://www.googleapis.com/oauth2/v3/userinfo}")
    private String googleUserInfoUri;

    private final RestTemplate restTemplate;

    public GoogleAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Verify Google token and extract user information using Spring OAuth2
     */
    public Map<String, Object> verifyGoogleToken(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    googleUserInfoUri,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new BadRequestException("Token de Google inválido");
            }
        } catch (HttpClientErrorException e) {
            throw new BadRequestException("Error al verificar el token de Google: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Error al verificar el token de Google: " + e.getMessage());
        }
    }
}
