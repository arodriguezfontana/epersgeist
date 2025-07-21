package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.*;

import ar.edu.unq.epersgeist.persistencia.dao.*;

import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EstadisticaServiceImpl implements EstadisticaService {


    private final EstadisticaDAO estadisticaDao;
    private final EstadisticaDAOMongo estadisticaMongoDao;
    private final UbicacionNeoDAO ubicacionNeoDAO;
    private final EspirituDAO espirituDAO;
    private final MediumDAO mediumDAO;
    private final UbicacionDAO ubicacionDAO;
    private final EspirituDAOMongo espirituDAOMongo;
    private final MediumDAOMongo mediumDAOMongo;
    private final UbicacionDAOMongo ubicacionDAOMongo;

    public EstadisticaServiceImpl(
            EspirituDAO espirituDao,
            MediumDAO mediumDao,
            UbicacionDAO ubicacionDao,
            EspirituDAOMongo espirituDAOMongo,
            MediumDAOMongo mediumDAOMongo,
            UbicacionDAOMongo ubicacionDAOMongo,
            UbicacionNeoDAO
            ubicacionNeoDAO, EstadisticaDAOMongo estadisticaMongoDao, EstadisticaDAO estadisticaDao) {
        this.estadisticaDao = estadisticaDao;
        this.estadisticaMongoDao = estadisticaMongoDao;
        this.ubicacionNeoDAO =  ubicacionNeoDAO;
        this.espirituDAO = espirituDao;
        this.mediumDAO = mediumDao;
        this.ubicacionDAO = ubicacionDao;
        this.ubicacionDAOMongo = ubicacionDAOMongo;
        this.espirituDAOMongo = espirituDAOMongo;
        this.mediumDAOMongo = mediumDAOMongo;

    }

    @Override
    public ReporteSantuarioMasCorrupto santuarioCorrupto() {
        //Precond: Existe al menos una ubicacion de tipo SANTUARIO
        PageRequest pageRequest = PageRequest.of(0, 1);
        List<Ubicacion> result = estadisticaDao.findTopSantuarioWithMoreDemoniosThanAngeles(pageRequest);
        if (!result.isEmpty()) {
            Ubicacion santuario = result.getFirst();
            int demoniosEnSantuario = estadisticaDao.espiritusDeTipoEnUbicacion(TipoEspiritu
                    .DEMONIO,santuario.getId());
            Medium mediumConMasDemoniosEnSantuario = estadisticaDao.mediumConMasEspiritusDeTipoEn(TipoEspiritu.DEMONIO,santuario.getId());
            int demoniosLibresEnSantuario = estadisticaDao.espiritusDeTipoLibres(TipoEspiritu
                    .DEMONIO,santuario.getId());
            ReporteSantuarioMasCorrupto reporte = new ReporteSantuarioMasCorrupto(santuario,
                    demoniosEnSantuario,mediumConMasDemoniosEnSantuario,demoniosLibresEnSantuario);
            return reporte;
        } else {
            throw new NoSuchElementException("No existe ninguna Ubicación de tipo SANTUARIO");
        }
    }

    @Override
    public void crearSnapshot() {
        List<EspirituSnapshotData> espiritus = espirituDAO.findAll().stream()
                .map(e -> e != null ? new EspirituSnapshotData(e) : null).toList();
        List<MediumSnapshotData> mediums = mediumDAO.findAll().stream()
                .map(m-> m != null ? new MediumSnapshotData(m) : null).toList();
        List<UbicacionSnapshotData> ubicaciones =ubicacionDAO.findAll().stream()
                .map(u-> u != null ? new UbicacionSnapshotData(u) : null).toList();

        List<EspirituMongo> espiritusMongo = espirituDAOMongo.findAll();
        List<MediumMongo> mediumsMongo = mediumDAOMongo.findAll();
        List<UbicacionMongo> ubicacionesMongo = ubicacionDAOMongo.findAll();

        List<UbicacionNeoSnapshotData> ubicacionesNeo = ubicacionNeoDAO.findAll().stream()
                .map(u -> u != null ?new UbicacionNeoSnapshotData(u) : null).toList();

        LocalDate fechaActual = LocalDate.now();

        SnapShot newSnapShot = new SnapShot(
                espiritus, mediums, ubicaciones,
                ubicacionesNeo,
                espiritusMongo, mediumsMongo, ubicacionesMongo,
                fechaActual
        );
        estadisticaMongoDao.save(newSnapShot);
    }
    @Override
    public SnapShot obtenerSnapshot(LocalDate fecha) {
        //Precond: Existe un SnapShot con la fecha dada
        SnapShot snapRecuperado= estadisticaMongoDao.obtenerSnapshoot(fecha);
        if(snapRecuperado != null) {
            return snapRecuperado;
        }
        else {
            throw new NoSuchElementException("");
        }


    }
}
