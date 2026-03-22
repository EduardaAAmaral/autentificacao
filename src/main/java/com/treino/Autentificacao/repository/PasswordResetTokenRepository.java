package com.treino.Autentificacao.repository;

import com.treino.Autentificacao.entity.PasswordResetToken;
import com.treino.Autentificacao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);

    void deleteByUser(User User);


}
