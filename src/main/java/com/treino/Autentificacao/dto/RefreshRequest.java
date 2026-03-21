package com.treino.Autentificacao.dto;

public class RefreshRequest {

    private String refreshToken;


    public RefreshRequest() {
    }

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}