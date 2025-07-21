package ar.edu.unq.epersgeist.modelo.excepcion;

public class EspirituNoPuedeSerInvocado extends RuntimeException {
    public EspirituNoPuedeSerInvocado(String nombre) {

        super(nombre + " no está libre para ser invocado.");
    }
}
