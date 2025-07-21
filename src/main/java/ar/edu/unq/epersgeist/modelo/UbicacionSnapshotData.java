package ar.edu.unq.epersgeist.modelo;



import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbicacionSnapshotData {
    private Long id;
    private String nombre;
    private String coordinates;
    private List<Long> espiritus;
    private List<Long> mediums;
    private String tipoDeUbicacion;
    private Integer energia;

    public UbicacionSnapshotData(Ubicacion ubicacion) {
        this.id = ubicacion.getId();
        this.nombre = ubicacion.getNombre();
        this.espiritus = ubicacion.getEspiritusEnUbicacion().stream().map(e-> e.getId()).toList();
        this.mediums = ubicacion.getMediumsEnUbicacion().stream().map(m-> m.getId()).toList();
        this.tipoDeUbicacion = ubicacion.getTipoUbicacion().toString();
        this.energia = ubicacion.getEnergia();
    }
    public UbicacionSnapshotData() {}
}

