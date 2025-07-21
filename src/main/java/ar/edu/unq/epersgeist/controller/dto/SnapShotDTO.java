package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record SnapShotDTO(List<EspirituSnapshotData> espiritus,
                          List<MediumSnapshotData> mediums,
                          List<UbicacionSnapshotData> ubicaciones,
                          List<UbicacionNeoSnapshotData> ubicacionesNeo,
                          List<EspirituMongoDTO> espiritusMongo,
                          List<MediumMongoDTO> mediumsMongo,
                          List<UbicacionMongoDTO>ubicacionesMongo,
                          LocalDate fecha) {

    public static SnapShotDTO desdeModelo(SnapShot snapShot) {
        return new SnapShotDTO(
                snapShot.getSql().getEspiritus(),

                snapShot.getSql().getMediums(),

                snapShot.getSql().getUbicaciones(),

                snapShot.getNeo4j().getUbicacionesNeo(),

                snapShot.getMongo().getEspiritusMongo().stream()
                .map(EspirituMongoDTO::desdeModelo)
                .collect(Collectors.toCollection(ArrayList::new)),

                snapShot.getMongo().getMediumsMongo().stream()
                        .map(MediumMongoDTO::desdeModelo)
                        .collect(Collectors.toCollection(ArrayList::new)),

                snapShot.getMongo().getUbicacionesMongo().stream()
                        .map(UbicacionMongoDTO::desdeModelo)
                        .collect(Collectors.toCollection(ArrayList::new)),

                snapShot.getFecha()

        );
    }
}
