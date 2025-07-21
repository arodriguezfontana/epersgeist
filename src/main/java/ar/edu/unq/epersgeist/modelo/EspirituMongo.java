package ar.edu.unq.epersgeist.modelo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("EspirituMongo")
public class EspirituMongo {

    @Id
    private Long id;
    private GeoJsonPoint punto;

    public EspirituMongo(Long id, GeoJsonPoint punto) {
        this.id = id;
        this.punto = punto;

    }
}
