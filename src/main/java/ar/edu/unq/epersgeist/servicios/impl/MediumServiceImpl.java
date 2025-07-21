package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.excepcion.EntidadAELiminarEstaRelacionada;
import ar.edu.unq.epersgeist.modelo.excepcion.PuntoNoPerteneceException;
import ar.edu.unq.epersgeist.modelo.excepcion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.servicios.BusquedaSemantica;
import ar.edu.unq.epersgeist.servicios.MediumService;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MediumServiceImpl implements MediumService {

    private final MediumDAO mediumDAO;
    private final EspirituDAO espirituDAO;
    private final UbicacionDAO ubicacionDAO;
    private final UbicacionNeoDAO ubicacionNeoDAO;
    private final MediumDAOMongo mediumDAOMongo;
    private final UbicacionDAOMongo ubicacionDAOMongo;
    private final EspirituDAOMongo espirituDAOMongo;
    private final MediumElasticDAO mediumElasticDAO;
    private final BusquedaSemantica busquedaSemantica;
    private final UbicacionElasticDAO ubicacionElasticDAO;

    public MediumServiceImpl(UbicacionDAOMongo ubicacionDAOMongo, MediumDAOMongo mediumDAOMongo,
                             MediumDAO mediumDAO, EspirituDAO espirituDao, UbicacionDAO ubicacionDAO,
                             UbicacionNeoDAO ubicacionNeoDAO, EspirituDAOMongo espirituDAOMongo,
                             MediumElasticDAO mediumElasticDAO, BusquedaSemantica busquedaSemantica,
                             UbicacionElasticDAO ubicacionElasticDAO){
        this.mediumDAO = mediumDAO;
        this.espirituDAO = espirituDao;
        this.ubicacionDAO = ubicacionDAO;
        this.ubicacionNeoDAO = ubicacionNeoDAO;
        this.mediumDAOMongo = mediumDAOMongo;
        this.ubicacionDAOMongo = ubicacionDAOMongo;
        this.espirituDAOMongo = espirituDAOMongo;
        this.mediumElasticDAO = mediumElasticDAO;
        this.busquedaSemantica = busquedaSemantica;
        this.ubicacionElasticDAO = ubicacionElasticDAO;
    }

    @Override
    public Medium crear(Medium medium, GeoJsonPoint punto, String descripcion) {
        Medium mediumCreado = mediumDAO.save(medium);
        Long ubiId = medium.getUbicacion().getId();

        if (!ubicacionDAOMongo.puntoPerteneceA(punto, ubiId)) {
            throw new PuntoNoPerteneceException();
        }

        MediumMongo mediumMongo = new MediumMongo(mediumCreado.getId(), punto);
        mediumDAOMongo.save(mediumMongo);

        MediumElastic mediumElastic = new MediumElastic(mediumCreado.getId(),
                mediumCreado.getNombre(),
                descripcion);
        mediumElasticDAO.save(mediumElastic);
        return mediumCreado;
    }

    @Override
    public Medium crearConIndexar(Medium medium, GeoJsonPoint punto, String descripcion) {
        Medium mediumCreado = mediumDAO.save(medium);
        Long ubiId = medium.getUbicacion().getId();

        if (!ubicacionDAOMongo.puntoPerteneceA(punto, ubiId)) {
            throw new PuntoNoPerteneceException();
        }

        MediumMongo mediumMongo = new MediumMongo(mediumCreado.getId(), punto);
        mediumDAOMongo.save(mediumMongo);

        MediumElastic mediumElastic = new MediumElastic(mediumCreado.getId(),
                mediumCreado.getNombre(),
                descripcion);
        mediumElasticDAO.save(mediumElastic);

        try {
            busquedaSemantica.indexarMedium(mediumElastic);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return mediumCreado;
    }

    @Override
    public Optional<Medium> recuperar(Long mediumId) {
        return mediumDAO.findById(mediumId);
    }

    @Override
    public Optional<MediumElastic> recuperarElastic(Long id) {
        return mediumElasticDAO.findById(id);
    }

    @Override
    public List<Medium> recuperarTodos() {
        return mediumDAO.findAll();
    }
    //PENSAR SI QUEREMOS EL RECUPERAR TODOS

    @Override
    public void actualizar(Medium medium) {
        mediumDAO.save(medium);
    }

    @Override
    public void actualizarDescripcion(MediumElastic medElastic) {
        mediumElasticDAO.save(medElastic);
    }

    @Override
    public void eliminar(Long mediumId) {
        Medium mediumR = this.recuperar(mediumId)
                .orElseThrow(() -> new NoSuchElementException("No existe el Medium a eliminar"));
        boolean tieneEspiritus = mediumDAO.tieneEspiritus(mediumR.getId());
        if (!tieneEspiritus) {
            mediumDAOMongo.deleteById(mediumId);
            mediumDAO.deleteById(mediumId);
            mediumElasticDAO.deleteById(mediumId);
        } else {
            throw new EntidadAELiminarEstaRelacionada();
        }
    }

    @Override
    public void descansar(Long mediumId) {
        Medium mRecuperado = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new NoSuchElementException("El medium solicitado no existe."));
        TipoEspiritu tEspiritu = mRecuperado.getUbicacion().getTipoUbicacion().getTipoAsociado();
        List<Espiritu> espiritusDescansados = mediumDAO.espiritusTipo(mediumId, tEspiritu);
        mRecuperado.descansar(espiritusDescansados);
        espiritusDescansados.forEach(espirituDAO::save);
        mediumDAO.save(mRecuperado);
    }

    @Override
    public List<Espiritu> espiritus(Long mediumId) {
        return mediumDAO.espiritus(mediumId);
    }

    @Override
    public Espiritu invocar(Long mediumId, Long espirituId) {
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new NoSuchElementException("El medium solicitado no existe."));
        Espiritu espiritu = espirituDAO.findById(espirituId)
                .orElseThrow(() -> new NoSuchElementException("El espíritu solicitado no existe."));

        MediumMongo mediumMongo = mediumDAOMongo.findById(mediumId)
                .orElseThrow(() -> new NoSuchElementException("El Medium Mongo no existe."));
        EspirituMongo espirituMongo = espirituDAOMongo.findById(espirituId)
                .orElseThrow(() -> new NoSuchElementException("El Espíritu Mongo no existe."));

        double lonEspiritu = espirituMongo.getPunto().getX();
        double latEspiritu = espirituMongo.getPunto().getY();
        double distancia = mediumDAOMongo.calcularDistanciaEntrePuntoYMedium(latEspiritu,
                lonEspiritu, mediumId);

        medium.invocarCerca(50D, distancia, espiritu);

        espiritu.setUbicacion(medium.getUbicacion());
        espirituMongo.setPunto(mediumMongo.getPunto());

        mediumDAO.save(medium);
        mediumDAOMongo.save(mediumMongo);
        espirituDAO.save(espiritu);
        espirituDAOMongo.save(espirituMongo);
        return espiritu;

    }

    @Override
    public void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar) {
        //Precondición 1: Los ids de los mediums dados existen en la base de datos.
        //Precondición 2: Ambos mediums estan en la misma ubicacion.

        Medium mExorcistaRecuperado = mediumDAO.findById(idMediumExorcista)
                .orElseThrow(() -> new NoSuchElementException("No existe el medium con id :" + idMediumExorcista));
        Medium mAExorcizarRecuperado = mediumDAO.findById(idMediumAExorcizar)
                .orElseThrow(() -> new NoSuchElementException("No existe el medium con id :" + idMediumAExorcizar));

        List<Espiritu> angelesDelExorcista = mediumDAO.espiritusTipo(idMediumExorcista, TipoEspiritu.ANGEL);
        List<Espiritu> demoniosDelAExorcizar = mediumDAO.espiritusTipo(idMediumAExorcizar, TipoEspiritu.DEMONIO);
        mExorcistaRecuperado.exorcizar(mAExorcizarRecuperado, angelesDelExorcista, demoniosDelAExorcizar);
        mExorcistaRecuperado.getEspiritus().forEach(espirituDAO::save);
        mediumDAO.save(mExorcistaRecuperado);
        mAExorcizarRecuperado.getEspiritus().forEach(espirituDAO::save);
        mediumDAO.save(mAExorcizarRecuperado);
    }

    @Override
    public void mover(Long mediumId, Double latitud, Double longitud) {

        Medium mRecuperado = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new NoSuchElementException("El Médium solicitado no existe."));
        MediumMongo mediumMongo = mediumDAOMongo.findById(mediumId).get();

        GeoJsonPoint punto = new GeoJsonPoint(longitud, latitud);
        List<UbicacionMongo> ubicacionesMongo = ubicacionDAOMongo.findAreaIdsQueContienenPunto(punto);
        if (ubicacionesMongo.isEmpty()) {
            throw new UbicacionNoEncontradaException("No existe una ubicación que contenga el punto dado.");
        }
        Long idUbicacion = ubicacionesMongo.getFirst().getId();
        Ubicacion uRecuperada = ubicacionDAO.findById(idUbicacion)
                .orElseThrow(() -> new NoSuchElementException("La Ubicación solicitada no existe."));

        Medium mediumMovido = this.moverAUnPuntoEn(mRecuperado, latitud, longitud, uRecuperada);
        this.actualizarMediumMovidoA(mediumMovido, mediumMongo, punto);
    }

    private Medium moverAUnPuntoEn(Medium medium, Double latitud, Double longitud, Ubicacion uRecuperada) {
        Long idUbicacionActual = medium.getUbicacion().getId();
        Boolean puedeMoverseA = ubicacionNeoDAO.ubicacionConectadaA(idUbicacionActual, uRecuperada.getId());

        Double distanciaEntrePuntos =
                mediumDAOMongo.calcularDistanciaEntrePuntoYMedium(latitud, longitud, medium.getId());

        medium.moverA(uRecuperada, puedeMoverseA, distanciaEntrePuntos);
        return medium;
    }

    private void actualizarMediumMovidoA(Medium medium, MediumMongo mMongo, GeoJsonPoint punto) {
        List<Espiritu> espiritus = mediumDAO.espiritus(medium.getId());
        if (!espiritus.isEmpty()) {
            espirituDAO.saveAll(espiritus);
        }
        mediumDAO.save(medium);

        mMongo.setPunto(punto);
        mediumDAOMongo.save(mMongo);

        if (!espiritus.isEmpty()) {
            List<Long> idsEspiritus = espiritus.stream().map(Espiritu::getId).toList();
            List<EspirituMongo> espiritusMongo = espirituDAOMongo.findAllById(idsEspiritus);
            espiritusMongo.forEach(e -> e.setPunto(punto));
            espirituDAOMongo.saveAll(espiritusMongo);
        }
    }

    @Override
    public List<MediumElastic> buscarSemanticamente(String texto) {
        try {
            List<Long> ids = busquedaSemantica.buscar(texto);
            return ids.stream()
                    .map(id -> mediumElasticDAO.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<UbicacionElastic> investigarUbicaciones(String criterioDeBusqueda) {
        return ubicacionElasticDAO.findByDescripcionContainingIgnoreCase(criterioDeBusqueda);
    }

    @Override
    public List<UbicacionElastic> investigarUbicacionesMejorado(String criterioDeBusqueda) {
        return ubicacionElasticDAO.buscarPorDescripcionAvanzada(criterioDeBusqueda);
    }

    @Override
    public List<UbicacionElastic> investigarUbicacionesMejoradoFuzzines(String criterioDeBusqueda, String fuzzines) {
        return ubicacionElasticDAO.buscarPorDescripcionAvanzadaFuzzines(criterioDeBusqueda, fuzzines);
    }

    @Override
    public String recuperarDescripcion(Long mediumId) {
        return mediumElasticDAO.findById(mediumId).get().getDescripcion();
    }
}