package com.example.lojix.domain.entity;

import com.example.lojix.domain.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String name;

    @Column(name = "foto_key", length = 300)
    private String fotoKey;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    @NotNull
    private String password;

    @NotNull
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @OneToOne(mappedBy = "usuario")
    @ToString.Exclude
    private Funcionario funcionario;

    @OneToOne(mappedBy = "usuario")
    @ToString.Exclude
    private Cliente cliente;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UserRole.ADMIN)
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_ATENDENTE"),
                    new SimpleGrantedAuthority("ROLE_ESTOQUISTA"));
        else if(this.role == UserRole.ATENDENTE)
            return List.of(new SimpleGrantedAuthority("ROLE_ATENDENTE"));
        else if(this.role == UserRole.ESTOQUISTA)
            return List.of(new SimpleGrantedAuthority("ROLE_ESTOQUISTA"));
        else {
            return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
        }
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
