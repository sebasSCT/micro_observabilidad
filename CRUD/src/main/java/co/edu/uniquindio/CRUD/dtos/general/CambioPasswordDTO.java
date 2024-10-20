package co.edu.uniquindio.CRUD.dtos.general;

import jakarta.validation.constraints.NotBlank;

public record CambioPasswordDTO (
                    @NotBlank
                    String nuevaPassword){
}
