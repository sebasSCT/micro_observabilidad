package co.edu.uniquindio.CRUD.dtos.usuario;

import jakarta.validation.constraints.NotBlank;

public record RegistroUsuarioDTO (
        @NotBlank
        String email,
        @NotBlank
        String password,
        @NotBlank
        String nombre,
        @NotBlank
        String apellido){
}
