package com.example.lojix.util;

import com.example.lojix.domain.entity.Cliente;
import com.example.lojix.domain.entity.Funcionario;
import com.example.lojix.domain.entity.Usuario;
import com.example.lojix.domain.enums.UserRole;
import com.example.lojix.infrastructure.security.TokenService;
import com.example.lojix.util.builder.ClienteTestBuilder;
import com.example.lojix.util.builder.FuncionarioTestBuilder;
import com.example.lojix.util.builder.UsuarioTestBuilder;
import com.example.lojix.infrastructure.repository.ClienteRepository;
import com.example.lojix.infrastructure.repository.FuncionarioRepository;
import com.example.lojix.infrastructure.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthTestFactory {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public TestAuthContext authenticateAsAdmin() {
        UsuarioTestBuilder builder = UsuarioTestBuilder.novo()
            .comRole(UserRole.ADMIN)
            .comPasswordEncoder(passwordEncoder);

        String rawPassword = builder.getRawPassword();

        Usuario admin = builder.build();
        usuarioRepository.save(admin);

        return createAuthContext(admin, rawPassword);
    }

    public TestAuthContext authenticateAsFuncionario() {
        UsuarioTestBuilder userBuilder = UsuarioTestBuilder.novo()
            .comRole(UserRole.FUNCIONARIO)
            .comPasswordEncoder(passwordEncoder);

        String rawPassword = userBuilder.getRawPassword();

        Funcionario funcionario = FuncionarioTestBuilder.novo()
            .comUsuarioBuilder(userBuilder)
            .build();
        funcionarioRepository.save(funcionario);
        return createAuthContext(funcionario.getUsuario(), rawPassword);
    }

    public TestAuthContext authenticateAsCliente() {
        UsuarioTestBuilder userBuilder = UsuarioTestBuilder.novo()
            .comRole(UserRole.CLIENTE)
            .comPasswordEncoder(passwordEncoder);

        String rawPassword = userBuilder.getRawPassword();

        Cliente cliente = ClienteTestBuilder.novo()
            .comUsuarioBuilder(userBuilder)
            .build();
        clienteRepository.save(cliente);

        return createAuthContext(cliente.getUsuario(), rawPassword);
    }

    public TestAuthContext authenticateAs(UserRole role) {
        if (role == UserRole.ADMIN) {
            return authenticateAsAdmin();
        } else if (role == UserRole.FUNCIONARIO) {
            return authenticateAsFuncionario();
        } else if (role == UserRole.CLIENTE) {
            return authenticateAsCliente();
        }
        throw new IllegalArgumentException("Role não suportada.");
    }

    private TestAuthContext createAuthContext(Usuario usuario, String rawPassword) {
        String token = tokenService.generateToken(usuario);
        return new TestAuthContext(token, usuario.getUsername(), rawPassword, usuario);
    }
}
