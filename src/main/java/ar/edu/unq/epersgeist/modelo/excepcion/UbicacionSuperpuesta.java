package ar.edu.unq.epersgeist.modelo.excepcion;

public class UbicacionSuperpuesta extends RuntimeException {
    public UbicacionSuperpuesta( ) {
        super("¡Cuidado! El área de la ubicación que querés crear se superpone con otra ya creada.");
    }
}
