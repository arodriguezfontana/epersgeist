package ar.edu.unq.epersgeist.modelo.excepcion;

public class EspirituDominadoException extends RuntimeException {
    public EspirituDominadoException() {
        super("No se puede conectar al espiritu con el medium porque el espiritu esta siendo dominado.");
    }
}
