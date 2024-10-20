package co.edu.uniquindio.CRUD.dtos.autenticacionJwt;

import jakarta.validation.constraints.NotBlank;
public record TokenDTO (
        @NotBlank
        String token
){
}