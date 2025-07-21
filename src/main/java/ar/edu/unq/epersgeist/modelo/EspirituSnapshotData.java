package ar.edu.unq.epersgeist.modelo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class EspirituSnapshotData {

    private Long id;
    private String tipo;
    private String nombre;
    private Integer nivelDeConexion;
    private Long mediumId; //Se evita referencia Circular
    private Long ubicacionId;//Se evita referencia Circular


    public EspirituSnapshotData(Espiritu espiritu) {
        this.id = espiritu.getId();
        this.nombre = espiritu.getNombre();
        this.tipo = espiritu.getTipo().toString();
        this.nivelDeConexion = espiritu.getNivelDeConexion();
        this.mediumId = espiritu.getMedium() != null ? espiritu.getMedium().getId() : null;
        this.ubicacionId = espiritu.getUbicacion().getId();
    }
    public EspirituSnapshotData() {}
}
