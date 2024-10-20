package co.edu.uniquindio.CRUD.dtos.usuario;

import jakarta.validation.constraints.NotBlank;

public record ActualizarUsuarioDTO(
        @NotBlank
        String codigo,
        @NotBlank
        String email,
        @NotBlank
        String nombre,
        @NotBlank
        String apellido
) {
}
