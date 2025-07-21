package ar.edu.unq.epersgeist.modelo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbicacionNeoSnapshotData {

    private Long id;
    private String nombre;
    private List<Long> conectadas;

    public UbicacionNeoSnapshotData(UbicacionNeo ubicacionNeo) {
        this.id = ubicacionNeo.getId();
        this.nombre = ubicacionNeo.getNombre();
        this.conectadas = ubicacionNeo.getConectadas().stream().map(u-> u.getId()).toList();
    }
    public UbicacionNeoSnapshotData() {}
}

