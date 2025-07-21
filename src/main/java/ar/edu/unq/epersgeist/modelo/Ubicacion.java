
package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.excepcion.NivelDeEnergiaFueraDeRango;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.*;

@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @ToString

@Entity
@Table(
        name = "ubicacion",
        uniqueConstraints = @UniqueConstraint(columnNames = {"nombre", "deleted_at"})
)
@SQLDelete(sql = "UPDATE ubicacion SET deleted_at = now() WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@EntityListeners(AuditingEntityListener.class)
public class Ubicacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String nombre;
    @OneToMany(mappedBy = "ubicacion", fetch = FetchType.LAZY)
    private Set<Espiritu> espiritusEnUbicacion = new HashSet<>();
    @OneToMany(mappedBy = "ubicacion", fetch = FetchType.LAZY)
    private Set<Medium> mediumsEnUbicacion = new HashSet<>();
    private TipoUbicacion tipoUbicacion;
    @Min(0)
    @Max(100)
    private Integer energia;

    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updateAt;
    @Column(name = "deleted_at")
    private Date deletedAt;

    public Ubicacion(@NonNull String nombre, @NonNull Integer energia, TipoUbicacion tipoUbicacion) {
        this.nombre = nombre;
        this.energia = ValidadorDeRangos.validarRango(energia, 0, 100,
                new NivelDeEnergiaFueraDeRango());
        this.tipoUbicacion = tipoUbicacion;
    }

    public void setEnergia(Integer energia){
        this.energia = Math.max(0, Math.min(energia, 100));
    }

    public void agregarEspiritu(Espiritu espiritu) {
        this.espiritusEnUbicacion.add(espiritu);
    }

    public void agregarMedium(Medium medium) {
        this.mediumsEnUbicacion.add(medium);
    }

    public boolean tipoEspirituAdmitido(Espiritu espiritu) {
        return this.tipoUbicacion.getTipoAsociado().equals(espiritu.getTipo());
    }
}