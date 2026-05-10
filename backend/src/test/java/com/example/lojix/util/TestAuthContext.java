package com.example.lojix.util;

import com.example.lojix.domain.entity.Usuario;

public record TestAuthContext(String token, String username, String rawPassword, Usuario usuario) {
}
