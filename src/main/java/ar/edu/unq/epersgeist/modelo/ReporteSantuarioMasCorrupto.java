package ar.edu.unq.epersgeist.modelo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ReporteSantuarioMasCorrupto {

    private String nombreSantuario;
    private Medium mediumConMasDemonios;
    private int cantidadDeDemonios;
    private int cantidadDeDemoniosLibres;

    public  ReporteSantuarioMasCorrupto(Ubicacion santuario,int
                                                    demoniosEnSantuario,
                                                    Medium mediumConMasDemonios,
                                                    int demoniosLibresEnSantuario) {
        this.setNombreSantuario(santuario.getNombre());
        this.setMediumConMasDemonios(mediumConMasDemonios);
        this.setCantidadDeDemonios(demoniosEnSantuario);
        this.setCantidadDeDemoniosLibres(demoniosLibresEnSantuario);

    }

}
