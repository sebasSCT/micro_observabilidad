package co.edu.uniquindio.CRUD.excepciones;

public class NoAutorizadoException extends RuntimeException{
    public NoAutorizadoException(String message) {
        super(message);
    }
}
