package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.EspirituMongo;
import java.util.List;
public record EspirituMongoDTO(Long id, List<Double> coordenadas) {

    public static EspirituMongoDTO desdeModelo(EspirituMongo espirituMongo) {
        return new EspirituMongoDTO(
                espirituMongo.getId(),
                espirituMongo.getPunto().getCoordinates()
        );
    }
}
