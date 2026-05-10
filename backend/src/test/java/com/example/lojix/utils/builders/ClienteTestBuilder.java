package com.example.lojix.utils.builders;

import com.example.lojix.domain.entities.Cliente;
import com.example.lojix.domain.entities.Usuario;
import com.example.lojix.domain.enums.UserRole;

import java.time.LocalDate;

public class ClienteTestBuilder {
    private LocalDate tempoFidelidade = LocalDate.now();
    private UsuarioTestBuilder usuarioBuilder = UsuarioTestBuilder.novo().comRole(UserRole.CLIENTE);

    public static ClienteTestBuilder novo() {
        return new ClienteTestBuilder();
    }

    public ClienteTestBuilder comTempoFidelidade(LocalDate tempoFidelidade) { this.tempoFidelidade = tempoFidelidade; return this; }
    public ClienteTestBuilder comUsuarioBuilder(UsuarioTestBuilder usuarioBuilder) { this.usuarioBuilder = usuarioBuilder; return this; }

    public Cliente build() {
        Usuario usuario = usuarioBuilder.build();
        Cliente cliente = new Cliente();
        cliente.setTempoFidelidade(tempoFidelidade);
        
        cliente.setUsuario(usuario);
        usuario.setCliente(cliente);
        
        return cliente;
    }
}
