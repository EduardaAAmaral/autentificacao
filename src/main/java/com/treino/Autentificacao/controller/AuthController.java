package com.treino.Autentificacao.controller;

import com.treino.Autentificacao.dto.*;
import com.treino.Autentificacao.entity.User;
import com.treino.Autentificacao.service.AuthService;
import com.treino.Autentificacao.service.JwtService;
import com.treino.Autentificacao.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private RefreshTokenService refreshTokenService;

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

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        return service.buscarPorEmail(email);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO dto) {
        passwordResetService.requestPasswordReset(dto.getEmail());
        return ResponseEntity.ok("Email enviado!");
    }


    @GetMapping("/reset-password")
    public ResponseEntity<?> validarToken(@RequestParam String token) {
        return ResponseEntity.ok("Token válido");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO dto) {
        passwordResetService.resetPassword(dto.getCode(), dto.getNewPassword());
        return ResponseEntity.ok("Senha alterada com sucesso");
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {

        service.verifyEmail(token);

        return ResponseEntity.ok("Email verificado com sucesso!");
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            refreshTokenService.delete(token);
        }

        return ResponseEntity.ok("logout com successo"); // 204
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        passwordResetService.changePassword(
                request.get("currentPassword"),
                request.get("newPassword")
        );
        return ResponseEntity.ok("Senha alterada com sucesso");
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/me")
    public ResponseEntity<User> updateMe(
            HttpServletRequest request,
            @RequestBody UpdateUserDTO dto) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        User updatedUser = service.updateUser(email, dto);

        return ResponseEntity.ok(updatedUser);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMe(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        service.deleteUser(email);

        return ResponseEntity.ok("Usuario deletado com sucesso");
    }
}