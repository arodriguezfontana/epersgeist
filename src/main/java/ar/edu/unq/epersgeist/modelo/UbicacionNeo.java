package ar.edu.unq.epersgeist.modelo;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@ToString
@Setter
@Getter

@Node
public class UbicacionNeo {

    private String nombre;
    @Id
    private Long id;
    private int energia;

    @Relationship(type = "CONECTADA")
    private Set<UbicacionNeo> conectadas = new HashSet<>();

    public UbicacionNeo(Long id, String nombre, Integer energia) {
        this.id = id;
        this.nombre = nombre;
        this.energia = energia;
    }

    public void conectarse(UbicacionNeo uDestino) {
        this.conectadas.add(uDestino);
    }
}
