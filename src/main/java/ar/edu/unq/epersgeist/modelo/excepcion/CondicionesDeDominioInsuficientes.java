package ar.edu.unq.epersgeist.modelo.excepcion;

public class CondicionesDeDominioInsuficientes extends RuntimeException {
    public CondicionesDeDominioInsuficientes() {
        super("Al menos una de las condiciones de domino no se cumple");
    }
}
