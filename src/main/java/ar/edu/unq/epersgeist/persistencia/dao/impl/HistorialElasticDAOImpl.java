package ar.edu.unq.epersgeist.persistencia.dao.impl;


import ar.edu.unq.epersgeist.controller.dto.HistorialElasticDTO;
import ar.edu.unq.epersgeist.persistencia.dao.HistorialElasticDAOCustom;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class HistorialElasticDAOImpl implements HistorialElasticDAOCustom {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Override
    public Long idDeUbicacionMasConcurridaPor(Long idEspiritu) {
        try {
            SearchResponse<HistorialElasticDTO> response = elasticsearchClient.search(s -> s
                            .index("historial")
                            .query(q -> q
                                    .term(t -> t
                                            .field("espirituId")
                                            .value(idEspiritu)
                                    )
                            )
                            .size(1000),
                    HistorialElasticDTO.class
            );

            Map<Long, Long> conteo = response.hits().hits().stream()
                    .map(hit -> hit.source().getUbicacionAsociadaId())
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(
                            id -> id,
                            Collectors.counting()
                    ));

            return conteo.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

        } catch (IOException e) {
            throw new RuntimeException("Error al comunicarse con Elasticsearch", e);
        }
    }

    @Override
    public List<Long> ubicacionesPorLasQuePaso(Long espirituId) {
        try {
            SearchResponse<HistorialElasticDTO> response = elasticsearchClient.search(s -> s
                            .index("historial")
                            .query(q -> q
                                    .term(t -> t
                                            .field("espirituId")
                                            .value(espirituId)
                                    )
                            )
                            .size(1000),
                    HistorialElasticDTO.class
            );

            return response.hits().hits().stream()
                    .map(hit -> hit.source().getUbicacionAsociadaId())
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Error consultando ubicaciones del espíritu", e);
        }
    }

    @Override
    public int cantidadDeMovimientosDe(Long idEspiritu) {
        try {
            SearchResponse<HistorialElasticDTO> response = elasticsearchClient.search(s -> s
                            .index("historial")
                            .query(q -> q
                                    .term(t -> t
                                            .field("espirituId")
                                            .value(idEspiritu)
                                    )
                            )
                            .size(0),
                    HistorialElasticDTO.class
            );

            return (int) response.hits().total().value();

        } catch (IOException e) {
            throw new RuntimeException("Error al contar movimientos del espíritu", e);
        }
    }
}

