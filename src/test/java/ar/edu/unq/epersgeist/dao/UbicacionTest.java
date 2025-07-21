package ar.edu.unq.epersgeist.dao;

import ar.edu.unq.epersgeist.controller.dto.ClosenessResult;
import ar.edu.unq.epersgeist.dao.Utils.DatabaseCleaner;
import ar.edu.unq.epersgeist.dao.Utils.ElasticSearchCleaner;
import ar.edu.unq.epersgeist.dao.Utils.MongoDBDatabaseCleaner;
import ar.edu.unq.epersgeist.dao.Utils.Neo4jDatabaseCleaner;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.excepcion.*;

import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;

import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import org.springframework.data.geo.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class UbicacionTest {

    @Autowired
    private UbicacionServiceImpl servUbi;
    @Autowired
    private EspirituServiceImpl servEsp;
    @Autowired
    private MediumServiceImpl servMed;

    private List<List<Double>> area1;
    private List<List<Double>> area2;
    private List<List<Double>> area3;
    private List<List<Double>> area4;
    private List<List<Double>> area5;

    private Ubicacion buenosAires;
    private Ubicacion ushuaia;
    private Ubicacion cordoba;
    private Ubicacion mendoza;

    private Espiritu marcelito;
    private Espiritu dieguito;

    private Medium pepe;
    private Medium rod;

    private String descripcion1;

    @Autowired
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private Neo4jDatabaseCleaner neo4jDatabaseCleaner;

    @Autowired
    private MongoDBDatabaseCleaner mongoDBDatabaseCleaner;

    @Autowired
    private ElasticSearchCleaner elasticSearchCleaner;

    private GeoJsonPoint puntoBuenosAires;


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

        buenosAires = new Ubicacion("Buenos Aires", 10, TipoUbicacion.CEMENTERIO);
        ushuaia = new Ubicacion("Ushuaia", 10, TipoUbicacion.SANTUARIO);

        marcelito = new Espiritu(TipoEspiritu.DEMONIO,50,"Marcelito", buenosAires);
        dieguito = new Espiritu(TipoEspiritu.ANGEL,10,"Dieguito", buenosAires);

        pepe = new Medium("pepe", 20, 10, buenosAires);
        rod = new Medium("rod", 20, 15, buenosAires);

        puntoBuenosAires = new GeoJsonPoint(new Point(0.11,0.11));

    }

    @Test
    void testSeCreaUnaUbicacionCorrectamente() {
        //SetUp
        cordoba = new Ubicacion("Cordoba", 15, TipoUbicacion.CEMENTERIO);

        //Exercise
        cordoba = servUbi.crear(cordoba, area4, descripcion1);

        //Verify
        assertNotNull(cordoba.getId());
        assertNotNull(cordoba.getCreatedAt());
    }

    @Test
    void testSePuedeCrearUnaUbicacionEnElLimiteLateralDeOtra() {
        //SetUp
        cordoba = new Ubicacion("Cordoba", 15, TipoUbicacion.CEMENTERIO);
        mendoza = new Ubicacion("Mendoza", 20, TipoUbicacion.CEMENTERIO);

        List<List<Double>> areaLimitrofe = Arrays.asList(
                Arrays.asList(0.19, -0.20),
                Arrays.asList(0.19, -0.10),
                Arrays.asList(0.35, -0.10),
                Arrays.asList(0.35, -0.20),
                Arrays.asList(0.19, -0.20)
        );


        //Exercise
        servUbi.crear(cordoba, area4, descripcion1);


        //Verify
        assertThrows(UbicacionSuperpuesta.class, () -> {
            servUbi.crear(mendoza, areaLimitrofe, descripcion1);});

    }

    @Test
    void testNoSePuedeCrearUnaUbicacionSuperpuestaEnUnaParteConOtra() {
        //SetUp
        cordoba = new Ubicacion("Cordoba", 15, TipoUbicacion.CEMENTERIO);
        mendoza = new Ubicacion("Mendoza", 20, TipoUbicacion.CEMENTERIO);

        List<List<Double>> poligonoSuperpuesto = Arrays.asList(
                Arrays.asList(0.09, -0.20),
                Arrays.asList(0.09, -0.10),
                Arrays.asList(0.28, -0.10),
                Arrays.asList(0.28, -0.20),
                Arrays.asList(0.09, -0.20)
        );

        List<List<Double>> areaSuperpuesta = poligonoSuperpuesto;

        //Exercise
        servUbi.crear(cordoba, area4, descripcion1);

        //Verify
        assertThrows(UbicacionSuperpuesta.class, () -> {
            servUbi.crear(mendoza, areaSuperpuesta, descripcion1);});
    }

    @Test
    void testNoSePuedeCrearUnaUbicacionSuperpuestaCompletamenteConOtra() {
        //SetUp
        cordoba = new Ubicacion("Cordoba", 15, TipoUbicacion.CEMENTERIO);
        mendoza = new Ubicacion("Mendoza", 20, TipoUbicacion.CEMENTERIO);

        //Exercise
        servUbi.crear(cordoba, area4, descripcion1);

        //Verify
        assertThrows(UbicacionSuperpuesta.class, () -> {
            servUbi.crear(mendoza, area4, descripcion1);});
    }

    @Test void testSePuedeCrearUnaUbicacionConElMismoNobreQueUnaBorrada() {
        //SetUp
        Ubicacion ushuaia1= servUbi.crear(ushuaia, area2, descripcion1);

        //Exercise
        servUbi.eliminar(ushuaia.getId());
        Ubicacion ushuaia2= servUbi.crear(ushuaia, area2, descripcion1);

        //Verify
        assertNotNull(ushuaia2.getId());
        assertNotNull(ushuaia2.getCreatedAt());
    }

    @Test
    void testNoSeCreaUnaUbicacionConEnergiaNegativa() {
        //verify
        assertThrows(NivelDeEnergiaFueraDeRango.class, () -> {
            new Ubicacion("Jujuy", -1, TipoUbicacion.SANTUARIO);
        });
    }

    @Test
    void testPersistirUnaUbicacionYRecuperarlo(){
        //SetUp
        ushuaia= servUbi.crear(ushuaia, area2, descripcion1);

        //Exercise
        Ubicacion ushuaiaRecuperado = servUbi.recuperar(ushuaia.getId()).get();

        //Verify
        assertEquals(ushuaiaRecuperado.getId(), ushuaia.getId());
        assertEquals(ushuaiaRecuperado.getNombre(), ushuaia.getNombre());
    }

    @Test
    void testNoSePuedeRecuperarUnElementoInexistenteYTiraExcepcion(){
        //Exercise
        Optional<Ubicacion> recuperado = servUbi.recuperar(10000L);

        //Verify
        assertThrows(NoSuchElementException.class, () -> {
            recuperado.get();
        });
    }

    @Test
    void testRecuperarTodasLasUbicaciones() {
        //SetUp
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);

        //Exercise
        List<Ubicacion> ubicacionesActuales = servUbi.recuperarTodos();
        List<Long> idsUbicaciones = ubicacionesActuales.stream().map(Ubicacion::getId).toList();

        //Verify
        assertTrue(idsUbicaciones.contains(ushuaia.getId()));
        assertTrue(idsUbicaciones.contains(buenosAires.getId()));
    }

    @Test
    void testSeCreanDosUbicacionesYUnaSeEliminaCorrectamente() {
        //SetUp
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);

        //Exercise
        servUbi.eliminar(ushuaia.getId());
        List<Ubicacion> ubicacionesActuales = servUbi.recuperarTodos();

        //Verify
        assertEquals(1, ubicacionesActuales.size());
        assertEquals(ubicacionesActuales.getFirst().getId(),buenosAires.getId());
    }

    @Test
    void testAcutalizarUbicacion(){
        //SetUp
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        //Exercise
        ushuaia.setNombre("Jujuy");
        servUbi.actualizar(ushuaia);

        Ubicacion ubicacionActualizada = servUbi.recuperar(ushuaia.getId()).get();

        //Verify
        assertEquals("Jujuy", ubicacionActualizada.getNombre());
        assertNotEquals(ushuaia.getCreatedAt(), ubicacionActualizada.getUpdateAt());
    }


    @Test
    void testSeCrean2EspiritusEnUnaUbicacionYSeObtienenCorrectamente(){
        //SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);

        //Exercise
        List<Espiritu> espiritusEnUbicacion = servUbi.espiritusEn(buenosAires.getId());

        //Verify
        assertTrue(espiritusEnUbicacion.stream().map(Espiritu::getId).toList().contains(dieguito.getId()));
        assertTrue(espiritusEnUbicacion.stream().map(Espiritu::getId).toList().contains(marcelito.getId()));
    }

    @Test
    void testMediumsSinEspiritusEnUnaUbicacion(){
        //SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);

        //Mediums con espiritus en Buenos Aires
        pepe = servMed.crear(pepe, puntoBuenosAires, descripcion1);
        Medium pepeConectado = servEsp.conectar(marcelito.getId(), pepe.getId());

        //Mediums sin espiritus en Buenos Aires
        rod = servMed.crear(rod, puntoBuenosAires, descripcion1);

        //Exercise
        List<Medium> mediumsSinEspiritusEn = servUbi.mediumsSinEspiritusEn(buenosAires.getId());

        //Verify
        assertNotNull(pepeConectado.getEspiritus());
        assertTrue(rod.getEspiritus().isEmpty());
        assertEquals(mediumsSinEspiritusEn.getFirst().getId(), rod.getId());
        assertEquals(1, mediumsSinEspiritusEn.size());
    }


    @Test
    void testMediumsSinEspiritusEnUnaUbicacionPeroTodosLosMediumsTienenEspiritus(){
        //SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);

        //Mediums con espiritus
        pepe = servMed.crear(pepe, puntoBuenosAires, descripcion1);
        rod = servMed.crear(rod, puntoBuenosAires, descripcion1);

        //conexion medium pepe con espiritu marcelito.
        servEsp.conectar(marcelito.getId(), pepe.getId());
        //conexion medium rod con espiritu dieguito.
        servEsp.conectar(dieguito.getId(), rod.getId());

        //Exercise
        List<Medium> mediumsSinEspiritusEn = servUbi.mediumsSinEspiritusEn(buenosAires.getId());

        //Verify
        assertTrue(mediumsSinEspiritusEn.isEmpty());

    }

    @Test
    void testEliminarUbicacionConEntidadesYFalla(){
        //SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        pepe = servMed.crear(pepe, puntoBuenosAires, descripcion1);

        //Verify
        assertThrows(EntidadAELiminarEstaRelacionada.class, () -> {
            servUbi.eliminar(buenosAires.getId());
        });

    }

    @Test
    void testSeVerificaQueUnaUbicacionEstaConectadaDirectamenteAOtra(){
        // SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        //Excercise
        servUbi.conectar(buenosAires.getId(), ushuaia.getId());

        //Verify
        assertTrue(servUbi.estanConectadas(buenosAires.getId(), ushuaia.getId()));
    }

    @Test
    void testSeVerificaQueUnaUbicacionNoEstaConectadaAOtra(){
        // SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        //Verify
        assertFalse(servUbi.estanConectadas(buenosAires.getId(), ushuaia.getId()));
    }

    @Test
    void testSeVerificaQueUnaUbicacionEstaConectadaAOtraUnidireccionalmente(){
        // SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        //Excercise
        servUbi.conectar(buenosAires.getId(), ushuaia.getId());

        //Verify
        assertFalse(servUbi.estanConectadas(ushuaia.getId(), buenosAires.getId()));
    }

    @Test
    void testSeVerificaSiUnaUbicacionEstaConectadaAOtraSolamenteAUnSaltoDeDistancia(){
        // SetUp
        Ubicacion misiones = new Ubicacion("Misiones", 10, TipoUbicacion.SANTUARIO);
        misiones = servUbi.crear(misiones, area3, descripcion1);
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        //Excercise
        servUbi.conectar(buenosAires.getId(), misiones.getId());
        servUbi.conectar(misiones.getId(), ushuaia.getId());

        //Verify
        assertFalse(servUbi.estanConectadas(buenosAires.getId(), ushuaia.getId()));
    }

    @Test
    void testSeVerificaSiUnaUbicacionEstaConectadaAOtraInexistente() {
        // SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);

        //Verify
        assertThrows(NoSuchElementException.class, () -> {
            servUbi.estanConectadas(buenosAires.getId(), ushuaia.getId());
        });
    }

    @Test
    void testSeBuscaCaminoMasCortoEntre2UbicacionesPeroNoEstanConectadas(){
        // SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        //Verify
        assertThrows(UbicacionesNoConectadasException.class, () -> {
            servUbi.caminoMasCorto(buenosAires.getId(), ushuaia.getId() );
        });
    }

    @Test
    void testSeBuscaElCaminoMasCortoEntre2Ubicaciones(){
        // SetUp
        Ubicacion misiones = new Ubicacion("Misiones", 10, TipoUbicacion.SANTUARIO);
        misiones = servUbi.crear(misiones, area3, descripcion1);
        Ubicacion cordoba = new Ubicacion("Cordoba", 10, TipoUbicacion.SANTUARIO);
        cordoba = servUbi.crear(cordoba, area4, descripcion1);
        Ubicacion neuquen = new Ubicacion("Neuquen", 10, TipoUbicacion.SANTUARIO);
        neuquen = servUbi.crear(neuquen, area5, descripcion1);
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        servUbi.conectar(buenosAires.getId(), misiones.getId());
        servUbi.conectar(misiones.getId(), ushuaia.getId());

        servUbi.conectar(buenosAires.getId(), cordoba.getId());
        servUbi.conectar(cordoba.getId(), neuquen.getId());
        servUbi.conectar(neuquen.getId(), ushuaia.getId());

        //Excercise
        List<Ubicacion> caminoMasCorto = servUbi.caminoMasCorto(buenosAires.getId(), ushuaia.getId());

        //Verify
        assertEquals(3, caminoMasCorto.size());
        assertEquals(buenosAires.getId(), caminoMasCorto.getFirst().getId());
        assertEquals(misiones.getId(), caminoMasCorto.get(1).getId());
        assertEquals(ushuaia.getId(), caminoMasCorto.getLast().getId());
    }

    @Test
    void testUnaUbicacionNoSePuedeConectarAOtraInexistente() {
        // SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);

        //Verify
        assertThrows(NoSuchElementException.class, () -> {
            servUbi.conectar(buenosAires.getId(), 1000L);
        });
    }

    @Test
    void testNoSePuedeRealizarUnaConexionConUnaUbicacionInexistente() {
        // SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);

        //Verify
        assertThrows(NoSuchElementException.class, () -> {
            servUbi.conectar(1000L, buenosAires.getId());
        });
    }

    @Test
    void testSurjeUnaConexionBidireccionalSiSeConectanAmbas() {
        // SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        //Excercise
        servUbi.conectar(buenosAires.getId(), ushuaia.getId());
        servUbi.conectar(ushuaia.getId(), buenosAires.getId());

        //Verify
        assertTrue(servUbi.estanConectadas(buenosAires.getId(), ushuaia.getId()));
        assertTrue(servUbi.estanConectadas(ushuaia.getId(), buenosAires.getId()));
    }

    @Test
    void testClosenessOfCon3UbicacionesConectadas(){
        //SetUp
        Ubicacion misiones = new Ubicacion( "Misiones", 10,
                                            TipoUbicacion.SANTUARIO);
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);
        misiones = servUbi.crear(misiones, area3, descripcion1);

        servUbi.conectar(buenosAires.getId(), ushuaia.getId());
        servUbi.conectar(ushuaia.getId(), buenosAires.getId());
        servUbi.conectar(buenosAires.getId(), misiones.getId());

        List<Long> ids = List.of(
                buenosAires.getId(),
                ushuaia.getId(),
                misiones.getId()
        );

        //Excercise
        List<ClosenessResult> closenessResultList = servUbi.closenessOf(ids);

        //Verify
        assertEquals(buenosAires.getId(), closenessResultList.getFirst().ubicacion().getId());
        assertEquals(0.5D, closenessResultList.getFirst().closeness());
        assertEquals(ushuaia.getId(), closenessResultList.get(1).ubicacion().getId());
        assertEquals(0.3333333333333333D, closenessResultList.get(1).closeness());
        assertEquals(misiones.getId(), closenessResultList.get(2).ubicacion().getId());
        assertEquals(0.3333333333333333D, closenessResultList.get(2).closeness());
    }

    @Test
    void testClosenessOfCon3UbicacionesYUnaNoExiste(){
        //SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        servUbi.conectar(buenosAires.getId(), ushuaia.getId());
        servUbi.conectar(ushuaia.getId(), buenosAires.getId());


        List<Long> ids = List.of(
                buenosAires.getId(),
                ushuaia.getId(),
                4564356354656L
        );

        //Exercise
        List<ClosenessResult> closenessResultList = servUbi.closenessOf(ids);

        //Verify
        assertEquals(2,closenessResultList.size());
        assertEquals(buenosAires.getId(), closenessResultList.getFirst().ubicacion().getId());
        assertEquals(1D, closenessResultList.getFirst().closeness());
        assertEquals(ushuaia.getId(), closenessResultList.get(1).ubicacion().getId());
        assertEquals(1D, closenessResultList.get(1).closeness());
    }

    @Test
    void testClosenessOfCon3Ubicaciones2ConectadasY1No(){
        //SetUp
        Ubicacion misiones = new Ubicacion( "Misiones", 10,
                                            TipoUbicacion.SANTUARIO);
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);
        misiones = servUbi.crear(misiones, area3, descripcion1);

        servUbi.conectar(buenosAires.getId(), ushuaia.getId());
        servUbi.conectar(ushuaia.getId(), buenosAires.getId());


        List<Long> ids = List.of(
                buenosAires.getId(),
                ushuaia.getId(),
                misiones.getId()
        );

        //Excercise
        List<ClosenessResult> closenessResultList = servUbi.closenessOf(ids);

        //Verify
        assertEquals(buenosAires.getId(), closenessResultList.getFirst().ubicacion().getId());
        assertEquals(1D, closenessResultList.getFirst().closeness());
        assertEquals(ushuaia.getId(), closenessResultList.get(1).ubicacion().getId());
        assertEquals(1D, closenessResultList.get(1).closeness());
        assertEquals(misiones.getId(), closenessResultList.get(2).ubicacion().getId());
        assertEquals(0D, closenessResultList.get(2).closeness());
    }
    @Test
    void testClosenessOfCon1UbicacionSinConexion(){
        //SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        List<Long> ids = List.of(
                buenosAires.getId()
        );

        //Excercise
        List<ClosenessResult> closenessResultList = servUbi.closenessOf(ids);

        //Verify
        assertEquals(buenosAires.getId(), closenessResultList.getFirst().ubicacion().getId());
        assertEquals(0D, closenessResultList.getFirst().closeness());
    }
    @Test
    void testNoExistenUbicacionesSobrecargadas() {
        //SetUp
         //Empty

        //Exercise
        List<Ubicacion> ubicaciones = servUbi.ubicacionesSobrecargadas(0);

        //Verify
        assertTrue(ubicaciones.isEmpty());
    }

    @Test
    void testUbicacionesSobrecargadas() {
        //Setup
        Ubicacion mataderos =  new Ubicacion("Mataderos", 8, TipoUbicacion.CEMENTERIO);
        servUbi.crear(mataderos, area3, descripcion1);
        servUbi.crear(buenosAires, area1, descripcion1);
        servUbi.crear(ushuaia, area2, descripcion1);

        //Exercise
        List<Long> idsUbicacionesSobrecargadas =
                servUbi.ubicacionesSobrecargadas(9).stream().map(u -> u.getId()).toList();

        //Verify
        assertTrue(idsUbicacionesSobrecargadas.contains(buenosAires.getId()));
        assertTrue(idsUbicacionesSobrecargadas.contains(ushuaia.getId()));
        assertFalse(idsUbicacionesSobrecargadas.contains(mataderos.getId()));
    }

    @Test
    void testExistenUbicacionesPeroNingunaSuperaElUmbralDeEnergia() {
        //Setup
        Ubicacion mataderos =  new Ubicacion("Mataderos", 8, TipoUbicacion.CEMENTERIO);
        servUbi.crear(mataderos, area3, descripcion1);
        servUbi.crear(buenosAires, area1, descripcion1);
        servUbi.crear(ushuaia, area2, descripcion1);

        //Exercise
        List<Long> idsUbicacionesSobrecargadas =
                servUbi.ubicacionesSobrecargadas(11).stream().map(u -> u.getId()).toList();

        //Verify
        assertTrue(idsUbicacionesSobrecargadas.isEmpty());
        assertFalse(idsUbicacionesSobrecargadas.contains(buenosAires.getId()));
        assertFalse(idsUbicacionesSobrecargadas.contains(ushuaia.getId()));
        assertFalse(idsUbicacionesSobrecargadas.contains(mataderos.getId()));
    }

    @Test
    void testExistenUbicacionesConIgualUmbralDeEnergiaASuperar() {
        //Setup
        Ubicacion mataderos =  new Ubicacion("Mataderos", 10, TipoUbicacion.CEMENTERIO);
        servUbi.crear(buenosAires, area1, descripcion1);
        servUbi.crear(ushuaia, area2, descripcion1);
        servUbi.crear(mataderos, area3, descripcion1);

        //Exercise
        List<Long> idsUbicacionesSobrecargadas =
                servUbi.ubicacionesSobrecargadas(10).stream().map(u -> u.getId()).toList();

        //Verify
        assertTrue(idsUbicacionesSobrecargadas.isEmpty());
        assertFalse(idsUbicacionesSobrecargadas.contains(buenosAires.getId()));
        assertFalse(idsUbicacionesSobrecargadas.contains(ushuaia.getId()));
        assertFalse(idsUbicacionesSobrecargadas.contains(mataderos.getId()));

    }

    @Test
    void testHarmonicOfCon3UbicacionesConectadas(){
        //SetUp
        Ubicacion misiones = new Ubicacion( "Misiones", 10,
                                            TipoUbicacion.SANTUARIO);

        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);
        misiones = servUbi.crear(misiones, area3, descripcion1);

        servUbi.conectar(buenosAires.getId(), ushuaia.getId());
        servUbi.conectar(ushuaia.getId(), buenosAires.getId());
        servUbi.conectar(buenosAires.getId(), misiones.getId());

        List<Long> ids = List.of(
                buenosAires.getId(),
                ushuaia.getId(),
                misiones.getId()
        );

        //Excercise
        List<ClosenessResult> closenessResultList = servUbi.harmonicOf(ids);

        //Verify
        assertEquals(buenosAires.getId(), closenessResultList.getFirst().ubicacion().getId());
        assertEquals(2D, closenessResultList.getFirst().closeness());
        assertEquals(ushuaia.getId(), closenessResultList.get(1).ubicacion().getId());
        assertEquals(1.5D, closenessResultList.get(1).closeness());
        assertEquals(misiones.getId(), closenessResultList.get(2).ubicacion().getId());
        assertEquals(1.5D, closenessResultList.get(2).closeness());
    }
    @Test
    void testHarmonicOfCon3UbicacionesYUnaNoExiste(){
        //SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        servUbi.conectar(buenosAires.getId(), ushuaia.getId());
        servUbi.conectar(ushuaia.getId(), buenosAires.getId());


        List<Long> ids = List.of(
                buenosAires.getId(),
                ushuaia.getId(),
                4564356354656L
        );
        //Exercise
        List<ClosenessResult> closenessResultList = servUbi.harmonicOf(ids);

        //Verify
        assertEquals(2,closenessResultList.size());
        assertEquals(buenosAires.getId(), closenessResultList.getFirst().ubicacion().getId());
        assertEquals(1D, closenessResultList.getFirst().closeness());
        assertEquals(ushuaia.getId(), closenessResultList.get(1).ubicacion().getId());
        assertEquals(1D, closenessResultList.get(1).closeness());

    }

    @Test
    void testHarmonicOfCon3Ubicaciones2ConectadasY1No(){
        //SetUp
        Ubicacion misiones = new Ubicacion("Misiones", 10, TipoUbicacion.SANTUARIO);
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);
        misiones = servUbi.crear(misiones, area3, descripcion1);

        servUbi.conectar(buenosAires.getId(), ushuaia.getId());
        servUbi.conectar(ushuaia.getId(), buenosAires.getId());


        List<Long> ids = List.of(
                buenosAires.getId(),
                ushuaia.getId(),
                misiones.getId()
        );

        //Excercise
        List<ClosenessResult> closenessResultList = servUbi.harmonicOf(ids);

        //Verify
        assertEquals(buenosAires.getId(), closenessResultList.getFirst().ubicacion().getId());
        assertEquals(1D, closenessResultList.getFirst().closeness());
        assertEquals(ushuaia.getId(), closenessResultList.get(1).ubicacion().getId());
        assertEquals(1D, closenessResultList.get(1).closeness());
        assertEquals(misiones.getId(), closenessResultList.get(2).ubicacion().getId());
        assertEquals(0D, closenessResultList.get(2).closeness());
    }

    @Test
    void noSePuedeEliminarUnaUbicacionCoenctadaAOtras(){
        //SetUp
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        servUbi.conectar(buenosAires.getId(), ushuaia.getId());
        servUbi.conectar(ushuaia.getId(), buenosAires.getId());

        //Verify
        assertThrows(EntidadAELiminarEstaRelacionada.class, () -> {
            servUbi.eliminar(buenosAires.getId());
        });
    }

    @Test
    void testNoSePuedeCrearUnaUbicacionEnElLimiteSuperiorDeOtra() {
        //SetUp
        mendoza = new Ubicacion("Mendoza", 20, TipoUbicacion.CEMENTERIO);

        List<List<Double>> areaLimitrofe = Arrays.asList(
                Arrays.asList(0.0, 0.19),
                Arrays.asList(0.0, 0.20),
                Arrays.asList(0.19, 0.20),
                Arrays.asList(0.19, 0.19),
                Arrays.asList(0.0, 0.19)
        );

        //Exercise
            servUbi.crear(buenosAires, area1, descripcion1);


        //Verify
        assertThrows(UbicacionSuperpuesta.class, () -> {
            servUbi.crear(mendoza, areaLimitrofe, descripcion1);});
    }

    @Test
    void testNoSePuedeCrearUnPoligoConMenosDe4Puntos() {
        //SetUp
        mendoza = new Ubicacion("Mendoza", 20, TipoUbicacion.CEMENTERIO);

        List<List<Double>> area = Arrays.asList(
                Arrays.asList(0.0, 0.19),
                Arrays.asList(0.0, 0.20),
                Arrays.asList(0.0, 0.19)
        );

        //Verify
        assertThrows(IllegalArgumentException.class, () -> {
            servUbi.crear(mendoza, area, descripcion1);});
    }

    @Test
    void testNoSePuedeCrearUnPoligoQueEmpieceYTermineDistinto() {
        //SetUp
        mendoza = new Ubicacion("Mendoza", 20, TipoUbicacion.CEMENTERIO);

        List<List<Double>> area = Arrays.asList(
                Arrays.asList(0.0, 0.19),
                Arrays.asList(0.0, 0.20),
                Arrays.asList(0.19, 0.20),
                Arrays.asList(0.19, 0.19)
        );

        //Verify
        assertThrows(IllegalArgumentException.class, () -> {
            servUbi.crear(mendoza, area, descripcion1);});
    }

    @Test
    void testNoSePuedeCrearUnPoligoSiLosPuntosTienenMasDe2Elementos() {
        //SetUp
        mendoza = new Ubicacion("Mendoza", 20, TipoUbicacion.CEMENTERIO);

        List<List<Double>> area = Arrays.asList(
                Arrays.asList(0.0, 0.19, 0.0),
                Arrays.asList(0.0, 0.20),
                Arrays.asList(0.19, 0.20),
                Arrays.asList(0.19, 0.19),
                Arrays.asList(0.0, 0.19)
        );

        //Verify
        assertThrows(IllegalArgumentException.class, () -> {
            servUbi.crear(mendoza, area, descripcion1);});
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.cleanDatabase();
        neo4jDatabaseCleaner.cleanDatabase();
        mongoDBDatabaseCleaner.cleanDatabase();
        elasticSearchCleaner.cleanAllDocuments();
    }
}
