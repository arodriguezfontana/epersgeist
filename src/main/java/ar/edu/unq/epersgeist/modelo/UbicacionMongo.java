package ar.edu.unq.epersgeist.modelo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter

@Document("UbicacionMongo")
public class UbicacionMongo {

    @Id
    private Long id;

    private GeoJsonPolygon coordenadas;

    public UbicacionMongo(Long id, GeoJsonPolygon coordenadas) {
        this.id = id;
        this.coordenadas = coordenadas;
    }
}
