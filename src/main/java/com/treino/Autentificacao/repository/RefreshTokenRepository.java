package com.treino.Autentificacao.repository;

import com.treino.Autentificacao.entity.RefreshToken;
import com.treino.Autentificacao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);
    void deleteByUser(User user);
}