package ar.edu.unq.epersgeist.persistencia.dao;


import ar.edu.unq.epersgeist.modelo.UbicacionNeo;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface UbicacionNeoDAO extends Neo4jRepository<UbicacionNeo, Long> {

    @Query("""
            RETURN EXISTS {
              MATCH (origen:UbicacionNeo {id: $idOrigen})-[:CONECTADA]->(destino:UbicacionNeo {id: $idDestino})
            } AS estaConectada
            """)
    Boolean ubicacionConectadaA(@Param("idOrigen") Long idOrigen, @Param("idDestino") Long idDestino);

    @Query("MATCH (u:UbicacionNeo {id: $id}) RETURN EXISTS {MATCH (u)-[:CONECTADA]->(:UbicacionNeo)} AS estaConectada")
    Boolean tieneUbicacionesConectadas(Long id);

    @Query("""
           MATCH (start:UbicacionNeo {id: $idOrigen}), (end:UbicacionNeo {id: $idDestino})
           MATCH path = shortestPath((start)-[:CONECTADA*]->(end))
           UNWIND nodes(path) AS nodo
           RETURN nodo.id AS id
           """)
    List<Long> caminoMasCorto(@Param("idOrigen") Long idOrigen, @Param("idDestino") Long idDestino);

    @Query("""
    CALL {
        WITH $idsNodos AS ids
        WITH ids, size(ids) AS cantidad
        UNWIND (CASE WHEN cantidad = 1 THEN [ids[0]] ELSE [] END) AS id
        RETURN 0.0 AS centrality, id
        UNION
        WITH $idsNodos AS ids
        UNWIND ids AS idOrigen
        WITH idOrigen, [x IN ids WHERE x <> idOrigen] AS otros
        MATCH (origen:UbicacionNeo {id: idOrigen})
        UNWIND otros AS idDestino
        MATCH (destino:UbicacionNeo {id: idDestino})
        OPTIONAL MATCH p = shortestPath((origen)-[*]-(destino))
        WITH idOrigen, collect(length(p)) AS distancias
        WITH idOrigen AS id, reduce(suma = 0, d IN distancias | suma + coalesce(d, 0)) AS suma_total
        RETURN CASE WHEN suma_total = 0 THEN 0.0 ELSE 1.0 / toFloat(suma_total) END AS centrality, id
    }
    RETURN centrality
    ORDER BY id ASC
""")
    List<Double> calcularClosenessPorIds(@Param("idsNodos") List<Long> idsNodos);

    @Query("""
           MATCH(u:UbicacionNeo)
           WHERE u.energia > $umbral
           RETURN u.id
           """)
    List<Long> ubicacionesSuperioresA(@Param("umbral") int umbral);

    @Query("""
    CALL {
        WITH $idsNodos AS ids
        WITH ids, size(ids) AS cantidad
        UNWIND (CASE WHEN cantidad = 1 THEN [ids[0]] ELSE [] END) AS id
        RETURN 0.0 AS centrality, id
        UNION
        WITH $idsNodos AS ids
        UNWIND ids AS idCentral
        WITH idCentral, [x IN ids WHERE x <> idCentral] AS destinos
        MATCH (origen:UbicacionNeo {id: idCentral})
        UNWIND destinos AS idDestino
        MATCH (destino:UbicacionNeo {id: idDestino})
        OPTIONAL MATCH p = shortestPath((origen)-[*]-(destino))
        WITH idCentral, collect(1.0 / toFloat(length(p))) AS invDistancias
        WITH idCentral AS id, reduce(suma = 0.0, d IN invDistancias | suma + coalesce(d, 0.0)) AS centrality
        RETURN centrality, id
    }
    RETURN centrality
    ORDER BY id ASC
""")
    List<Double> calcularHarmonicCentrality(@Param("idsNodos") List<Long> idsNodos);

}
