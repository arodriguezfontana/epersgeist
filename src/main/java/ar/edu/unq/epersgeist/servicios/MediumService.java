package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;
import java.util.Optional;

public interface MediumService {
    Medium crear(Medium medium, GeoJsonPoint punto, String descripcion);
    Medium crearConIndexar(Medium medium, GeoJsonPoint punto, String descripcion);
    Optional<Medium> recuperar(Long mediumId);
    Optional<MediumElastic> recuperarElastic(Long id);
    List<Medium> recuperarTodos();
    void actualizar(Medium medium);
    void actualizarDescripcion(MediumElastic medElastic);
    void eliminar(Long mediumId);
    void descansar(Long mediumId);
    List<Espiritu> espiritus(Long mediumId);
    Espiritu invocar(Long mediumId, Long espirituId);
    void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar);
    void mover(Long mediumId, Double latitud, Double longitud);
    List<MediumElastic> buscarSemanticamente(String texto);
    List<UbicacionElastic> investigarUbicaciones(String criterioDeBusqueda);
    List<UbicacionElastic> investigarUbicacionesMejorado(String criterioDeBusqueda);
    List<UbicacionElastic> investigarUbicacionesMejoradoFuzzines(String criterioDeBusqueda, String fuzzines);
    String recuperarDescripcion(Long mediumId);
}