package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.UbicacionMongo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface UbicacionDAOMongo extends MongoRepository<UbicacionMongo, Long> {


    @Query(
            value = "{ 'coordenadas': { $geoIntersects: { $geometry: ?0 } } }"
    )
    List<UbicacionMongo> findAreaIdsQueContienenPunto(GeoJsonPoint punto);


    @Query(
            value = "{ 'id': ?1, 'coordenadas': { $geoIntersects: { $geometry: ?0 } } }", exists = true
    )
    Boolean puntoPerteneceA(GeoJsonPoint punto, Long ubicacionId);

    @Query(value = "{ 'coordenadas': { $geoIntersects: { $geometry: ?0 } } }", count = true)
    long areasSuperpuestas(GeoJsonPolygon poligono);

    @Query(value = "{ 'coordenadas': { $geoIntersects: { $geometry: ?0 } } }", exists = true)
    boolean existeAreaSuperpuesta(GeoJsonPolygon poligono);

    @Query("{ 'coordenadas.coordinates.0': { $elemMatch: { $in: ?0 } } }")
    List<UbicacionMongo> buscarAreasConVerticesSuperpuestos(List<List<Double>> puntosComoArrays);
}
