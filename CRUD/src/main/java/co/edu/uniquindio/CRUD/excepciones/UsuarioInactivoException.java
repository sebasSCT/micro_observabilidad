package co.edu.uniquindio.CRUD.excepciones;

public class UsuarioInactivoException extends RuntimeException{
    public UsuarioInactivoException(String message) {
        super(message);
    }
}