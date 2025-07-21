package ar.edu.unq.epersgeist.dao;

import ar.edu.unq.epersgeist.dao.Utils.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.excepcion.*;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class EspirituTest {

    @Autowired
    private EspirituServiceImpl servEsp;

    @Autowired
    private MediumServiceImpl servMed;

    @Autowired
    private UbicacionServiceImpl servUbi;

    @Autowired
    private Neo4jDatabaseCleaner neo4jDatabaseCleaner;

    @Autowired
    private MongoDBDatabaseCleaner mongoDBDatabaseCleaner;

    @Autowired
    private ElasticSearchCleaner elasticSearchCleaner;

    private List<List<Double>> area1;
    private List<List<Double>> area2;

    private Espiritu marcelito;
    private Espiritu dieguito;
    private Espiritu sid;

    private Medium merlin;
    private Medium meliodas;
    private Medium yuno;

    private Ubicacion buenosAires;
    private Ubicacion ushuaia;

    private GeoJsonPoint puntoBuenosAires;
    private GeoJsonPoint puntoUshuaia;

    private String descripcion1;

    @Autowired
    private DatabaseCleaner databaseCleaner;

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

        area1 = coordsPoligono1;
        area2 = coordsPoligono2;

        descripcion1 = "Lorem ipsum dolor sit amet consectetur adipiscing elit nec per rutrum hac, accumsan bibendum fermentum massa cum suscipit malesuada rhoncus feugiat sapien taciti, cursus libero velit senectus nascetur vivamus varius nullam porttitor molestie.";

        buenosAires = new Ubicacion("Buenos Aires", 10, TipoUbicacion.CEMENTERIO);
        ushuaia = new Ubicacion("Ushuaia", 10, TipoUbicacion.SANTUARIO);


        marcelito = new Espiritu(TipoEspiritu.DEMONIO,50,"Marcelito", buenosAires);
        dieguito = new Espiritu(TipoEspiritu.ANGEL,10,"Dieguito", buenosAires);

        merlin = new Medium("Merlin", 90, 35, buenosAires);
        yuno = new Medium("Yuno", 30, 15, buenosAires);
        meliodas = new Medium("Meliodas", 130, 60, ushuaia);

        puntoBuenosAires = new GeoJsonPoint(new Point(0.11,0.11));
        puntoUshuaia = new GeoJsonPoint(new Point(0.21,0.17));

        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);
        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
    }

    @Test
    void testSeCreaUnEspirituCorrectamente() {
        //SetUp
        sid = new Espiritu(TipoEspiritu.ANGEL,60,"Sid", ushuaia);

        //Exercise
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);
        sid = servEsp.crear(sid, puntoUshuaia, descripcion1);

        //Verify
        assertNotNull(marcelito.getId());
        assertNotNull(dieguito.getId());
        assertNotNull(sid.getId());
        assertNotNull(sid.getCreatedAt());
        assertNotNull(dieguito.getCreatedAt());
        assertNotNull(marcelito.getCreatedAt());
    }


    @Test
    void testNoSeCreaEspirituConNivelDeConexionFueraDeRango(){
        //Verify
        assertThrows(NivelDeConexionFueraDeRango.class, () -> {
            new Espiritu(TipoEspiritu.DEMONIO,-1,"Ramon", buenosAires);
        });
    }

    @Test
    void testNoSeCreaUnEspirituEnUnPuntoFueraDeSuUbicacion() {
        //Verify
        assertThrows(PuntoNoPerteneceException.class, () -> {
            servEsp.crear(marcelito, puntoUshuaia, descripcion1);});
    }

    @Test
    void testPersistirUnEspirituYRecuperarlo(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);

        //Exercise
        Espiritu marcelitoRecuperado = servEsp.recuperar(marcelito.getId()).get();

        //Verify
        assertEquals(marcelitoRecuperado.getId(), marcelito.getId());
        assertEquals(marcelitoRecuperado.getNombre(), marcelito.getNombre());
        assertEquals(marcelitoRecuperado.getNivelDeConexion(), marcelito.getNivelDeConexion());
        assertEquals(marcelitoRecuperado.getTipo(), marcelito.getTipo());
    }

    @Test
    void testNoSePuedeRecuperarUnElementoInexistenteYTiraExcepcion(){
        //Exercise
        Optional<Espiritu> recuperado = servEsp.recuperar(10000L);

        //Verify
        assertThrows(NoSuchElementException.class, () -> {
            recuperado.get();
        });
    }

    @Test
    void testRecuperarTodosLosEspiritus() {
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);

        //Exercise
        List<Espiritu> espiritusActuales = servEsp.recuperarTodos();
        List<Long> idsEspiritus = espiritusActuales.stream().map(Espiritu::getId).toList();

        //Verify
        assertTrue(idsEspiritus.contains(marcelito.getId()));
        assertTrue(idsEspiritus.contains(dieguito.getId()));
    }

    @Test
    void testSeCreanDosEspiritusYUnoSeEliminaCorrectamente() {
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);


        //Exercise
        servEsp.eliminar(dieguito.getId());
        List<Espiritu> espiritusActuales = servEsp.recuperarTodos();

        //Verify
        assertEquals(1, espiritusActuales.size());
        assertEquals(espiritusActuales.getFirst().getId(), marcelito.getId());

    }

    @Test
    void testAcutalizarEspiritu(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);

        //Exercise
        marcelito.setNombre("Hola");
        servEsp.actualizar(marcelito);
        Espiritu marcelitoR = servEsp.recuperar(marcelito.getId()).get();
        Date actualizacion1 = marcelitoR.getUpdateAt();

        marcelitoR.setNivelDeConexion(9);
        servEsp.actualizar(marcelitoR);
        Espiritu marcelitoR2 = servEsp.recuperar(marcelitoR.getId()).get();
        Date actualizacion2 = marcelitoR2.getUpdateAt();

        Espiritu espirituActualizado = servEsp.recuperar(marcelito.getId()).get();

        //Verify
        assertEquals("Hola", espirituActualizado.getNombre());
        assertEquals(9, espirituActualizado.getNivelDeConexion());
        assertNotEquals(marcelitoR2.getCreatedAt(), actualizacion2);
        assertNotEquals(actualizacion1, actualizacion2);
    }

    @Test
    void testNoSePuedeAcutalizarEspirituConNDCNegativoYQuedaEn0(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);

        //Exercise
        marcelito.setNivelDeConexion(-100);
        servEsp.actualizar(marcelito);

        Espiritu espirituActualizado = servEsp.recuperar(marcelito.getId()).get();

        //Verify
        assertEquals(0, espirituActualizado.getNivelDeConexion());
    }

    @Test
    void testNoSePuedeAcutalizarEspirituConNDCMayorA100YQuedaEn100(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);

        //Exercise
        marcelito.setNivelDeConexion(200);
        servEsp.actualizar(marcelito);

        Espiritu espirituActualizado = servEsp.recuperar(marcelito.getId()).get();

        //Verify
        assertEquals(100, espirituActualizado.getNivelDeConexion());
    }


    @Test
    void testUnEspirituSePuedeConectarConUnMedium() {
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);

        servEsp.conectar(marcelito.getId(), merlin.getId());

        Medium mRecuperado = servMed.recuperar(merlin.getId()).get();
        Espiritu eRecuperado = servEsp.recuperar(marcelito.getId()).get();

        assertEquals(1, mRecuperado.getEspiritus().size());
        assertEquals(57, eRecuperado.getNivelDeConexion());
    }

    @Test
    void testUnEspirituNoSePuedeConectarConUnMediumSiNoEstanEnLaMismaUbicacion() {
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        assertNotEquals(meliodas.getUbicacion().getId(), marcelito.getUbicacion().getId());
        assertThrows(NoSePuedeRealizarLaConexionExeption.class, () -> {
            servEsp.conectar(marcelito.getId(), meliodas.getId());
        });
    }

    @Test
    void testUnEspirituNoSePuedeConectarConUnMediumSiNoEstaLibre() {
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);
        yuno = servMed.crear(yuno, puntoBuenosAires, descripcion1);

        servEsp.conectar(marcelito.getId(), yuno.getId());

        assertThrows(NoSePuedeRealizarLaConexionExeption.class, () -> {
            servEsp.conectar(marcelito.getId(), merlin.getId());
        });
    }

    @Test
    void testSiNoHayUnMediumNoSePuedeRealizarUnaConexion() {
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);

        assertThrows(RuntimeException.class, () -> {
            servEsp.conectar(marcelito.getId(), 500L);
        });
    }

    @Test
    void testSiNoHayUnEspirituNoSePuedeRealizarUnaConexion() {
        yuno = servMed.crear(yuno, puntoBuenosAires, descripcion1);

        assertThrows(RuntimeException.class, () -> {
            servEsp.conectar(500L, yuno.getId());
        });
    }

   @Test
    void sePidenLosEspiritusDemoniacosEnUnaPrimeraPaginaDeDosDeFormaAscendente(){
        //SetUp
        Espiritu miguelito = new Espiritu(TipoEspiritu.DEMONIO,20,"Miguelito", buenosAires);
        Espiritu jorgito = new Espiritu(TipoEspiritu.DEMONIO,60,"Jorgito", buenosAires);


        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);
        servEsp.crear(miguelito, puntoBuenosAires, descripcion1);
        servEsp.crear(jorgito, puntoBuenosAires, descripcion1);

        //Exercise
        List<Espiritu> espiritusDemoniacos= servEsp.espiritusDemoniacos
                (Direccion.ASCENDENTE,1,2);

        //Verify
        assertEquals(2, espiritusDemoniacos.size());
        assertEquals("Miguelito",espiritusDemoniacos.getFirst().getNombre());
    }

    @Test
    void sePidenLosEspiritusDemoniacosEnUnaPrimeraPaginaDeDosDeFormaDescendente(){
        //SetUp
        Espiritu miguelito = new Espiritu(TipoEspiritu.DEMONIO,20,"Miguelito", buenosAires);
        Espiritu jorgito = new Espiritu(TipoEspiritu.DEMONIO,60,"Jorgito", buenosAires);

        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);
        servEsp.crear(miguelito, puntoBuenosAires, descripcion1);
        servEsp.crear(jorgito, puntoBuenosAires, descripcion1);

        //Exercise
        List<Espiritu> espiritusDemoniacos= servEsp.espiritusDemoniacos
                (Direccion.DESCENDENTE,1,2);

        //Verify
        assertEquals(2, espiritusDemoniacos.size());
        assertEquals("Jorgito",espiritusDemoniacos.getFirst().getNombre());
    }

    @Test
    void sePidenLosEspiritusDemoniacosEnDistintosTamaniosDePagina(){
        //SetUp
        Espiritu miguelito = new Espiritu(TipoEspiritu.DEMONIO,20,"Miguelito", buenosAires);
        Espiritu jorgito = new Espiritu(TipoEspiritu.DEMONIO,60,"Jorgito", buenosAires);


        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);
        servEsp.crear(miguelito, puntoBuenosAires, descripcion1);
        servEsp.crear(jorgito, puntoBuenosAires, descripcion1);

        //Exercise
        List<Espiritu> espiritusDemoniacosTamanio2= servEsp.espiritusDemoniacos
                (Direccion.DESCENDENTE,1,2);
        List<Espiritu> espiritusDemoniacosTamanio3= servEsp.espiritusDemoniacos
                (Direccion.DESCENDENTE,1,3);

        //Verify
        assertEquals(2, espiritusDemoniacosTamanio2.size());
        assertEquals("Jorgito",espiritusDemoniacosTamanio2.getFirst().getNombre());
        assertEquals("Marcelito",espiritusDemoniacosTamanio2.getLast().getNombre());

        assertEquals(3, espiritusDemoniacosTamanio3.size());
        assertEquals("Jorgito",espiritusDemoniacosTamanio3.getFirst().getNombre());
        assertEquals("Miguelito",espiritusDemoniacosTamanio3.getLast().getNombre());
    }

    @Test
    void sePidenLosEspiritusDemoniacosDeDosPaginasDiferentes(){
        //SetUp
        Espiritu miguelito = new Espiritu(TipoEspiritu.DEMONIO,20,"Miguelito", buenosAires);
        Espiritu jorgito = new Espiritu(TipoEspiritu.DEMONIO,60,"Jorgito", buenosAires);


        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);
        servEsp.crear(miguelito, puntoBuenosAires, descripcion1);
        servEsp.crear(jorgito, puntoBuenosAires, descripcion1);

        //Exercise
        List<Espiritu> espiritusDemoniacosPagina1= servEsp.espiritusDemoniacos
                (Direccion.DESCENDENTE,1,2);
        List<Espiritu> espiritusDemoniacosPagina2= servEsp.espiritusDemoniacos
                (Direccion.DESCENDENTE,2,2);

        //Verify
        assertEquals(2, espiritusDemoniacosPagina1.size());
        assertEquals("Jorgito",espiritusDemoniacosPagina1.getFirst().getNombre());
        assertEquals("Marcelito",espiritusDemoniacosPagina1.getLast().getNombre());

        assertEquals(1, espiritusDemoniacosPagina2.size());
        assertEquals("Miguelito",espiritusDemoniacosPagina2.getFirst().getNombre());
        assertEquals("Miguelito",espiritusDemoniacosPagina2.getLast().getNombre());
    }

    @Test
    void sePidenLosEspiritusDemoniacosPeroNoHayYDevuelveUnaListaVacia(){
        //SetUp
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);

        //Exercise
        List<Espiritu> espiritusDemoniacos= servEsp.espiritusDemoniacos
                (Direccion.DESCENDENTE,1,2);

        //Verify

        assertTrue(espiritusDemoniacos.isEmpty());
    }

    @Test
    void sePidenLosEspiritusDemoniacosPeroSeMandaUnNumeroNegativoPorParametro(){
        //SetUp
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);

        //Verify
        assertThrows(ValorNegativoDePaginacion.class, () -> {
            servEsp.espiritusDemoniacos
                    (Direccion.DESCENDENTE,1,-2);
        });

        assertThrows(ValorNegativoDePaginacion.class, () -> {
            servEsp.espiritusDemoniacos
                    (Direccion.DESCENDENTE,-1,2);
        });

        assertThrows(ValorNegativoDePaginacion.class, () -> {
            servEsp.espiritusDemoniacos
                    (Direccion.DESCENDENTE,-1,-2);
        });

    }

    @Test
    void testSeEliminaUnEspirituYSeVerificaQueNoEsteEnLaUbicacion(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);

        //Exercise
        servEsp.eliminar(dieguito.getId());
        Ubicacion ubRecuperada = servUbi.recuperar(buenosAires.getId()).get();

        //Verify
        assertEquals(1, ubRecuperada.getEspiritusEnUbicacion().size());
    }

    @Test
    void testSeEliminaUnEspirituConectadoYFalla(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);
        servEsp.conectar(marcelito.getId(), merlin.getId());

        //Verify
        assertThrows(EntidadAELiminarEstaRelacionada.class, () -> {
            servEsp.eliminar(marcelito.getId());
        });
    }

    @Test
    void unEspirituDominaAOtroExitosamente(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        GeoJsonPoint puntoBuenosAires2 = new GeoJsonPoint(new Point(0.11,0.138));
        dieguito = servEsp.crear(dieguito, puntoBuenosAires2, descripcion1);

        //Excercise
        servEsp.dominar(marcelito.getId(), dieguito.getId());

        Espiritu marcelitoRecuperado = servEsp.recuperar(marcelito.getId()).get();
        Espiritu dieguitoRecuperado = servEsp.recuperar(dieguito.getId()).get();

        //Verify
        assertEquals(1, dieguitoRecuperado.getAmos().size());
        assertEquals(1, marcelitoRecuperado.getEspiritusBajoControl().size());
    }

    @Test
    void unEspirituDominadoNoSePuedeConectarAUnMedium(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        GeoJsonPoint puntoBuenosAires2 = new GeoJsonPoint(new Point(0.11,0.138));
        dieguito = servEsp.crear(dieguito, puntoBuenosAires2, descripcion1);
        merlin = servMed.crear(merlin, puntoBuenosAires2, descripcion1);

        //Excercise
        servEsp.dominar(marcelito.getId(), dieguito.getId());

        Espiritu dieguitoRecuperado = servEsp.recuperar(dieguito.getId()).get();

        //Verify
        assertThrows(EspirituDominadoException.class, () -> {
            servEsp.conectar(dieguitoRecuperado.getId(), merlin.getId());
        });
    }

    @Test
    void unEspirituIntentaDominarAOtroPeroEstaMuyLejos(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        GeoJsonPoint puntoBuenosAires2 = new GeoJsonPoint(new Point(0.11,0.18));
        dieguito = servEsp.crear(dieguito, puntoBuenosAires2, descripcion1);

        //Verify

        assertThrows(CondicionesDeDominioInsuficientes.class, () -> {
            servEsp.dominar(marcelito.getId(), dieguito.getId());
        });
    }

    @Test
    void unEspirituIntentaDominarAOtroPeroEstaMuyCerca() {
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        GeoJsonPoint puntoBuenosAires2 = new GeoJsonPoint(new Point(0.11, 0.11));
        dieguito = servEsp.crear(dieguito, puntoBuenosAires2, descripcion1);

        //Verify
        assertThrows(CondicionesDeDominioInsuficientes.class, () -> {
            servEsp.dominar(marcelito.getId(), dieguito.getId());
        });
    }

    @Test
    void unEspirituIntentaDominarAOtroPeroEstaCoectadoAUnMedium() {
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        GeoJsonPoint puntoBuenosAires2 = new GeoJsonPoint(new Point(0.11, 0.138));
        dieguito = servEsp.crear(dieguito, puntoBuenosAires2, descripcion1);
        servMed.crear(merlin, puntoBuenosAires2, descripcion1);
        servEsp.conectar(dieguito.getId(), merlin.getId());

        //Verify
        assertThrows(CondicionesDeDominioInsuficientes.class, () -> {
            servEsp.dominar(marcelito.getId(), dieguito.getId());
        });
    }

    @Test
    void unEspirituIntentaDominarAOtroPeroTieneMuchaEnergia() {
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        GeoJsonPoint puntoBuenosAires2 = new GeoJsonPoint(new Point(0.11, 0.138));
        dieguito = servEsp.crear(dieguito, puntoBuenosAires2, descripcion1);

        //Verify
        assertThrows(CondicionesDeDominioInsuficientes.class, () -> {
            servEsp.dominar(dieguito.getId(), marcelito.getId());
        });
    }

    @Test
    void unEspirituIntentaDominarASuAmoYFalla() {
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        GeoJsonPoint puntoBuenosAires2 = new GeoJsonPoint(new Point(0.11, 0.138));
        dieguito = servEsp.crear(dieguito, puntoBuenosAires2, descripcion1);

        //Excercise
        servEsp.dominar(marcelito.getId(), dieguito.getId());

        //Verify
        assertThrows(CondicionesDeDominioInsuficientes.class, () -> {
            servEsp.dominar(dieguito.getId(), marcelito.getId());
        });
    }

    @Test
    void unEspirituDominadoIntentaDominarAOtroExitosamente() {
        //SetUp
        Espiritu miguelito = new Espiritu(TipoEspiritu.DEMONIO,20,"Miguelito", buenosAires);
        Espiritu jorgito = new Espiritu(TipoEspiritu.DEMONIO,60,"Jorgito", buenosAires);


        jorgito = servEsp.crear(jorgito, puntoBuenosAires, descripcion1);
        GeoJsonPoint puntoBuenosAires2 = new GeoJsonPoint(new Point(0.11, 0.138));
        miguelito = servEsp.crear(miguelito, puntoBuenosAires2, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);

        //Excercise
        servEsp.dominar(jorgito.getId(), miguelito.getId());
        servEsp.dominar(miguelito.getId(), dieguito.getId());

        Espiritu miguelitoRecuperado = servEsp.recuperar(miguelito.getId()).get();
        Espiritu dieguitoRecuperado = servEsp.recuperar(dieguito.getId()).get();

        //Verify
        assertEquals(1, dieguitoRecuperado.getAmos().size());
        assertEquals(1, miguelitoRecuperado.getEspiritusBajoControl().size());
    }

    @Test
    void recuperarEspirituElastic(){
        // Setup
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);

        // Exercise
        EspirituElastic eRecuperado = servEsp.recuperarElastic(marcelito.getId()).get();

        // Verify
        assertEquals(marcelito.getId(), eRecuperado.getId());
    }

    @Test
    void espirituActualizaDescripcion(){
        // Setup
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        EspirituElastic espirituElastic = servEsp.recuperarElastic(marcelito.getId()).get();

        // Exercise
        String nuevaDescripcion = "Nueva descripcion de Marcelito";
        espirituElastic.setDescripcion(nuevaDescripcion);
        servEsp.actualizarDescripcion(espirituElastic);

        EspirituElastic eRecuperado = servEsp.recuperarElastic(marcelito.getId()).get();

        // Verify
        assertEquals(nuevaDescripcion, eRecuperado.getDescripcion());

    }
    @Test
    void testUnEspirituSeTeletransporta(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);

        //Exercise
        servEsp.teletransportar(ushuaia.getId(), marcelito.getId());

        Espiritu marcelitoRecuperado = servEsp.recuperar(marcelito.getId()).get();

        //Verify
        assertEquals(ushuaia.getId(), marcelitoRecuperado.getUbicacion().getId());
    }

    @Test
    void testUnEspirituQueNoEstaLibreNoSePuedeTeletransportar(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        yuno = servMed.crear(yuno, puntoBuenosAires, descripcion1);

        //Exercise
        servEsp.conectar(marcelito.getId(), yuno.getId());
        Espiritu marcelitoRecuperado = servEsp.recuperar(marcelito.getId()).get();

        //Verify
        assertThrows(NoSePuedeRealizarLaTeletransportacionException.class, () -> {
            servEsp.teletransportar(ushuaia.getId(), marcelitoRecuperado.getId());
        });
    }

    @Test
    void testUnEspirituConMenosDe50DeNDCNoSePuedeTeletransportar(){
        //SetUp
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);

        //Verify
        assertThrows(NoSePuedeRealizarLaTeletransportacionException.class, () -> {
            servEsp.teletransportar(ushuaia.getId(), dieguito.getId());
        });
    }

    @Test
    void testUnEspirituSeTelestransoporta(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);

        //Exercise
        marcelito.setNivelDeConexion(100);
        servEsp.actualizar(marcelito);
        servEsp.teletransportar(ushuaia.getId(), marcelito.getId());
        servEsp.teletransportar(buenosAires.getId(), marcelito.getId());
        servEsp.teletransportar(ushuaia.getId(), marcelito.getId());
        servEsp.teletransportar(buenosAires.getId(), marcelito.getId());
        servEsp.teletransportar( ushuaia.getId(), marcelito.getId());
        Espiritu marcelitoRecuperado = servEsp.recuperar(marcelito.getId()).get();

        //Verify
        assertEquals(ushuaia.getId(), marcelitoRecuperado.getUbicacion().getId());
        assertEquals(marcelitoRecuperado.getNivelDeConexion(), 75);

    }


    @Test
    void testHistorialDeTeletransportacionDeEspirituSeMuestraCorrectamente() {
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);

        //Exercise
        marcelito.setNivelDeConexion(100);
        servEsp.actualizar(marcelito);
        servEsp.teletransportar(ushuaia.getId(), marcelito.getId());
        servEsp.teletransportar(buenosAires.getId(), marcelito.getId());
        servEsp.teletransportar(ushuaia.getId(), marcelito.getId());
        servEsp.teletransportar(buenosAires.getId(), marcelito.getId());
        Espiritu marcelitoRecuperado = servEsp.recuperar(marcelito.getId()).get();

        HistorialTeletransportacion hsEsp = servEsp.historialDeMovimiento(marcelitoRecuperado.getId());

        //Verify
        assertEquals(buenosAires.getId(), marcelitoRecuperado.getUbicacion().getId());
        assertEquals(marcelitoRecuperado.getNombre(),hsEsp.getNombreEspiritu());
        assertEquals(4,hsEsp.getCantidadDeMovimientos());
        assertTrue(hsEsp.getIdsDeUbicacionesPorLasQueAnduvo().containsAll(Arrays.asList(ushuaia.getId(),buenosAires.getId())));
        assertEquals(hsEsp.getUbicacionMasConcurrida(), ushuaia.getId());
    }



    @AfterEach
    void tearDown() {
        databaseCleaner.cleanDatabase();
        neo4jDatabaseCleaner.cleanDatabase();
        mongoDBDatabaseCleaner.cleanDatabase();
        elasticSearchCleaner.cleanAllDocuments();
    }
}