package ar.edu.unq.epersgeist.controller.dto;


import ar.edu.unq.epersgeist.modelo.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;


public record UbicacionDTO(Long id, String nombre, TipoUbicacion tipoUbicacion,
                           int energia, AreaDTO coordenadas, String descripcion) {

    public static UbicacionDTO desdeModelo(Ubicacion ubicacion, GeoJsonPolygon coordenadas, String descripcion) {
        return new UbicacionDTO(
                ubicacion.getId(),
                ubicacion.getNombre(),
                ubicacion.getTipoUbicacion(),
                ubicacion.getEnergia(),
                coordenadas != null ? AreaDTO.desdeModelo(coordenadas) : null,
                descripcion);
    }

    public Ubicacion aModelo(){
        Ubicacion ubicacion = new Ubicacion(this.nombre, this.energia, this.tipoUbicacion);
        ubicacion.setId(this.id);
        return ubicacion;
    }

    public Ubicacion actualizarModelo(Ubicacion ubicacion) {
        ubicacion.setNombre(this.nombre);
        ubicacion.setTipoUbicacion(this.tipoUbicacion);
        ubicacion.setEnergia(this.energia);
        return ubicacion;
    }

}