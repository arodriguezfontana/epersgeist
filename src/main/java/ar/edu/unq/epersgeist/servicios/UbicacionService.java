package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.controller.dto.ClosenessResult;
import ar.edu.unq.epersgeist.modelo.*;

import java.util.List;
import java.util.Optional;


public interface UbicacionService {
    Ubicacion crear(Ubicacion ubicacion, List<List<Double>> coordenadas, String descripcion);

    Ubicacion crearConIndexar(Ubicacion ubicacion, List<List<Double>> coordenadas, String descripcion);

    Optional<Ubicacion> recuperar(Long ubicacionId);

    Optional<UbicacionElastic> recuperarElastic(Long id);

    List<Ubicacion> recuperarTodos();
    void actualizar(Ubicacion ubicacion);

    void actualizarDescripcion(UbicacionElastic ubicacionElastic);

    void eliminar(Long ubicacionId);
    List<Espiritu> espiritusEn(Long ubicacionId);
    List<Medium> mediumsSinEspiritusEn(Long ubicacionId);
    void conectar(Long idOrigen, Long idDestino);
    Boolean estanConectadas(Long idOrigen, Long idDestino);
    List<Ubicacion> caminoMasCorto(Long idOrigen, Long idDestino);
    List<ClosenessResult> closenessOf(List<Long> ids);
    List<Ubicacion> ubicacionesSobrecargadas(int umbralDeEnergia);
    List<ClosenessResult> harmonicOf(List<Long> ids);
    Optional<UbicacionMongo> recuperarMongo(Long ubicacionId);
    List<UbicacionMongo> recuperarTodasMongo(List<Long> ids);
    List<UbicacionElastic> buscarSemanticamente(String texto);
    String recuperarDescripcion(Long ubicacionId);
}
