package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.EspirituMongo;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface EspirituDAOMongo extends MongoRepository<EspirituMongo,Long> {

    @Aggregation(pipeline = {
            "{ $match: { $or: [ { _id: ?0 }, { _id: ?1 } ] } }",
            "{ $project: { " +
                    "id: 1, " +
                    "lat: { $arrayElemAt: [ '$punto.coordinates', 1 ] }, " +
                    "lon: { $arrayElemAt: [ '$punto.coordinates', 0 ] } " +
                    "} }",
            "{ $group: { " +
                    "_id: null, " +
                    "coords: { $push: { lat: '$lat', lon: '$lon' } } " +
                    "} }",
            "{ $project: { " +
                    "distancia: { $multiply: [ 111, { $sqrt: { $add: [ " +
                    "{ $pow: [ { $subtract: [ { $arrayElemAt: [ '$coords.lat', 0 ] }, { $arrayElemAt: [ '$coords.lat', 1 ] } ] }, 2 ] }, " +
                    "{ $pow: [ { $subtract: [ { $arrayElemAt: [ '$coords.lon', 0 ] }, { $arrayElemAt: [ '$coords.lon', 1 ] } ] }, 2 ] } " +
                    "] } } ] } " +
                    "} }"
    })
    Double calcularDistanciaEntreEspiritus(Long id1, Long id2);
}
