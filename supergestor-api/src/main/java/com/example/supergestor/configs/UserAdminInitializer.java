package com.example.supergestor.configs;

import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.domain.enums.UserRole;
import com.example.supergestor.domain.repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserAdminInitializer implements CommandLineRunner {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.nome}")
    private String adminNome;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.cpf}")
    private String adminCpf;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAdminInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!usuarioRepository.existsByUsername(adminUsername)) {

            Usuario admin = new Usuario();
            admin.setName(adminNome);
            admin.setEmail(adminEmail);
            admin.setUsername(adminUsername);
            admin.setCpf(adminCpf);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(UserRole.ADMIN);

            usuarioRepository.save(admin);
            log.info("Usuário admin ({}) criado!", adminUsername);
        }
    }
}
