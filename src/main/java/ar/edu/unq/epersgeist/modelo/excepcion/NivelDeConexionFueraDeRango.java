package ar.edu.unq.epersgeist.modelo.excepcion;

public class NivelDeConexionFueraDeRango extends RuntimeException {
    public NivelDeConexionFueraDeRango() {

        super("El nivel de conexón debe ser un valor entre 0 y 100." );
    }
}
