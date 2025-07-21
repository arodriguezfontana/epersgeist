package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.TipoEspiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Set;
import java.util.stream.Collectors;

public record EspirituDTO(Long id, String nombre, String tipo, @Min(0) @Max(100) int nivelDeConexion,
                          Long medium, Long idUbicacion, Set<Long> amos, Set<Long> espiritusBajoControl, String descripcion) {


    public static EspirituDTO desdeModelo(Espiritu espiritu, String descripcion) {
        return new EspirituDTO(
                espiritu.getId(),
                espiritu.getNombre(),
                espiritu.getTipo().toString(),
                espiritu.getNivelDeConexion(),
                espiritu.getMedium() != null ? espiritu.getMedium().getId() : null,
                espiritu.getUbicacion() != null ? espiritu.getUbicacion().getId() : null,
                espiritu.getAmos().stream()
                        .map(Espiritu::getId)
                        .collect(Collectors.toSet()),
                espiritu.getEspiritusBajoControl().stream()
                        .map(Espiritu::getId)
                        .collect(Collectors.toSet()),
                descripcion
        );
    }

    // Sobrecarga sin descripción, usa null por defecto
    public static EspirituDTO desdeModelo(Espiritu espiritu) {
        return desdeModelo(espiritu, null);
    }

    public Espiritu aModelo(Ubicacion ubicacion, TipoEspiritu tipo) {
        Espiritu espiritu = new Espiritu(tipo, this.nivelDeConexion, this.nombre, ubicacion);
        espiritu.setId(this.id);
        return espiritu;
    }

    public Espiritu actualizarModelo(Espiritu espiritu, Ubicacion ubicacion, TipoEspiritu tipo) {
        espiritu.setNombre(this.nombre);
        espiritu.setTipo(tipo);
        espiritu.setNivelDeConexion(this.nivelDeConexion);
        espiritu.setUbicacion(ubicacion);
        return espiritu;
    }
}

