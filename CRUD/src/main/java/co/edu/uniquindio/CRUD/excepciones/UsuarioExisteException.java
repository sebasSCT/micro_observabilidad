package co.edu.uniquindio.CRUD.excepciones;

public class UsuarioExisteException extends RuntimeException {
    public UsuarioExisteException(String message) {
        super(message);
    }
}