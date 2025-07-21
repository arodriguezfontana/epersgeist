package ar.edu.unq.epersgeist.dao;

import ar.edu.unq.epersgeist.dao.Utils.DatabaseCleaner;
import ar.edu.unq.epersgeist.dao.Utils.ElasticSearchCleaner;
import ar.edu.unq.epersgeist.dao.Utils.MongoDBDatabaseCleaner;
import ar.edu.unq.epersgeist.dao.Utils.Neo4jDatabaseCleaner;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.persistencia.dao.impl.EstadisticaDaoImpl;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.EstadisticaServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EstadisticaTest {

    @Autowired
    private UbicacionServiceImpl servUbi;
    @Autowired
    private EstadisticaDaoImpl estadisticaDao;
    @Autowired
    private EstadisticaDAOMongo estadisticaMongoDao;
    @Autowired
    private UbicacionNeoDAO ubicacionNeoDAO;
    @Autowired
    private  EspirituDAO espirituDAO;
    @Autowired
    private  MediumDAO mediumDAO;
    @Autowired
    private  UbicacionDAO ubicacionDAO;
    @Autowired
    private  EspirituDAOMongo espirituDAOMongo;
    @Autowired
    private  MediumDAOMongo mediumDAOMongo;
    @Autowired
    private  UbicacionDAOMongo ubicacionDAOMongo;

    @Autowired
    private EspirituServiceImpl servEsp;
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private MediumServiceImpl servMed;
    @Autowired
    private MongoDBDatabaseCleaner mongoDBDatabaseCleaner;
    @Autowired
    private Neo4jDatabaseCleaner neo4jDatabaseCleaner;
    @Autowired
    private ElasticSearchCleaner elasticSearchCleaner;

    private EstadisticaServiceImpl servEst;

    private List<List<Double>> area1;
    private List<List<Double>> area2;
    private List<List<Double>> area3;
    private List<List<Double>> area4;
    private List<List<Double>> area5;

    private Ubicacion sant1;
    private Ubicacion sant2;
    private Ubicacion sant3;

    private Espiritu mati;
    private Espiritu lu;
    private Espiritu aby;
    private Espiritu walle;
    private Espiritu iansito;

    private Ubicacion varela;
    private Ubicacion quilmes;

    private GeoJsonPoint puntoMiami;
    private GeoJsonPoint puntoCarlosPaz;
    private GeoJsonPoint puntoBerazategui;
    private GeoJsonPoint puntoVarela;
    private GeoJsonPoint puntoQuilmes;

    private String descripcion1;

    @BeforeEach
    void setUp() {

        List<List<Double>> coordsPoligono1 = Arrays.asList(
                Arrays.asList(0.0, 0.0),
                Arrays.asList(0.0, 0.19),
                Arrays.asList(0.19, 0.19),
                Arrays.asList(0.19, 0.0),
                Arrays.asList(0.0, 0.0)
        );

        List<List<Double>> coordsPoligono2 = Arrays.asList(
                Arrays.asList(0.20, 0.0),
                Arrays.asList(0.20, 0.19),
                Arrays.asList(0.38, 0.19),
                Arrays.asList(0.38, 0.0),
                Arrays.asList(0.20, 0.0)
        );

        List<List<Double>> coordsPoligono3 = Arrays.asList(
                Arrays.asList(0.15, 0.20),
                Arrays.asList(0.15, 0.38),
                Arrays.asList(0.34, 0.38),
                Arrays.asList(0.34, 0.20),
                Arrays.asList(0.15, 0.20)
        );

        List<List<Double>> coordsPoligono4 = Arrays.asList(
                Arrays.asList(0.0, -0.20),
                Arrays.asList(0.0, -0.1),
                Arrays.asList(0.19, -0.1),
                Arrays.asList(0.19, -0.20),
                Arrays.asList(0.0, -0.20)
        );

        List<List<Double>> coordsPoligono5 = Arrays.asList(
                Arrays.asList(0.20, -0.20),
                Arrays.asList(0.20, -0.1),
                Arrays.asList(0.38, -0.1),
                Arrays.asList(0.38, -0.20),
                Arrays.asList(0.20, -0.20)
        );


        area1 = coordsPoligono1;
        area2 = coordsPoligono2;
        area3 = coordsPoligono3;
        area4 = coordsPoligono4;
        area5 = coordsPoligono5;

        descripcion1 = "Lorem ipsum dolor sit amet consectetur adipiscing elit nec per rutrum hac, accumsan bibendum fermentum massa cum suscipit malesuada rhoncus feugiat sapien taciti, cursus libero velit senectus nascetur vivamus varius nullam porttitor molestie.";

        servEst = new EstadisticaServiceImpl(espirituDAO,mediumDAO,ubicacionDAO,espirituDAOMongo,mediumDAOMongo,ubicacionDAOMongo,ubicacionNeoDAO,estadisticaMongoDao,estadisticaDao);

        sant1 = new Ubicacion("Miamee", 50, TipoUbicacion.SANTUARIO);
        sant2 = new Ubicacion("Carlos Paz", 20, TipoUbicacion.SANTUARIO);
        sant3 = new Ubicacion("Berazategui", 100, TipoUbicacion.SANTUARIO);

        varela = new Ubicacion("Varela",20,TipoUbicacion.CEMENTERIO);
        quilmes = new Ubicacion("Quilmes",49,TipoUbicacion.CEMENTERIO);

        mati = new Espiritu(TipoEspiritu.DEMONIO,50,"mati", sant1);
        lu = new Espiritu(TipoEspiritu.DEMONIO,50,"lu", sant1);
        aby = new Espiritu(TipoEspiritu.DEMONIO,50,"aby", sant3);
        walle = new Espiritu(TipoEspiritu.DEMONIO,50,"walle", sant3);
        iansito = new Espiritu(TipoEspiritu.ANGEL,50,"iansito", sant3);

        puntoMiami = new GeoJsonPoint(new Point(0.11,0.11));
        puntoCarlosPaz = new GeoJsonPoint(new Point(0.21,0.17));
        puntoBerazategui = new GeoJsonPoint(new Point(0.16,0.22));
        puntoVarela = new GeoJsonPoint(new Point(0.11,-0.11));
        puntoQuilmes = new GeoJsonPoint(new Point(0.21,-0.11));
    }

    void persistirUbicaciones() {
        sant1 = servUbi.crear(sant1, area1, descripcion1);
        sant2 = servUbi.crear(sant2, area2, descripcion1);
        sant3 = servUbi.crear(sant3, area3, descripcion1);
    }

    @Test
    void testReporteGeneradoDelSantuarioCorrupto() {
        //Setup
       this.persistirUbicaciones();

        mati = servEsp.crear(mati, puntoMiami, descripcion1);
        lu = servEsp.crear(lu, puntoMiami, descripcion1);
        aby = servEsp.crear(aby, puntoBerazategui, descripcion1);
        walle = servEsp.crear(walle, puntoBerazategui, descripcion1);
        iansito = servEsp.crear(iansito, puntoBerazategui, descripcion1);

        //Exercise
        ReporteSantuarioMasCorrupto reporteSantuarioCorrupto = servEst.santuarioCorrupto();

        //Verify
        assertEquals(sant1.getNombre(), reporteSantuarioCorrupto.getNombreSantuario());
        assertEquals(2,reporteSantuarioCorrupto.getCantidadDeDemonios());
        assertEquals(2,reporteSantuarioCorrupto.getCantidadDeDemoniosLibres());
        assertNull(reporteSantuarioCorrupto.getMediumConMasDemonios()); //Mmhmh
    }

    @Test
    void testReporteNoSePuedeGenerarNoExistenSantuarios() {
        //Setup
        varela = servUbi.crear(varela, area4, descripcion1);
        quilmes = servUbi.crear(quilmes, area5, descripcion1);

        //Exercise

        //Verify
        assertThrows(NoSuchElementException.class, () -> {
             servEst.santuarioCorrupto();
        });
    }


    @Test
    void testReporteConDosSantuariosCorruptosEmpatados() {

        Ubicacion santuarioBoo = new Ubicacion("Vice City", 50, TipoUbicacion.SANTUARIO);
        Ubicacion santuarioGhost = new Ubicacion("San Andreas", 50, TipoUbicacion.SANTUARIO);

        santuarioBoo = servUbi.crear(santuarioBoo, area1, descripcion1);
        santuarioGhost = servUbi.crear(santuarioGhost, area2, descripcion1);

        Medium aladin = new Medium("Aladin", 90, 35, santuarioBoo);
        Medium machimbre = new Medium("Machimbre", 100, 20, santuarioGhost);

        Espiritu messi = new Espiritu(TipoEspiritu.DEMONIO,20,"Messi", santuarioBoo);
        Espiritu cris = new Espiritu(TipoEspiritu.DEMONIO,30,"Cris", santuarioGhost);

        aladin = servMed.crear(aladin, puntoMiami, descripcion1);
        machimbre = servMed.crear(machimbre, puntoCarlosPaz, descripcion1);

        messi = servEsp.crear(messi, puntoMiami, descripcion1);
        cris = servEsp.crear(cris, puntoCarlosPaz, descripcion1);

        servEsp.conectar(messi.getId(), aladin.getId());
        servEsp.conectar(cris.getId(), machimbre.getId());


        //Exercise
        ReporteSantuarioMasCorrupto reporte = servEst.santuarioCorrupto();

        //Verify
        assertEquals(santuarioBoo.getNombre(), reporte.getNombreSantuario());
        assertEquals(0,reporte.getCantidadDeDemoniosLibres());
        assertEquals(1,reporte.getCantidadDeDemonios());
        assertEquals(aladin.getId(),reporte.getMediumConMasDemonios().getId());

    }
    @Test
    void testSnapShotDelEstadoDelSistemaEsCorrectoConEspiritusUbicacionesConectadas() {
        this.persistirUbicaciones();

        mati = servEsp.crear(mati, puntoMiami, descripcion1);
        lu = servEsp.crear(lu, puntoMiami, descripcion1);
        aby = servEsp.crear(aby, puntoBerazategui, descripcion1);
        walle = servEsp.crear(walle, puntoBerazategui, descripcion1);
        iansito = servEsp.crear(iansito, puntoBerazategui, descripcion1);

        servUbi.conectar(sant1.getId(), sant2.getId());
        servUbi.conectar(sant1.getId(), sant3.getId());
        servUbi.conectar(sant2.getId(),sant1.getId());
        List<Long> espiritusIds = Arrays.asList(
                mati.getId(), lu.getId(), aby.getId(),
                walle.getId(), iansito.getId());
        List<Long> ubicacionesIds = Arrays.asList(
                sant1.getId(), sant2.getId(),sant3.getId()
        );

        //Exercise
        LocalDate hoy= LocalDate.now();
        servEst.crearSnapshot();
        SnapShot snapShot = servEst.obtenerSnapshot(hoy);

        //Verify
        assertTrue(snapShot.getSql().getEspiritus().stream().map(es -> es.getId()).toList().containsAll(espiritusIds));
        assertTrue(snapShot.getNeo4j().getUbicacionesNeo().stream().map(u -> u.getId()).toList().containsAll(ubicacionesIds));
        assertTrue(snapShot.getMongo().getUbicacionesMongo().stream().map(u -> u.getId()).toList().containsAll(ubicacionesIds));
    }

    @Test
    void testSnapShotDelEstadoDelSistemaEsCorrectoConEspiritusMediumsYUbicacionesConectadas() {
        this.persistirUbicaciones();

        mati = servEsp.crear(mati, puntoMiami, descripcion1);
        lu = servEsp.crear(lu, puntoMiami, descripcion1);
        aby = servEsp.crear(aby, puntoBerazategui, descripcion1);
        walle = servEsp.crear(walle, puntoBerazategui, descripcion1);
        iansito = servEsp.crear(iansito, puntoBerazategui, descripcion1);

        Medium aladin = new Medium("Aladin", 90, 35, sant1);
        Medium machimbre = new Medium("Machimbre", 100, 20, sant1);

        aladin = servMed.crear(aladin, puntoMiami, descripcion1);
        machimbre = servMed.crear(machimbre, puntoMiami, descripcion1);

        servUbi.conectar(sant1.getId(), sant2.getId());
        servUbi.conectar(sant2.getId(), sant1.getId());

        servEsp.conectar(mati.getId(),aladin.getId());
        servEsp.conectar(lu.getId(), machimbre.getId());

        List<Long> espiritusIds = Arrays.asList(
                mati.getId(), lu.getId(), aby.getId(),
                walle.getId(), iansito.getId());
        List<Long> ubicacionesIds = Arrays.asList(
                sant1.getId(), sant2.getId(),sant3.getId()
        );
        List<Long> mediumsIds = Arrays.asList(
                aladin.getId(), machimbre.getId()
        );

        //Exercise
        LocalDate hoy= LocalDate.now();
        servEst.crearSnapshot();
        SnapShot snapShot = servEst.obtenerSnapshot(hoy);

        //Verify
        assertTrue(snapShot.getSql().getEspiritus().stream().map(es -> es.getId()).toList().containsAll(espiritusIds));
        assertTrue(snapShot.getNeo4j().getUbicacionesNeo().stream().map(u -> u.getId()).toList().containsAll(ubicacionesIds));
        assertTrue(snapShot.getSql().getMediums().stream().map(m -> m.getId()).toList().containsAll(mediumsIds));
    }

    @Test
    void testSeBuscaUnSnapShotConFechaNoPersistida() {
        //Setup
        LocalDate fecha = LocalDate.of(1965,9,23);

        //Verify
        assertThrows(NoSuchElementException.class, () -> {
            servEst.obtenerSnapshot(fecha);;
        });
    }

    //TearDown
    @AfterEach
    void tearDown() {
        databaseCleaner.cleanDatabase();
        neo4jDatabaseCleaner.cleanDatabase();
        mongoDBDatabaseCleaner.cleanDatabase();
        elasticSearchCleaner.cleanAllDocuments();
    }
}

