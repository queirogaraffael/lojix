package com.example.supergestor.configs;

import com.example.supergestor.domain.entities.Funcionario;
import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.domain.enums.UserRole;
import com.example.supergestor.domain.repositories.FuncionarioRepository;
import com.example.supergestor.domain.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Configuration
public class UserAdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAdminInitializer(UsuarioRepository usuarioRepository, 
                                FuncionarioRepository funcionarioRepository,
                                PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String emailAdmin = "admin@supergestor.com";

        if (usuarioRepository.findByEmail(emailAdmin).isEmpty()) {
            Usuario adminUser = new Usuario();
            adminUser.setName("Administrador Principal");
            adminUser.setEmail(emailAdmin);
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setCpf("00000000000");
            adminUser.setRole(UserRole.ADMIN);

            Usuario savedUser = usuarioRepository.save(adminUser);

            Funcionario adminFuncionario = new Funcionario();
            adminFuncionario.setUsuario(savedUser);
            adminFuncionario.setCargo("CEO");
            adminFuncionario.setSalario(new BigDecimal("50000.00"));
            adminFuncionario.setAtivo(true); // <--- CORRIGIDO AQUI (era setStatus)

            funcionarioRepository.save(adminFuncionario);
        }
    }
}