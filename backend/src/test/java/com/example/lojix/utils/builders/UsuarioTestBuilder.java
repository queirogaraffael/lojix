package com.example.lojix.utils.builders;

import com.example.lojix.domain.entities.Usuario;
import com.example.lojix.domain.enums.UserRole;
import com.example.lojix.shared.utils.CpfGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class UsuarioTestBuilder {
    private String name = "Test User";
    private String username = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
    private String email = username + "@lojix.com";
    private String cpf = CpfGenerator.generate();
    private String rawPassword = "password123";
    private UserRole role = UserRole.CLIENTE;
    private PasswordEncoder passwordEncoder;

    public static UsuarioTestBuilder novo() {
        return new UsuarioTestBuilder();
    }

    public UsuarioTestBuilder comName(String name) { this.name = name; return this; }
    public UsuarioTestBuilder comUsername(String username) { this.username = username; return this; }
    public UsuarioTestBuilder comEmail(String email) { this.email = email; return this; }
    public UsuarioTestBuilder comCpf(String cpf) { this.cpf = cpf; return this; }
    public UsuarioTestBuilder comRole(UserRole role) { this.role = role; return this; }
    public UsuarioTestBuilder comRawPassword(String rawPassword) { this.rawPassword = rawPassword; return this; }
    public UsuarioTestBuilder comPasswordEncoder(PasswordEncoder passwordEncoder) { this.passwordEncoder = passwordEncoder; return this; }

    public String getRawPassword() { return this.rawPassword; }

    public Usuario build() {
        Usuario usuario = new Usuario();
        usuario.setName(name);
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setCpf(cpf);
        if (passwordEncoder != null) {
            usuario.setPassword(passwordEncoder.encode(rawPassword));
        } else {
            usuario.setPassword(rawPassword);
        }
        usuario.setRole(role);
        return usuario;
    }
}
