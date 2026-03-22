package com.treino.Autentificacao.service;

import com.treino.Autentificacao.entity.PasswordResetToken;
import com.treino.Autentificacao.entity.User;
import com.treino.Autentificacao.repository.PasswordResetTokenRepository;
import com.treino.Autentificacao.repository.UserRepository;
import com.treino.Autentificacao.service.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void requestPasswordReset(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        PasswordResetToken resetToken = tokenRepository.findByUser(user)
                .orElse(null);

        if (resetToken == null) {
            resetToken = new PasswordResetToken();
            resetToken.setUser(user);
        }

        // 🔢 gera código de 6 dígitos
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        resetToken.setToken(code);
        resetToken.setExpirationDate(new Date(System.currentTimeMillis() + 15 * 60 * 1000));

        tokenRepository.save(resetToken);

        // 📧 email com código
        emailService.sendEmail(
                user.getEmail(),
                "Redefinição de senha",
                "Seu código de redefinição é: " + code +
                        "\n\nEsse código expira em 15 minutos."
        );
    }

    @Transactional
    public void resetPassword(String code, String newPassword) {

        PasswordResetToken resetToken = tokenRepository.findByToken(code)
                .orElseThrow(() -> new RuntimeException("Código inválido"));

        if (new Date().after(resetToken.getExpirationDate())) {
            throw new RuntimeException("Código expirado");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}