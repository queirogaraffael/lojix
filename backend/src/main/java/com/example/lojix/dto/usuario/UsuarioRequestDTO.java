package com.example.lojix.dto.usuario;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 5, max = 100, message = "O nome deve ter entre 5 e 100 caracteres.")
    private String name;

    private String fotoPerfilBase64;

    @NotBlank(message = "O nome de usuário é obrigatório.")
    @Size(min = 7, max = 50, message = "O nome de usuário deve ter entre 7 e 50 caracteres.")
    private String username;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "^\\d{11}$", message = "O CPF deve conter 11 dígitos (somente números).")
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail deve ser um endereço válido.")
    @Size(max = 100, message = "O e-mail não pode exceder 100 caracteres.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String password;
}