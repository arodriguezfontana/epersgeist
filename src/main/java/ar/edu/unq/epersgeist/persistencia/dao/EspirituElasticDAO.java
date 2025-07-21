package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.EspirituElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EspirituElasticDAO extends ElasticsearchRepository<EspirituElastic, Long> {
}