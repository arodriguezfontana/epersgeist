package ar.edu.unq.epersgeist.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.geo.GeoJsonPolygon;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Getter @Setter

@Document(indexName = "ubicaciones")

@JsonIgnoreProperties(ignoreUnknown = true) //IMPORTANTE, Es necesario porq la query
                                            // intenta mapear campos que el doc elastic
                                            // tiene y la clase no
public class UbicacionElastic {

    @Id
    private Long id;
    private String nombre;
    private String descripcion;
    private GeoPoint area;

    public UbicacionElastic() {
    }

    public UbicacionElastic(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;

    }

}
