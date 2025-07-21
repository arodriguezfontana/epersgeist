package ar.edu.unq.epersgeist.persistencia.dao;


import ar.edu.unq.epersgeist.modelo.MediumMongo;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MediumDAOMongo extends MongoRepository<MediumMongo,Long> {



    @Aggregation(pipeline = {

            "{ $match: { _id: ?2 } }",


            "{ $project: { " +
                    "latDB: { $arrayElemAt: [ '$punto.coordinates', 1 ] }, " +
                    "lonDB: { $arrayElemAt: [ '$punto.coordinates', 0 ] } " +
                    "} }",


            "{ $project: { " +
                    "distancia: { $multiply: [ 111, { $sqrt: { $add: [ " +
                    "{ $pow: [ { $subtract: [ ?1, '$latDB' ] }, 2 ] }, " +
                    "{ $pow: [ { $subtract: [ ?0, '$lonDB' ] }, 2 ] } " +
                    "] } } ] } " +
                    "} }"
    })
    Double calcularDistanciaEntrePuntoYMedium(double lat, double lon, Long mediumId);


}
