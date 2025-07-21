package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.UbicacionElastic;

import java.util.List;

public interface UbicacionElasticDAOCustom {
    List<UbicacionElastic> buscarPorDescripcionAvanzada(String criterio);
    List<UbicacionElastic> buscarPorDescripcionAvanzadaFuzzines(String criterio, String fuzzines);
}
