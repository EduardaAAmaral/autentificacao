package com.treino.Autentificacao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private boolean enabled;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void setEnabled(boolean b) {
        this.enabled = enabled;

    }

    public boolean isEnabled() {
        return enabled;
    }
}

