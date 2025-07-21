package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.dto.ClosenessResult;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.excepcion.ConflictException;
import ar.edu.unq.epersgeist.modelo.excepcion.EntidadAELiminarEstaRelacionada;
import ar.edu.unq.epersgeist.modelo.excepcion.UbicacionSuperpuesta;
import ar.edu.unq.epersgeist.modelo.excepcion.UbicacionesNoConectadasException;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAOMongo;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionElasticDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionNeoDAO;
import ar.edu.unq.epersgeist.servicios.BusquedaSemantica;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionDAO ubicacionDAO;
    private final UbicacionNeoDAO ubicacionNeoDAO;
    private final UbicacionDAOMongo ubicacionDAOMongo;
    private final UbicacionElasticDAO ubicacionElasticDAO;
    private final BusquedaSemantica busquedaSemantica;

    public UbicacionServiceImpl(UbicacionDAOMongo ubicacionDAOMongo,
                                UbicacionDAO ubicacionDao,
                                UbicacionNeoDAO ubicacionNeoDAO,
                                UbicacionElasticDAO ubicacionElasticDAO, BusquedaSemantica busquedaSemantica) {
        this.ubicacionDAO = ubicacionDao;
        this.ubicacionNeoDAO = ubicacionNeoDAO;
        this.ubicacionDAOMongo = ubicacionDAOMongo;
        this.ubicacionElasticDAO = ubicacionElasticDAO;
        this.busquedaSemantica = busquedaSemantica;
    }

    @Override
    public Ubicacion crear(Ubicacion ubicacion, List<List<Double>> coordenadas, String descripcion) {
        if (ubicacionDAO.existsByNombreAndDeletedAtIsNull(ubicacion.getNombre())) {
            throw new ConflictException();
        }

        GeoJsonPolygon poligono = this.crearGeoJsonPolygonDesdeCoordenadas(coordenadas);
        List<UbicacionMongo> areasConVerticeSuperpuesto = ubicacionDAOMongo.buscarAreasConVerticesSuperpuestos(coordenadas);
        boolean seSuperpone = ubicacionDAOMongo.existeAreaSuperpuesta(poligono);

        if (!areasConVerticeSuperpuesto.isEmpty() || seSuperpone) {
            throw new UbicacionSuperpuesta();
        }

        Ubicacion ubiCreada = ubicacionDAO.save(ubicacion);
        UbicacionNeo ubiNEO = new UbicacionNeo(
                ubiCreada.getId(),
                ubiCreada.getNombre(),
                ubicacion.getEnergia()
        );
        UbicacionMongo ubiMongo = new UbicacionMongo(ubiCreada.getId(), poligono);

        UbicacionElastic ubiElastic = new UbicacionElastic(
                ubiCreada.getId(),
                ubiCreada.getNombre(),
                descripcion
        );

        List<Double> primerPunto = coordenadas.get(0);
        ubiElastic.setArea(new GeoPoint(primerPunto.get(0), primerPunto.get(1)));

        ubicacionDAOMongo.save(ubiMongo);
        ubicacionNeoDAO.save(ubiNEO);
        ubicacionElasticDAO.save(ubiElastic);

        return ubiCreada;
    }

    @Override
    public Ubicacion crearConIndexar(Ubicacion ubicacion, List<List<Double>> coordenadas, String descripcion) {
        if (ubicacionDAO.existsByNombreAndDeletedAtIsNull(ubicacion.getNombre())) {
            throw new ConflictException();
        }

        GeoJsonPolygon poligono = this.crearGeoJsonPolygonDesdeCoordenadas(coordenadas);
        List<UbicacionMongo> areasConVerticeSuperpuesto = ubicacionDAOMongo.buscarAreasConVerticesSuperpuestos(coordenadas);
        boolean seSuperpone = ubicacionDAOMongo.existeAreaSuperpuesta(poligono);

        if (!areasConVerticeSuperpuesto.isEmpty() || seSuperpone) {
            throw new UbicacionSuperpuesta();
        }

        Ubicacion ubiCreada = ubicacionDAO.save(ubicacion);
        UbicacionNeo ubiNEO = new UbicacionNeo(
                ubiCreada.getId(),
                ubiCreada.getNombre(),
                ubicacion.getEnergia()
        );
        UbicacionMongo ubiMongo = new UbicacionMongo(ubiCreada.getId(), poligono);

        UbicacionElastic ubiElastic = new UbicacionElastic(
                ubiCreada.getId(),
                ubiCreada.getNombre(),
                descripcion
        );

        List<Double> primerPunto = coordenadas.get(0);
        ubiElastic.setArea(new GeoPoint(primerPunto.get(0), primerPunto.get(1)));

        ubicacionDAOMongo.save(ubiMongo);
        ubicacionNeoDAO.save(ubiNEO);
        ubicacionElasticDAO.save(ubiElastic);

        try {
            busquedaSemantica.indexarUbicacion(ubiElastic);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return ubiCreada;
    }

    private GeoJsonPolygon crearGeoJsonPolygonDesdeCoordenadas(List<List<Double>> coordenadas) {
        if (coordenadas == null || coordenadas.size() < 4) {
            throw new IllegalArgumentException("El polígono debe tener al menos 4 puntos (incluyendo el cierre).");
        }
        if (!Objects.equals(coordenadas.getFirst(), coordenadas.getLast())) {
            throw new IllegalArgumentException("El polígono debe empezar y terminar con la misma coordenada.");
        }

        List<Point> puntos = new ArrayList<>();

        for (List<Double> coord : coordenadas) {
            if (coord == null || coord.size() != 2) {
                throw new IllegalArgumentException("Cada coordenada debe tener exactamente dos valores: [lon, lat].");
            }
            puntos.add(new Point(coord.get(0), coord.get(1))); // lon, lat
        }

        Point primero = puntos.get(0);
        Point ultimo = puntos.get(puntos.size() - 1);

        if (!primero.equals(ultimo)) {
            puntos.add(new Point(primero.getX(), primero.getY()));
        }

        return new GeoJsonPolygon(puntos);
    }


    @Override
    public Optional<Ubicacion> recuperar(Long ubicacionId) {
        return ubicacionDAO.findById(ubicacionId);
    }

    @Override
    //HACER CONTROLLER
    public Optional<UbicacionElastic> recuperarElastic(Long id){
     return ubicacionElasticDAO.findById(id);
    }

    @Override
    public List<Ubicacion> recuperarTodos() {
        return ubicacionDAO.findAll();
    }

    //PENSAR SI QUEREMOS EL RECUPERAR TODOS

    @Override
    public void actualizar(Ubicacion ubicacion) {
        ubicacionDAO.save(ubicacion);
        UbicacionNeo ubiNEO = new UbicacionNeo( ubicacion.getId(),
                                                ubicacion.getNombre(),
                                                ubicacion.getEnergia());
        ubicacionNeoDAO.save(ubiNEO);
    }

    @Override
    public void actualizarDescripcion(UbicacionElastic ubiElastic) {
        ubicacionElasticDAO.save(ubiElastic);
    }

    @Override
    public void eliminar(Long ubicacionId) {
        Ubicacion ubicacionR = ubicacionDAO.findById(ubicacionId)
                .orElseThrow(() -> new NoSuchElementException("No existe la Ubicación a eliminar"));

        boolean tieneEspiritus = ubicacionDAO.existenEspiritusEnUbicacion(ubicacionR.getId());
        boolean tieneMediums = ubicacionDAO.existenMediumsEnUbicacion(ubicacionR.getId());
        boolean tieneUbicacionesConectadas = ubicacionNeoDAO.tieneUbicacionesConectadas(ubicacionR.getId());

        UbicacionNeo ubiNEO = ubicacionNeoDAO.findById(ubicacionId).get();
        UbicacionMongo ubiMongo = ubicacionDAOMongo.findById(ubicacionId).get();
        UbicacionElastic ubiElastic = ubicacionElasticDAO.findById(ubicacionId).get();

        if(!tieneEspiritus &&
                !tieneMediums &&
                !tieneUbicacionesConectadas)
        {
            ubicacionDAOMongo.delete(ubiMongo);
            ubicacionDAO.deleteById(ubicacionId);
            ubicacionNeoDAO.delete(ubiNEO);
            ubicacionElasticDAO.delete(ubiElastic);
        }
        else {
            throw new EntidadAELiminarEstaRelacionada();
        };
    }

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        return  ubicacionDAO.espiritusEn(ubicacionId);
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId){
        return ubicacionDAO.mediumsSinEspiritusEn(ubicacionId);
    }

    @Override
    public void conectar(Long idOrigen, Long idDestino){
        UbicacionNeo uOrigen = ubicacionNeoDAO.findById(idOrigen)
                .orElseThrow(() -> new NoSuchElementException("No existe la Ubicación origen"));

        UbicacionNeo uDestino = ubicacionNeoDAO.findById(idDestino)
                .orElseThrow(() -> new NoSuchElementException("No existe la Ubicación destino"));

        uOrigen.conectarse(uDestino);
        ubicacionNeoDAO.save(uOrigen);
    }

    @Override
    public Boolean estanConectadas(Long idOrigen, Long idDestino){
        UbicacionNeo uOrigen = ubicacionNeoDAO.findById(idOrigen)
                .orElseThrow(() -> new NoSuchElementException("No existe la Ubicación origen"));

        UbicacionNeo uDestino = ubicacionNeoDAO.findById(idDestino)
                .orElseThrow(() -> new NoSuchElementException("No existe la Ubicación destino"));

        return ubicacionNeoDAO.ubicacionConectadaA(uOrigen.getId(), uDestino.getId());
    }

    @Override
    public List<Ubicacion> caminoMasCorto(Long idOrigen, Long idDestino){
        UbicacionNeo uOrigen = ubicacionNeoDAO.findById(idOrigen)
                .orElseThrow(() -> new NoSuchElementException("No existe la Ubicación origen"));

        UbicacionNeo uDestino = ubicacionNeoDAO.findById(idDestino)
                .orElseThrow(() -> new NoSuchElementException("No existe la Ubicación destino"));

        List<Long> camino = ubicacionNeoDAO.caminoMasCorto(idOrigen, idDestino);
        if (camino.isEmpty()) {
            throw new UbicacionesNoConectadasException(uOrigen.getNombre(), uDestino.getNombre());
        }
        List <Ubicacion> ubicacionesDesordenadas = ubicacionDAO.findAllById(camino);

        return ordenarCamino(ubicacionesDesordenadas, camino);
    }

    private List<Ubicacion> ordenarCamino(List<Ubicacion> ubicacionesDesordenadas, List<Long> camino){

        Map<Long, Ubicacion> mapUbicaciones = ubicacionesDesordenadas.stream()
                .collect(Collectors.toMap(Ubicacion::getId, Function.identity()));

        List<Ubicacion> ubicacionesOrdenadas = camino.stream()
                .map(mapUbicaciones::get)
                .toList();

        return ubicacionesOrdenadas;
    }

    @Override
    public List<ClosenessResult> closenessOf(List<Long> ids){
        List<Ubicacion> ubicaciones = this.ubicacionesOrdenadas(ids);
        List<Double> closenessIndices = ubicacionNeoDAO.calcularClosenessPorIds(ids);
        return crearLista(ubicaciones, closenessIndices);
    }

    @Override
    public List<ClosenessResult> harmonicOf(List<Long> ids) {
        List<Ubicacion> ubicaciones = this.ubicacionesOrdenadas(ids);
        List<Double> harmonicIndices = ubicacionNeoDAO.calcularHarmonicCentrality(ids);
        return crearLista(ubicaciones, harmonicIndices);
    }

    private List<Ubicacion> ubicacionesOrdenadas(List<Long> ids){
        return ubicacionDAO.findAllByIdOrdered(ids);
    }

    private List<ClosenessResult> crearLista(List<Ubicacion> ubicaciones, List<Double> indices) {
        List<ClosenessResult> listaDeCloseness = new ArrayList<>();

        for (int i = 0; i < ubicaciones.size(); i++) {
            Ubicacion ubicacion = ubicaciones.get(i);
            Double indice = indices.get(i);
            ClosenessResult closenessResult = new ClosenessResult(ubicacion, indice);
            listaDeCloseness.add(closenessResult);
        }

        return listaDeCloseness;
    }

    @Override
    public List<Ubicacion> ubicacionesSobrecargadas(int umbralDeEnergia) {
        List<Long> neoUbicaciones = ubicacionNeoDAO.ubicacionesSuperioresA(umbralDeEnergia);
        return ubicacionDAO.findAllById(neoUbicaciones);
    }

    @Override
    public Optional<UbicacionMongo> recuperarMongo(Long ubicacionId) {
        return ubicacionDAOMongo.findById(ubicacionId);
    }

    @Override
    public List<UbicacionMongo> recuperarTodasMongo(List<Long> ids) {
        return ubicacionDAOMongo.findAllById(ids);
    }

    @Override
    public List<UbicacionElastic> buscarSemanticamente(String texto) {
        try {
            List<Long> ids = busquedaSemantica.buscar(texto);
            return ids.stream()
                    .map(id -> ubicacionElasticDAO.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public String recuperarDescripcion(Long ubicacionId){
        return ubicacionElasticDAO.findById(ubicacionId).get().getDescripcion();
    }
}