package co.edu.uniquindio.CRUD.excepciones;

public class LimiteEnvioAlcanzadoException extends RuntimeException {
    public LimiteEnvioAlcanzadoException(String message) {
        super(message);
    }
}
