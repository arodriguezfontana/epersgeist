package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.MediumElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MediumElasticDAO extends ElasticsearchRepository<MediumElastic, Long> {
}
