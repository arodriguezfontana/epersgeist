package ar.edu.unq.epersgeist.modelo.excepcion;

public class UbicacionLejanaException extends RuntimeException {
    public UbicacionLejanaException(String ubicacion) {
        super(
                "La ubicacion " + ubicacion + " se encuentra demasiado lejos como para moverse allí."
        );
    }
}
