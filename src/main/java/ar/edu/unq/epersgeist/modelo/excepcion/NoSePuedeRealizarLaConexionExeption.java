package ar.edu.unq.epersgeist.modelo.excepcion;

public class NoSePuedeRealizarLaConexionExeption extends RuntimeException {
    public NoSePuedeRealizarLaConexionExeption() {
        super("No se puede conectar al espiritu con el medium porque no estan en la misma ubicacion o el epiritu se encuentra ya conectado.");
    }
}