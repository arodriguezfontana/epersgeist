package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.UbicacionMongo;

public record UbicacionMongoDTO(Long id, AreaDTO coordenadas) {

    public static UbicacionMongoDTO desdeModelo(UbicacionMongo ubicacionMongo) {
        return new UbicacionMongoDTO(
                ubicacionMongo.getId(),
                ubicacionMongo.getCoordenadas() != null ?
                        AreaDTO.desdeModelo(ubicacionMongo.getCoordenadas()) : null

        );
    }

}
