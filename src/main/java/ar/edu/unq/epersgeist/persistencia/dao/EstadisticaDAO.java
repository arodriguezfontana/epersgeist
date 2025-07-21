package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.TipoEspiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface EstadisticaDAO {
    Medium mediumConMasEspiritusDeTipoEn(TipoEspiritu tipoEspiritu, Long idUbicacion);
    int espiritusDeTipoEnUbicacion(TipoEspiritu tipoEspiritu,Long ubicacionId);
    List<Ubicacion> findTopSantuarioWithMoreDemoniosThanAngeles(Pageable pageable);
    int espiritusDeTipoLibres(TipoEspiritu tipoEspiritu, Long ubicacionId);
}
