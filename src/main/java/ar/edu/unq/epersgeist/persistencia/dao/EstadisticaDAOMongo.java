package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.*;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface EstadisticaDAOMongo extends MongoRepository<SnapShot,String> {

    @Aggregation(pipeline = {
            "{ $match: { 'fecha': ?0 } }",
            "{ $sort: { 'id': -1 } }",
            "{ $limit: 1 }"
    })
    SnapShot obtenerSnapshoot(LocalDate fecha);
}
