package ar.edu.unq.epersgeist.modelo.excepcion;

public class InvocacionFallidaException extends RuntimeException {
    public InvocacionFallidaException() {

        super("El medium no tiene suficiente maná o la Ubicacion no es del tipo correcto");
    }
}
