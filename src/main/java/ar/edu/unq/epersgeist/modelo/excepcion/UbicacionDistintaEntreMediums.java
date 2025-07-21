package ar.edu.unq.epersgeist.modelo.excepcion;

import ar.edu.unq.epersgeist.modelo.Medium;

public class UbicacionDistintaEntreMediums extends RuntimeException{
    public UbicacionDistintaEntreMediums(Medium medium1, Medium medium2){
        super("El medium " + medium1.getNombre() + " y el medium " + medium2.getNombre() + " no se encuentran en la misma ubicacion.");
    }
}
