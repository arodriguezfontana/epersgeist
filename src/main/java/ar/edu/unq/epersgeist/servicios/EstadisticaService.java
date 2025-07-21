package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.ReporteSantuarioMasCorrupto;
import ar.edu.unq.epersgeist.modelo.SnapShot;

import java.time.LocalDate;


public interface EstadisticaService {
    ReporteSantuarioMasCorrupto santuarioCorrupto();
    void crearSnapshot();
    SnapShot obtenerSnapshot(LocalDate fecha);
}
