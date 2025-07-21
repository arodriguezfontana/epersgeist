package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.MediumMongo;
import java.util.List;
public record MediumMongoDTO(Long id, List<Double> coordenadas) {

    public static MediumMongoDTO desdeModelo(MediumMongo mediumMongo) {
        return new MediumMongoDTO(
                mediumMongo.getId(),
                mediumMongo.getPunto().getCoordinates()
        );
    }
}
