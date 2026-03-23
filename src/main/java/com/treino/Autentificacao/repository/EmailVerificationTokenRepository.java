package com.treino.Autentificacao.repository;

import com.treino.Autentificacao.entity.EmailVerificationToken;
import com.treino.Autentificacao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByUser(User user);
}