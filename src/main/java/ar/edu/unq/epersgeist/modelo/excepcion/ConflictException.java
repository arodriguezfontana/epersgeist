package ar.edu.unq.epersgeist.modelo.excepcion;

public class ConflictException extends RuntimeException {
    public ConflictException() {
        super("Ya existe una ubicación activa con ese nombre");
    }
}
