package com.treino.Autentificacao.service;

import com.treino.Autentificacao.dto.*;
import com.treino.Autentificacao.entity.EmailVerificationToken;
import com.treino.Autentificacao.entity.RefreshToken;
import com.treino.Autentificacao.entity.Role;
import com.treino.Autentificacao.entity.User;
import com.treino.Autentificacao.repository.EmailVerificationTokenRepository;
import com.treino.Autentificacao.repository.PasswordResetTokenRepository;
import com.treino.Autentificacao.repository.RefreshTokenRepository;
import com.treino.Autentificacao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    public AuthResponse register(RegisterDTO dto) {

        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);

        repository.save(user);

        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = refreshTokenService.create(user).getToken();

        String link = "http://localhost:8080/auth/verify-email?token=" + accessToken;

        emailService.sendEmail(
                user.getEmail(),
                "Verificação de email",
                "Clique no link para verificar sua conta:\n" + link
        );

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginDTO dto) {

        User user = repository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha inválida");
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = refreshTokenService.create(user).getToken();

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {

        RefreshToken token = refreshTokenService.validate(refreshToken);

        String email = token.getUser().getEmail();


        refreshTokenService.delete(refreshToken);


        String newAccessToken = jwtService.generateToken(email);
        String newRefreshToken = refreshTokenService.create(token.getUser()).getToken();

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    public List<User> listar() {
        return repository.findAll();
    }

    public User buscarPorEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        refreshTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.deleteByUser(user);
        emailVerificationTokenRepository.deleteByUser(user);

        userRepository.delete(user);
    }

    public void logout(String refreshToken) {
        refreshTokenService.delete(refreshToken);
    }

    public void sendVerificationEmail(User user) {

        emailVerificationTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setExpirationDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));

        emailVerificationTokenRepository.save(verificationToken);

        String link = "http://localhost:3000/verify-email?token=" + token;

        emailService.sendEmail(
                user.getEmail(),
                "Verifique seu email",
                "Clique no link:\n" + link
        );
    }

    public void verifyEmail(String token) {

        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (new Date().after(verificationToken.getExpirationDate())) {
            throw new RuntimeException("Token expirado");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);

        userRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);
    }

    public User updateUser(String currentEmail, UpdateUserDTO dto) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            User existingUser = userRepository.findByEmail(dto.getEmail()).orElse(null);

            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                throw new RuntimeException("Email já está em uso");
            }

            user.setEmail(dto.getEmail());
        }

        return userRepository.save(user);
    }

    public void deleteUser(String currentEmail) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        userRepository.delete(user);
    }
}