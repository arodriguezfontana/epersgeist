package ar.edu.unq.epersgeist.modelo.excepcion;

public class NivelDeEnergiaFueraDeRango extends RuntimeException {

    public NivelDeEnergiaFueraDeRango() {
        super("El nivel de energia debe ser un valor entre 0 y 100." );
    }
}