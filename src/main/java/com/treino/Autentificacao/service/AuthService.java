package com.treino.Autentificacao.service;

import com.treino.Autentificacao.dto.*;
import com.treino.Autentificacao.entity.RefreshToken;
import com.treino.Autentificacao.entity.Role;
import com.treino.Autentificacao.entity.User;
import com.treino.Autentificacao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
}