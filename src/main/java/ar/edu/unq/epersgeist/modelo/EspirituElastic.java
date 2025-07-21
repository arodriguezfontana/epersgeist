package ar.edu.unq.epersgeist.modelo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Getter @Setter

@Document(indexName = "espiritus")
public class EspirituElastic {
    @Id
    private Long id;
    private String nombre;
    private String descripcion;
    private GeoPoint ubicacion;

    public EspirituElastic() {}

    public EspirituElastic(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
