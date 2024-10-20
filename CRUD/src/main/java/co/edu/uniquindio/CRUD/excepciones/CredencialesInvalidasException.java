package co.edu.uniquindio.CRUD.excepciones;

public class CredencialesInvalidasException extends RuntimeException{
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
