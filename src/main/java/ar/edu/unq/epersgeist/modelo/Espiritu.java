package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.excepcion.CondicionesDeDominioInsuficientes;
import ar.edu.unq.epersgeist.modelo.excepcion.EspirituNoPuedeSerInvocado;
import ar.edu.unq.epersgeist.modelo.excepcion.NivelDeConexionFueraDeRango;
import ar.edu.unq.epersgeist.modelo.excepcion.NoSePuedeRealizarLaTeletransportacionException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor

@Entity
@SQLDelete(sql = "UPDATE espiritu SET deleted_at = now() WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@EntityListeners(AuditingEntityListener.class)
public class Espiritu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 10)
    private TipoEspiritu tipo;
    @Min(0)
    @Max(100)
    private Integer nivelDeConexion;
    private String nombre;
    @ManyToOne
    private Medium medium;
    @ManyToOne
    private Ubicacion ubicacion;

    @ManyToMany
    @JoinTable(
            name = "espiritu_amo",
            joinColumns = @JoinColumn(name = "espiritu_id"),
            inverseJoinColumns = @JoinColumn(name = "amo_id")
    )
    private Set<Espiritu> amos = new HashSet<>();

    @ManyToMany(mappedBy = "amos")
    private Set<Espiritu> espiritusBajoControl = new HashSet<>();

    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updateAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Espiritu(@NonNull TipoEspiritu tipo, @NonNull Integer nivelDeConexion,
                    @NonNull String nombre, @NonNull Ubicacion ubicacion) {
        this.tipo = tipo;
        this.nivelDeConexion = ValidadorDeRangos.validarRango(
                nivelDeConexion, 0, 100, new NivelDeConexionFueraDeRango());
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        ubicacion.agregarEspiritu(this);
    }

    public boolean estaLibre() {
        return this.medium == null;
    }

    public void descansar() {
        int valorTotalConexion = this.getNivelDeConexion() + this.getUbicacion().getEnergia();
        if(valorTotalConexion > 100) {
            this.setNivelDeConexion(valorTotalConexion);
        } else {
            this.setNivelDeConexion(ValidadorDeRangos.validarRango(valorTotalConexion,
                    0, 100, new NivelDeConexionFueraDeRango()));
        }
    }

    public void invocar(Ubicacion ubicacion){
        if (this.estaLibre()){
            setUbicacion(ubicacion);
        }else {
            throw new EspirituNoPuedeSerInvocado(this.getNombre());
        }
    }

    public void atacar(Espiritu espiritu) {
        GeneradorDeNumerosAleatorios generador = GeneradorDeNumerosAleatorios
                .getGeneradorDeNumerosAleatorios();
        int poderAtaque = Math.min(generador
                .numeroAleatorioEntre(10) + this.nivelDeConexion, 100);
        int poderDefensa = generador.numeroAleatorioEntre(100);
        if (this.esAtaqueEfectivo(poderAtaque, poderDefensa)) {
            espiritu.setNivelDeConexion(espiritu
                    .getNivelDeConexion() - (this.getNivelDeConexion() / 2));
        } else {
            this.setNivelDeConexion(this.getNivelDeConexion() - 5);
        }
    }

    public void desvincularSiNoHayConexion(Medium medium) {
        if (nivelDeConexion == 0) {
            this.setMedium(null);
            medium.desvicularseDe(this);
        }
    }

    private boolean esAtaqueEfectivo(int poderAtaque, int poderDefensa) {
        return poderAtaque > poderDefensa;
    }

    public void setNivelDeConexion (Integer nivelDeConexion) {
        this.nivelDeConexion = Math.max(0, Math.min(nivelDeConexion, 100));
    }

    public void dominar(Espiritu eADominar, boolean cumpleCondiciones) {
        if(cumpleCondiciones){
            this.espiritusBajoControl.add(eADominar);
            eADominar.getAmos().add(this);
        }else {
            throw new CondicionesDeDominioInsuficientes();
        }

    }

    public Espiritu telestransportarA(Ubicacion ubicacion) {
        if(this.estaLibre() && this.nivelDeConexion >= 50) {
            this.nivelDeConexion -= 5;
            this.ubicacion = ubicacion;
            return this;
        } else {
            throw new NoSePuedeRealizarLaTeletransportacionException();
        }

    }
}