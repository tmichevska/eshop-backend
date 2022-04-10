package com.management.project.eshopbackend.service.intef;


import com.management.project.eshopbackend.models.users.AuthToken;

public interface AuthTokenService {
    AuthToken createAuthToken(Long userId, String type);

    AuthToken updateAuthToken(AuthToken authToken);

    boolean validateToken(String token);

    AuthToken findByUserId(Long userId);

    AuthToken findByToken(String token);
}
