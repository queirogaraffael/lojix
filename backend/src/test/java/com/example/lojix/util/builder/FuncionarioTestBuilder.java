package com.example.lojix.util.builder;

import com.example.lojix.domain.entity.Funcionario;
import com.example.lojix.domain.entity.Usuario;
import com.example.lojix.domain.enums.UserRole;

import java.math.BigDecimal;

public class FuncionarioTestBuilder {
    private String cargo = "Cargo Teste";
    private BigDecimal salario = BigDecimal.valueOf(2500.00);
    private UsuarioTestBuilder usuarioBuilder = UsuarioTestBuilder.novo().comRole(UserRole.FUNCIONARIO);

    public static FuncionarioTestBuilder novo() {
        return new FuncionarioTestBuilder();
    }

    public FuncionarioTestBuilder comCargo(String cargo) { this.cargo = cargo; return this; }
    public FuncionarioTestBuilder comSalario(BigDecimal salario) { this.salario = salario; return this; }
    public FuncionarioTestBuilder comUsuarioBuilder(UsuarioTestBuilder usuarioBuilder) { this.usuarioBuilder = usuarioBuilder; return this; }

    public Funcionario build() {
        Usuario usuario = usuarioBuilder.build();
        Funcionario funcionario = new Funcionario();
        funcionario.setCargo(cargo);
        funcionario.setSalario(salario);
        
        funcionario.setUsuario(usuario);
        usuario.setFuncionario(funcionario);
        
        return funcionario;
    }
}
