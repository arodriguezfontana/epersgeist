package ar.edu.unq.epersgeist.modelo;

import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;
@Getter
@Setter

public class HistorialTeletransportacion {

    //Atributos
    private String nombreEspiritu;
    private List<Long> idsDeUbicacionesPorLasQueAnduvo = new ArrayList<Long>();
    private int cantidadDeMovimientos;
    private Long ubicacionMasConcurrida;
    //Constructor
    public HistorialTeletransportacion(String nombreEspiritu,
                                       Long ubicacionMasConcurrida,
                                       List<Long> idsUbicaciones,
                                       int cantidadDeMovimientos
    ) {
        this.nombreEspiritu = nombreEspiritu;
        this.idsDeUbicacionesPorLasQueAnduvo = idsUbicaciones;
        this.ubicacionMasConcurrida = ubicacionMasConcurrida;
        this.cantidadDeMovimientos = cantidadDeMovimientos;
    }
    public HistorialTeletransportacion() {}


}
