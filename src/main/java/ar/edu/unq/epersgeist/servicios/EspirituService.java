package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;


import java.util.List;
import java.util.Optional;

public interface EspirituService {
    Espiritu crear(Espiritu espiritu, GeoJsonPoint punto, String descripcion);

    Espiritu crearConIndexar(Espiritu espiritu, GeoJsonPoint punto, String descripcion);

    Optional<Espiritu> recuperar(Long espirituId);
    Optional<EspirituElastic> recuperarElastic(Long espirituId);
    List<Espiritu> recuperarTodos();
    void actualizar(Espiritu espiritu);
    void actualizarDescripcion(EspirituElastic espElastic);
    void eliminar(Long espirituId);
    Medium conectar(Long espirituId, Long mediumId);
    List<Espiritu> espiritusDemoniacos(Direccion direccion, int pagina, int cantidadPorPagina);
    void dominar(Long espirituDominanteId, Long espirituADominarId);
    void teletransportar(Long idUbicacion, Long idEspiritu);
    List<EspirituElastic> buscarSemanticamente(String texto);
    String recuperarDescripcion(Long espirituId);

    HistorialTeletransportacion historialDeMovimiento(Long espirituId);
}
