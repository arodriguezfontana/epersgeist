package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.excepcion.*;
import jakarta.persistence.*;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static ar.edu.unq.epersgeist.modelo.TipoEspiritu.ANGEL;
import static ar.edu.unq.epersgeist.modelo.TipoEspiritu.DEMONIO;
import static ar.edu.unq.epersgeist.modelo.TipoUbicacion.CEMENTERIO;
import static ar.edu.unq.epersgeist.modelo.TipoUbicacion.SANTUARIO;

@Getter
@Setter
@NoArgsConstructor


@Entity
@SQLDelete(sql = "UPDATE medium SET deleted_at = now() WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@EntityListeners(AuditingEntityListener.class)
public class Medium implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 10)
    private String nombre;
    @Min(0)
    private Integer manaMax;
    @Min(0)
    private Integer mana;
    @OneToMany(mappedBy = "medium", fetch = FetchType.LAZY)
    private Set<Espiritu> espiritus = new HashSet<>();
    @ManyToOne
    private Ubicacion ubicacion;

    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updateAt;
    @Column(name = "deleted_at")
    private Date deletedAt;

    public Medium(@NonNull String nombre, @NonNull Integer manaMax,
                  @NonNull Integer mana, @NonNull Ubicacion ubicacion) {
        this.nombre = nombre;
        this.manaMax = ValidadorDeRangos.validarMinimo(manaMax, 0,
                new ManaFueraDeRangoException());
        this.mana = ValidadorDeRangos.validarRango(mana, 0, manaMax,
                new ManaFueraDeRangoException());
        this.ubicacion = ubicacion;
        ubicacion.agregarMedium(this);
    }

    private void verificarUbicacionEntre(Medium medium1, Medium medium2){
        if(medium1.getUbicacion().getId() != medium2.getUbicacion().getId()){
            throw new UbicacionDistintaEntreMediums(medium1, medium2);
        }
    }

    public void verificarDominio(Espiritu espiritu, Boolean estaDominado){
        if (!estaDominado) {
            this.conectarseAEspiritu(espiritu);
        } else {
            throw new EspirituDominadoException();
        }
    }

    public void conectarseAEspiritu(Espiritu espiritu) {
        if (this.estaEnLaMismaUbicacionQueEspiritu(espiritu) && espiritu.estaLibre()) {
            this.espiritus.add(espiritu);
            espiritu.setMedium(this);
            espiritu.setNivelDeConexion(espiritu.getNivelDeConexion() + this.manaSegunPorcentaje(20));
        } else {
            throw new NoSePuedeRealizarLaConexionExeption();
        }
    }

    public boolean estaEnLaMismaUbicacionQueEspiritu(Espiritu espiritu) {
        return (espiritu.getUbicacion().getId()).equals(this.ubicacion.getId());
    }

    public int manaSegunPorcentaje(int porcentaje){
        return (this.getMana() * porcentaje) / 100;
    }

    public void descansar(List<Espiritu> listaEspiritus) {
        if (this.getUbicacion().getTipoUbicacion().equals(CEMENTERIO)) {
            this.setearManaMaxSiSeSupera(this.recuperarEnergia(0.5));
        }else{
            this.setearManaMaxSiSeSupera(this.recuperarEnergia(1.5));
        }
        listaEspiritus.forEach(Espiritu::descansar);
    }

    public void setManaMax(int manaMax) {
        this.manaMax = Math.max(0, manaMax);
    }

    public void setMana(int mana) {
        this.mana = Math.max(0, Math.min(mana, this.manaMax));
    }

    private void setearManaMaxSiSeSupera(Integer valorASumar) {
        Integer valorASetear = this.getMana() + valorASumar;
        if(valorASetear > this.manaMax) {
            this.setMana(this.manaMax);
        } else {
            this.setMana(valorASetear);
        }
    }

    private Integer recuperarEnergia(Double d) {
        double energia = this.getUbicacion().getEnergia();
        return (int) (energia * d);
    }

    public boolean puedeConsumirMana(int mana){
        return this.getMana() >= mana;
    }
    public void invocarCerca(Double distanciaLimitite, Double distancia, Espiritu espiritu) {
        if(this.distanciaMenorOIgualA(distancia, distanciaLimitite)){
            this.invocar(espiritu);
        } else {
            throw new EspirituMuyLejanoException();
        }
    }

    public void invocar( Espiritu espiritu) {
        if (this.puedeConsumirMana(10) && this.getUbicacion().tipoEspirituAdmitido(espiritu)) {
            this.setMana(this.getMana() - 10);
            espiritu.invocar(this.getUbicacion());
        } else {
            throw new InvocacionFallidaException();
        }
    }

    public void exorcizar(Medium mAExorcizar, List<Espiritu> angelesDelExorcista,
                                              List<Espiritu> demoniosDelAExorcizar) {
        this.verificarUbicacionEntre(this, mAExorcizar);
        if (!angelesDelExorcista.isEmpty()) {
            this.atacarConAngeles(mAExorcizar, angelesDelExorcista, demoniosDelAExorcizar);
        } else {
            throw new ExorcistaSinAngelesException(this, mAExorcizar);
        }
    }

    private void atacarConAngeles(Medium mAExorcizar, List<Espiritu> angelesDelExorcista,
                                                      List<Espiritu> demoniosDelAExorcizar) {
        while (!demoniosDelAExorcizar.isEmpty() && !angelesDelExorcista.isEmpty()) {
            Espiritu angelActual = angelesDelExorcista.getFirst();
            Espiritu demonioActual = demoniosDelAExorcizar.getFirst();
            angelActual.atacar(demonioActual);
            demonioActual.desvincularSiNoHayConexion(mAExorcizar);
            if (demonioActual.estaLibre()) {
                demoniosDelAExorcizar.remove(demonioActual);
            }
            angelActual.desvincularSiNoHayConexion(this);
            angelesDelExorcista.remove(angelActual);
        }
    }

    public void desvicularseDe (Espiritu espiritu) {
        espiritus.remove(espiritu);
    }

public void moverA(Ubicacion ubicacion, Boolean puedeMoverseA, Double distancia) {
    if (puedeMoverseA && this.distanciaMenorOIgualA(distancia,30d)) {
        this.ubicacion = ubicacion;
        Set<Espiritu> copiaEspiritus = new HashSet<>(this.espiritus);
        copiaEspiritus.forEach(e -> this.realizarMovimientoDeEspirituA(e, ubicacion));
        this.espiritus = copiaEspiritus;
    } else {
        throw new UbicacionLejanaException(ubicacion.getNombre());
    }
}

    private boolean distanciaMenorOIgualA(Double i, Double distancia) {
        return distancia > i;
    }

    public void realizarMovimientoDeEspirituA(Espiritu espiritu, Ubicacion ubicacion) {
        espiritu.setUbicacion(ubicacion);
        if (espiritu.getTipo() == DEMONIO && ubicacion.getTipoUbicacion() == SANTUARIO) {
            espiritu.setNivelDeConexion(espiritu.getNivelDeConexion() - 10);
        } else if (espiritu.getTipo() == ANGEL && ubicacion.getTipoUbicacion() == CEMENTERIO) {
            espiritu.setNivelDeConexion(espiritu.getNivelDeConexion() - 5);
        }
        espiritu.desvincularSiNoHayConexion(this);
    }
}