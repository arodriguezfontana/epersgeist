package ar.edu.unq.epersgeist.modelo.excepcion;

public class NoSePuedeRealizarLaTeletransportacionException extends RuntimeException {
    public NoSePuedeRealizarLaTeletransportacionException() {
        super("No se puede realizar la teletransportacion del espiritu ya que este no se encuentra libre o su nivel de conexion es menor a 50.");
    }
}
