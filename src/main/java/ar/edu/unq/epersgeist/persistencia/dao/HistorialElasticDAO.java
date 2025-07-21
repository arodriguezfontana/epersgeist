package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.HistorialElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialElasticDAO extends ElasticsearchRepository<HistorialElastic, String >, HistorialElasticDAOCustom {
}
