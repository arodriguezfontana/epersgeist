package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.ReporteSantuarioMasCorrupto;


public record ReporteDTO(String santuarioMasCorrupto, long idMediumConMasDemonios,
                         int demoniosTotales, int demoniosLibres) {

    public static ReporteDTO desdeModelo(ReporteSantuarioMasCorrupto reporte){
        return new ReporteDTO(
                reporte.getNombreSantuario(),
                reporte.getMediumConMasDemonios() != null ?
                        reporte.getMediumConMasDemonios().getId() : -1,
                reporte.getCantidadDeDemonios(),
                reporte.getCantidadDeDemoniosLibres()
        );
    }
}
