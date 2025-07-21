package ar.edu.unq.epersgeist.dao.Utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Conflicts;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class ElasticSearchCleaner {

    private final ElasticsearchClient esClient;

    public ElasticSearchCleaner(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public void cleanAllDocuments() {
        try {
            // Obtener los nombres de todos los índices
            GetIndexResponse response = esClient.indices().get(
                    new GetIndexRequest.Builder().index("*").build()
            );

            Set<String> indexNames = response.result().keySet();

            if (indexNames.isEmpty()) {
                System.out.println("ℹ No se encontraron índices para limpiar.");
                return;
            }

            for (String index : indexNames) {
                // Evitar borrar índices internos del sistema
                if (!index.startsWith(".") && !index.startsWith("kibana")) {
                    try {
                        DeleteByQueryRequest deleteRequest = DeleteByQueryRequest.of(d -> d
                                .index(index)
                                .query(MatchAllQuery.of(m -> m)._toQuery())
                                .conflicts(Conflicts.Proceed)
                        );

                        esClient.deleteByQuery(deleteRequest);
                        System.out.println("✔ Documentos eliminados del índice: " + index);
                    } catch (Exception ex) {
                        System.err.println("⚠ No se pudo eliminar documentos del índice: " + index);
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            // Manejo más suave del error
            System.err.println("⚠ No se pudo obtener los índices de Elasticsearch.");
            e.printStackTrace();
        }
    }
}