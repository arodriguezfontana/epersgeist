package ar.edu.unq.epersgeist.modelo;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter @Setter

@Document(indexName = "mediums")
public class MediumElastic {
    @Id
    private Long id;
    private String nombre;
    private String descripcion;
    

    public MediumElastic() {}

    public MediumElastic(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
