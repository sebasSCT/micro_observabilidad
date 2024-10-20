package co.edu.uniquindio.CRUD.dtos.general;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {
}