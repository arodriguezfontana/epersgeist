package ar.edu.unq.epersgeist.persistencia.dao;


import ar.edu.unq.epersgeist.modelo.UbicacionElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface UbicacionElasticDAO extends ElasticsearchRepository<UbicacionElastic, Long>, UbicacionElasticDAOCustom {
    List<UbicacionElastic> findByDescripcionContainingIgnoreCase(String descripcion);
}
