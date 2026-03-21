package com.treino.Autentificacao.controller;

import com.treino.Autentificacao.dto.*;
import com.treino.Autentificacao.entity.User;
import com.treino.Autentificacao.service.AuthService;
import com.treino.Autentificacao.service.JwtService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private JwtService jwtService;

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public List<User> listar() {
        return service.listar();
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterDTO dto) {
        return service.register(dto);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginDTO dto) {
        return service.login(dto);
    }


    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam String refreshToken) {
        return service.refresh(refreshToken);
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public User me(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        return service.buscarPorEmail(email);
    }
}