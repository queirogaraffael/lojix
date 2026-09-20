package com.example.lojix.util.builder;

import com.example.lojix.domain.entity.Cliente;
import com.example.lojix.domain.entity.Usuario;
import com.example.lojix.domain.enums.UserRole;

import java.time.LocalDate;

public class ClienteTestBuilder {
    private LocalDate membroDesde = LocalDate.now();
    private UsuarioTestBuilder usuarioBuilder = UsuarioTestBuilder.novo().comRole(UserRole.CLIENTE);

    public static ClienteTestBuilder novo() {
        return new ClienteTestBuilder();
    }

    public ClienteTestBuilder comMembroDesde(LocalDate membroDesde) { this.membroDesde = membroDesde; return this; }
    public ClienteTestBuilder comUsuarioBuilder(UsuarioTestBuilder usuarioBuilder) { this.usuarioBuilder = usuarioBuilder; return this; }

    public Cliente build() {
        Usuario usuario = usuarioBuilder.build();
        Cliente cliente = new Cliente();
        cliente.setMembroDesde(membroDesde);
        
        cliente.setUsuario(usuario);
        usuario.setCliente(cliente);
        
        return cliente;
    }
}
