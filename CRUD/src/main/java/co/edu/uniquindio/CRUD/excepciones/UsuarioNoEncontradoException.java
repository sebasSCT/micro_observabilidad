package co.edu.uniquindio.CRUD.excepciones;

public class UsuarioNoEncontradoException extends RuntimeException{
    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
}