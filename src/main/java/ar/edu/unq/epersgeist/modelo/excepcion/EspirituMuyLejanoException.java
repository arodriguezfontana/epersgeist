package ar.edu.unq.epersgeist.modelo.excepcion;

public class EspirituMuyLejanoException extends RuntimeException {
    public EspirituMuyLejanoException() {
        super("El espiritu se encuentra a mas de 50km de distancia del medium");
    }
}
