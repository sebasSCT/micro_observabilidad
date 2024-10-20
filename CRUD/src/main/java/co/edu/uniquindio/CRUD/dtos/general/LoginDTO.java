package co.edu.uniquindio.CRUD.dtos.general;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record LoginDTO(
        @NotNull
        @Length(max=50)
        @Email
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String email,
        @NotBlank
        String password) {
}
