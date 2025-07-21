package ar.edu.unq.epersgeist.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.Date;

@Getter
@Setter
@Document(indexName = "historial")
public class HistorialElastic {
    @Id
    private String id;
    private Long espirituId;
    private GeoPoint ubicacion;
    private Long ubicacionAsociadaId;
    private Date fecha;


    public HistorialElastic() {
    }

    public HistorialElastic(Long espirituId, GeoPoint ubicacion, Long idUbicacion) {
        this.espirituId = espirituId;
        this.ubicacionAsociadaId = idUbicacion;
        this.ubicacion = ubicacion;
        this.fecha = new Date();

    }

}