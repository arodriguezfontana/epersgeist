package ar.edu.unq.epersgeist.persistencia.dao.impl;

import ar.edu.unq.epersgeist.modelo.UbicacionElastic;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionElasticDAOCustom;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class UbicacionElasticDAOImpl implements UbicacionElasticDAOCustom {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Override
    public List<UbicacionElastic> buscarPorDescripcionAvanzada(String criterio) {
        try {
            SearchResponse<UbicacionElastic> response = elasticsearchClient.search(s -> s
                            .index("ubicaciones")  // nombre real del índice
                            .query(q -> q
                                    .match(m -> m
                                            .field("descripcion")
                                            .query(criterio)
                                            .fuzziness("AUTO")          // permite errores de tipeo
                                            .operator(Operator.And)  // todas las palabras deben estar
                                            .boost(2.0f)                // más relevancia
                                    )
                            ),
                    UbicacionElastic.class
            );

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Error al buscar ubicaciones", e);
        }
    }

    @Override
    public List<UbicacionElastic> buscarPorDescripcionAvanzadaFuzzines(String criterio, String fuzzines) {
        try {
            SearchResponse<UbicacionElastic> response = elasticsearchClient.search(s -> s
                            .index("ubicaciones")  // nombre real del índice
                            .query(q -> q
                                    .match(m -> m
                                            .field("descripcion")
                                            .query(criterio)
                                            .fuzziness(fuzzines)          // permite errores de tipeo
                                            .operator(Operator.And)  // todas las palabras deben estar
                                            .boost(2.0f)                // más relevancia
                                    )
                            ),
                    UbicacionElastic.class
            );

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Error al buscar ubicaciones", e);
        }
    }
}




