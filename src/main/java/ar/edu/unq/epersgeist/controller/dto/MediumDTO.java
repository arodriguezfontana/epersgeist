package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.TipoEspiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import jakarta.validation.constraints.Min;

import java.util.HashSet;

import java.util.Set;
import java.util.stream.Collectors;

public record MediumDTO(Long id, String nombre, @Min(0) int manaMax, @Min(0) int mana,
                        Set<EspirituDTO> espiritus, Long idUbicacion, String descripcion) {


    public static MediumDTO desdeModelo(Medium medium, String descripcion) {
        return new MediumDTO(
                medium.getId(),
                medium.getNombre(),
                medium.getManaMax(),
                medium.getMana(),
                medium.getEspiritus().stream()
                        .map(EspirituDTO::desdeModelo)
                        .collect(Collectors.toCollection(HashSet::new)),
                medium.getUbicacion() != null ? medium.getUbicacion().getId() : null,
                descripcion);
    }


    public Medium aModelo(Ubicacion ubicacion) {
        Medium medium = new Medium(this.nombre, this.manaMax, this.mana, ubicacion);
        medium.setId(this.id);
        medium.setEspiritus(this.espiritus != null ?
                this.espiritus.stream()
                        .map(espirituDTO -> espirituDTO.aModelo(ubicacion,
                                TipoEspiritu.valueOf(espirituDTO.tipo())))
                        .collect(Collectors.toCollection(HashSet::new)) :
                new HashSet<>()
        );
        return medium;
    }


    public Medium actualizarModelo(Medium medium, Ubicacion ubicacion,
                                   Set<Espiritu> espiritus) {
        medium.setNombre(this.nombre);
        medium.setMana(this.mana);
        medium.setManaMax(this.manaMax);
        medium.setEspiritus(espiritus);
        medium.setUbicacion(ubicacion);

        return medium;
    }
}