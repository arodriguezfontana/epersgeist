package ar.edu.unq.epersgeist.modelo.excepcion;

public class UbicacionesNoConectadasException extends RuntimeException {
    public UbicacionesNoConectadasException(String uOrigen, String uDestino) {
        super("No existe un camino que que vaya desde " + uOrigen + " a " + uDestino);
    }
}
