package com.gdg.slbackend.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByOauthSubject(String oauthSubject);

    Optional<User> findByEmail(String email);

    Optional<User> findByOauthProviderAndOauthSubject(String oauthProvider, String oauthSubject);
}