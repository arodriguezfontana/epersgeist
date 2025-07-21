package ar.edu.unq.epersgeist.persistencia.dao;

import java.util.List;
public interface HistorialElasticDAOCustom {

    Long idDeUbicacionMasConcurridaPor(Long idEspiritu);
    List<Long> ubicacionesPorLasQuePaso(Long idEspiritu);
    int cantidadDeMovimientosDe(Long idEspiritu);
}