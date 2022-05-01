package com.management.project.eshopbackend.repository;

import com.management.project.eshopbackend.models.users.AuthToken;
import com.management.project.eshopbackend.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthTokenJPARepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findAuthTokenByUser(User user);

    Optional<AuthToken> findAuthTokenByToken(String token);
}
