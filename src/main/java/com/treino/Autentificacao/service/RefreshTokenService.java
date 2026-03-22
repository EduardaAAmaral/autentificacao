package com.treino.Autentificacao.service;

import com.treino.Autentificacao.entity.RefreshToken;
import com.treino.Autentificacao.entity.User;
import com.treino.Autentificacao.service.JwtService;
import com.treino.Autentificacao.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class RefreshTokenService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository repository;

    public RefreshToken create(User user) {
        System.out.println(user.getClass());
        String tokenJwt = jwtService.generateRefreshToken(user.getEmail());
        RefreshToken token = new RefreshToken();
        token.setToken(tokenJwt);
        token.setUser(user);
        token.setExpiration(new Date(System.currentTimeMillis() + 604800000));
        return repository.save(token); }

    public RefreshToken validate(String token) {
        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado"));
        if (refreshToken.getExpiration().before(new Date())) {
            throw new RuntimeException("Refresh token expirado"); }
        return refreshToken; } public void delete(String token) {
        repository.findByToken(token) .ifPresent(repository::delete); } }