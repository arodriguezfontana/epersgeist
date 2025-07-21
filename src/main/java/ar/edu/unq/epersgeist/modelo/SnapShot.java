package ar.edu.unq.epersgeist.modelo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter

@Document("SnapShot") // Specifies the MongoDB collection name
public class SnapShot {

    @Id
    private String id;

    private SnapshotData sql;
    private SnapshotData neo4j;
    private SnapshotData mongo;

    private LocalDate fecha;

    @Getter
    @Setter
    public static class SnapshotData {
        private List<EspirituSnapshotData> espiritus;
        private List<MediumSnapshotData> mediums;
        private List<UbicacionSnapshotData> ubicaciones;
        private List<UbicacionNeoSnapshotData> ubicacionesNeo;
        private List<EspirituMongo> espiritusMongo;
        private List<MediumMongo> mediumsMongo;
        private List<UbicacionMongo> ubicacionesMongo;

    }
    public SnapShot(List<EspirituSnapshotData> espiritus,
                    List<MediumSnapshotData> mediums,
                    List<UbicacionSnapshotData> ubicaciones,
                    List<UbicacionNeoSnapshotData> ubicacionesNeo,
                    List<EspirituMongo> espiritusMongo,
                    List<MediumMongo> mediumsMongo,
                    List<UbicacionMongo> ubicacionesMongo,
                    LocalDate fechaActual) {

        this.sql = new SnapshotData();
        this.sql.setEspiritus(espiritus);
        this.sql.setMediums(mediums);
        this.sql.setUbicaciones(ubicaciones);

        this.neo4j = new SnapshotData();
        this.neo4j.setUbicacionesNeo(ubicacionesNeo);

        this.mongo = new SnapshotData();
        this.mongo.setEspiritusMongo(espiritusMongo);
        this.mongo.setMediumsMongo(mediumsMongo);
        this.mongo.setUbicacionesMongo(ubicacionesMongo);

        this.fecha = fechaActual;
    }

    public SnapShot() {}
}