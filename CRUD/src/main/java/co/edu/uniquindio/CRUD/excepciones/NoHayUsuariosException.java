package co.edu.uniquindio.CRUD.excepciones;

public class NoHayUsuariosException extends RuntimeException {
    public NoHayUsuariosException(String message) {
        super(message);
    }
}
