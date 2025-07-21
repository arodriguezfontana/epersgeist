package ar.edu.unq.epersgeist.modelo.excepcion;

public class PuntoNoPerteneceException extends RuntimeException {
    public PuntoNoPerteneceException() {
        super("El punto no pertenece a la ubicación.");
    }
}
