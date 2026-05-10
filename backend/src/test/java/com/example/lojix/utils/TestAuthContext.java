package com.example.lojix.utils;

import com.example.lojix.domain.entities.Usuario;

public record TestAuthContext(String token, String username, String rawPassword, Usuario usuario) {
}
