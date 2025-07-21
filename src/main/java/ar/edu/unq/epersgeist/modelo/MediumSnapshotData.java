package ar.edu.unq.epersgeist.modelo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediumSnapshotData {

    private Long id;
    private Integer manaMax;
    private Integer mana;
    private Long idUbicacion;
    private List<Long> espiritus;

    public MediumSnapshotData(Medium medium) {
        this.id = medium.getId();
        this.manaMax = medium.getManaMax();
        this.espiritus = medium.getEspiritus().stream().map(e-> e.getId()).toList();
        this.idUbicacion = medium.getUbicacion().getId();
    }
    public MediumSnapshotData() {}
}

