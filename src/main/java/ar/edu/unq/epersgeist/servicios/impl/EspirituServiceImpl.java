package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.excepcion.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.servicios.BusquedaSemantica;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EspirituServiceImpl implements EspirituService {

    private final EspirituDAO espirituDAO;
    private final MediumDAO mediumDAO;
    private final UbicacionDAOMongo ubicacionDAOMongo;
    private final EspirituDAOMongo espirituDAOMongo;
    private final EspirituElasticDAO espirituElasticDAO;
    private final UbicacionElasticDAO ubicacionElasticDAO;
    private final UbicacionDAO ubicacionDAO;
    private final HistorialElasticDAO historialElasticDAO;
    private final BusquedaSemantica busquedaSemantica;

    public EspirituServiceImpl(EspirituDAO espirituDao, MediumDAO mediumDao,
                               EspirituDAOMongo espirituDAOMongo,
                               UbicacionDAOMongo ubicacionDAOMongo,
                               EspirituElasticDAO espirituElasticDAO,
                               UbicacionElasticDAO ubicacionElasticDAO,
                               UbicacionDAO ubicacionDAO,
                               HistorialElasticDAO historialElasticDAO, BusquedaSemantica busquedaSemantica) {
        this.mediumDAO = mediumDao;
        this.espirituDAO = espirituDao;
        this.espirituDAOMongo = espirituDAOMongo;
        this.ubicacionDAOMongo = ubicacionDAOMongo;
        this.espirituElasticDAO = espirituElasticDAO;
        this.ubicacionElasticDAO = ubicacionElasticDAO;
        this.ubicacionDAO = ubicacionDAO;
        this.historialElasticDAO = historialElasticDAO;
        this.busquedaSemantica = busquedaSemantica;
    }

    @Override
    public Espiritu crear(Espiritu espiritu, GeoJsonPoint punto, String descripcion) {
        Espiritu espirituCreado = espirituDAO.save(espiritu);
        Long ubiId = espiritu.getUbicacion().getId();

        if (!ubicacionDAOMongo.puntoPerteneceA(punto, ubiId)) {
            throw new PuntoNoPerteneceException();
        }

        EspirituMongo espirituMongo = new EspirituMongo(espirituCreado.getId(), punto);
        espirituDAOMongo.save(espirituMongo);

        EspirituElastic espirituElastic = new EspirituElastic(
                espirituCreado.getId(),
                espirituCreado.getNombre(),
                descripcion
        );
        espirituElastic.setUbicacion(new GeoPoint(punto.getX(), punto.getY()));
        espirituElasticDAO.save(espirituElastic);

        return espirituCreado;
    }

    @Override
    public Espiritu crearConIndexar(Espiritu espiritu, GeoJsonPoint punto, String descripcion) {
        Espiritu espirituCreado = espirituDAO.save(espiritu);
        Long ubiId = espiritu.getUbicacion().getId();

        if (!ubicacionDAOMongo.puntoPerteneceA(punto, ubiId)) {
            throw new PuntoNoPerteneceException();
        }

        EspirituMongo espirituMongo = new EspirituMongo(espirituCreado.getId(), punto);
        espirituDAOMongo.save(espirituMongo);

        EspirituElastic espirituElastic = new EspirituElastic(
                espirituCreado.getId(),
                espirituCreado.getNombre(),
                descripcion
        );
        espirituElastic.setUbicacion(new GeoPoint(punto.getX(), punto.getY()));
        espirituElasticDAO.save(espirituElastic);

        try {
            busquedaSemantica.indexarEspiritu(espirituElastic);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return espirituCreado;
    }

    @Override
    public Optional<Espiritu> recuperar(Long espirituId) {
        return espirituDAO.findById(espirituId);
    }

    @Override
    public Optional<EspirituElastic> recuperarElastic(Long id){
        return espirituElasticDAO.findById(id);
    }

    @Override
    public List<Espiritu> recuperarTodos() {
            return espirituDAO.findAll();
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        espirituDAO.save(espiritu);
    }

    @Override
    public void actualizarDescripcion(EspirituElastic espElastic) {
        espirituElasticDAO.save(espElastic);
    }

    @Override
    public void eliminar(Long espirituId) {
        Espiritu espirituR = this.recuperar(espirituId)
                .orElseThrow(() -> new NoSuchElementException("No existe el Espíritu a eliminar"));
        if(espirituR.estaLibre()){
            espirituDAOMongo.deleteById(espirituId);
            espirituDAO.deleteById(espirituId);
            espirituElasticDAO.deleteById(espirituId);
        }
        else {
            throw new EntidadAELiminarEstaRelacionada();
        }
    }

    @Override
    public Medium conectar(Long espirituId, Long mediumId) {
            Medium mRecuperado = mediumDAO.findById(mediumId)
                    .orElseThrow(() -> new NoSuchElementException("No hay un Médium para realizar la conexión."));
            Espiritu eRecuperado = espirituDAO.findById(espirituId)
                    .orElseThrow(() -> new NoSuchElementException ("No hay un Espíritu para realizar la conexión."));

            Boolean estaSiendoDominado = espirituDAO.estaSiendoDominado(espirituId);
            mRecuperado.verificarDominio(eRecuperado, estaSiendoDominado);
            espirituDAO.save(eRecuperado);
            mediumDAO.save(mRecuperado);
            return mRecuperado;

    }

    @Override
    public List<Espiritu> espiritusDemoniacos(Direccion direccion, int pagina,
                                              int cantidadPorPagina) {
        this.verificarValoresDePaginacion(pagina, cantidadPorPagina);
        Sort.Direction direction = Sort.Direction.valueOf(direccion.getDireccion());
        Pageable pageable = PageRequest.of((pagina - 1), cantidadPorPagina, Sort.by(direction, "nivelDeConexion"));
        return espirituDAO.findByTipo(TipoEspiritu.DEMONIO, pageable).getContent();
    }

    private void verificarValoresDePaginacion(int pagina, int cantidadPorPagina) {
        if (pagina < 0 || cantidadPorPagina < 0 ) {
            throw new ValorNegativoDePaginacion();
        }
    }

    @Override
    public void dominar(Long espirituDominanteId, Long espirituADominarId){
        Espiritu eDominanteRecuperado = espirituDAO.findById(espirituDominanteId)
                .orElseThrow(() -> new NoSuchElementException ("No se encontro el Espiritu Dominate."));

        Espiritu eADominarRecuperado = espirituDAO.findById(espirituADominarId)
            .orElseThrow(() -> new NoSuchElementException ("No se encontro el Espiritu Dominate."));

        double distanciaEntreEspiritus = espirituDAOMongo
                .calcularDistanciaEntreEspiritus(espirituDominanteId, espirituADominarId);
        boolean cumpleCondiciones = this.cumpleConCondicionesDeDominio(distanciaEntreEspiritus,
                eDominanteRecuperado, eADominarRecuperado);

        eDominanteRecuperado.dominar(eADominarRecuperado, cumpleCondiciones);
        this.espirituDAO.save(eDominanteRecuperado);
        this.espirituDAO.save(eADominarRecuperado);

    }

    private boolean cumpleConCondicionesDeDominio(double distancia, Espiritu espirituDominante,
                                                  Espiritu espirituADominar) {
        boolean distanciaIndicada = distancia >= 2 && distancia <= 5;
        boolean espirituADominarLibre = espirituADominar.estaLibre();
        boolean espirituADominarDebil = espirituADominar.getNivelDeConexion() < 50;
        boolean noEsSuAmo = espirituDAO.noTieneComoAmoA(espirituDominante.getId(),
                espirituADominar.getId());

        return (noEsSuAmo && espirituADominarLibre && espirituADominarDebil && distanciaIndicada);
    }

    @Override
    public void teletransportar(Long idUbicacion, Long idEspiritu) {

        Espiritu espirituRecuperado = this.recuperar(idEspiritu)
                .orElseThrow(() -> new NoSuchElementException("No existe el Espíritu a teletransportar"));
        Ubicacion ubicacion = ubicacionDAO.findById(idUbicacion)
                .orElseThrow(() -> new NoSuchElementException("No existe la ubicación a teletransportar"));

        if (espirituRecuperado.estaLibre() && espirituRecuperado.getNivelDeConexion() >= 50) {
            Espiritu espirituTeletransportado = espirituRecuperado.telestransportarA(ubicacion);

            UbicacionElastic ubicacionElastic = ubicacionElasticDAO.findById(idUbicacion)
                    .orElseThrow(() -> new NoSuchElementException("No existe la ubicación a teletransportar"));
            EspirituElastic espirituElasticRecuperado = espirituElasticDAO.findById(idEspiritu)
                    .orElseThrow(() -> new NoSuchElementException("No existe el Espíritu a teletransportar"));

            espirituElasticRecuperado.setUbicacion(ubicacionElastic.getArea());
            EspirituMongo espirituMongo = new EspirituMongo(espirituTeletransportado.getId(), new GeoJsonPoint(ubicacionElastic.getArea().getLat(), ubicacionElastic.getArea().getLon()));
            espirituDAOMongo.save(espirituMongo);
            espirituElasticDAO.save(espirituElasticRecuperado);
            this.actualizar(espirituTeletransportado);
            HistorialElastic registro = new HistorialElastic(espirituTeletransportado.getId(), ubicacionElastic.getArea(), idUbicacion);
            historialElasticDAO.save(registro);
        } else {
            throw new NoSePuedeRealizarLaTeletransportacionException();
        }
    }

    @Override
    public List<EspirituElastic> buscarSemanticamente(String texto) {
        try {
            List<Long> ids = busquedaSemantica.buscar(texto);
            return ids.stream()
                    .map(id -> espirituElasticDAO.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public String recuperarDescripcion(Long idEspiritu) {
        return espirituElasticDAO.findById(idEspiritu).get().getDescripcion();
    }

    @Override
    public HistorialTeletransportacion historialDeMovimiento(Long espirituId) {
        EspirituElastic eEncontrado = espirituElasticDAO.findById(espirituId)
                .orElseThrow(() -> new NoSuchElementException("El Espiritu solicitado no existe."));

        String nombreEspiritu = eEncontrado.getNombre();

        int cantDeMovimientos = historialElasticDAO.cantidadDeMovimientosDe(espirituId);
        List<Long> idsUbicacionesPorLasQueAnduvo = historialElasticDAO.ubicacionesPorLasQuePaso(espirituId);
        Long ubicacionMasConcurrida = historialElasticDAO.idDeUbicacionMasConcurridaPor(espirituId);
        return new HistorialTeletransportacion(nombreEspiritu,ubicacionMasConcurrida,idsUbicacionesPorLasQueAnduvo,cantDeMovimientos);
    }
}
