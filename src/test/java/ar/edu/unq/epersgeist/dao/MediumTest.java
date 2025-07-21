package ar.edu.unq.epersgeist.dao;

import ar.edu.unq.epersgeist.dao.Utils.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.excepcion.*;
import ar.edu.unq.epersgeist.servicios.MediumService;
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

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class MediumTest {

    @Autowired
    private EspirituServiceImpl servEsp;

    @Autowired
    private MediumServiceImpl servMed;

    @Autowired
    private UbicacionServiceImpl servUbi;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private Neo4jDatabaseCleaner neo4jDatabaseCleaner;

    @Autowired
    private MongoDBDatabaseCleaner mongoDBDatabaseCleaner;

    @Autowired
    private ElasticSearchCleaner elasticSearchCleaner;

    private List<List<Double>> area1;
    private List<List<Double>> area2;
    private List<List<Double>> area3;

    private Ubicacion buenosAires;
    private Ubicacion ushuaia;

    private Espiritu marcelito;
    private Espiritu nezuko;
    private Espiritu dieguito;
    private Espiritu sid;
    private Espiritu debilcito;
    private Espiritu lieve;
    private Espiritu maki;
    private Espiritu muertito;

    private Medium merlin;
    private Medium meliodas;

    private GeoJsonPoint puntoBuenosAires;
    private GeoJsonPoint puntoUshuaia;

    private String descripcion1;

    @Autowired
    private MediumService mediumService;

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

        area1 = coordsPoligono1;
        area2 = coordsPoligono2;
        area3 = coordsPoligono3;

        descripcion1 = "Lorem ipsum dolor sit amet consectetur adipiscing elit nec per rutrum hac, accumsan bibendum fermentum massa cum suscipit malesuada rhoncus feugiat sapien taciti, cursus libero velit senectus nascetur vivamus varius nullam porttitor molestie.";

        buenosAires = new Ubicacion("Buenos Aires", 10, TipoUbicacion.CEMENTERIO);
        ushuaia = new Ubicacion("Ushuaia", 10, TipoUbicacion.SANTUARIO);

        buenosAires = servUbi.crear(buenosAires, area1, descripcion1);
        ushuaia = servUbi.crear(ushuaia, area2, descripcion1);

        merlin = new Medium("Merlin", 90, 35, buenosAires);
        meliodas = new Medium("Meliodas", 130, 60, ushuaia);

        marcelito = new Espiritu(TipoEspiritu.DEMONIO,50,"Marcelito", buenosAires);
        nezuko = new Espiritu(TipoEspiritu.DEMONIO,70,"Nezuko", buenosAires);
        dieguito = new Espiritu(TipoEspiritu.ANGEL,10,"Dieguito", buenosAires);
        sid = new Espiritu(TipoEspiritu.ANGEL,60,"Sid", buenosAires);
        debilcito = new Espiritu(TipoEspiritu.ANGEL,20,"Debilcito", buenosAires);
        lieve = new Espiritu(TipoEspiritu.DEMONIO,50,"Lieve", ushuaia);
        muertito = new Espiritu(TipoEspiritu.DEMONIO,1,"Muertito", buenosAires);
        maki = new Espiritu(TipoEspiritu.ANGEL,25,"Maki", ushuaia);

        servUbi.conectar(buenosAires.getId(), ushuaia.getId());
        servUbi.conectar(ushuaia.getId(), buenosAires.getId());

        puntoBuenosAires = new GeoJsonPoint(new Point(0.11,0.11));
        puntoUshuaia = new GeoJsonPoint(new Point(0.21,0.17));
    }

    @Test
    void testSeCreaUnMediumCorrectamente() {
        //Exercise
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Verify
        assertNotNull(merlin.getId());
        assertNotNull(meliodas.getId());
        assertNotNull(merlin.getCreatedAt());
        assertNotNull(meliodas.getCreatedAt());
    }

    @Test
    void testNoSeCreaUnMediumEnUnPuntoFueraDeSuUbicacion() {
        //Verify
        assertThrows(PuntoNoPerteneceException.class, () -> {
            servMed.crear(merlin, puntoUshuaia, descripcion1);});
    };

    @Test
    void testPersistirUnMediumYRecuperarlo(){
        //SetUp
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);

        //Exercise
        Optional<Medium> merlinRecuperadoOp = servMed.recuperar(merlin.getId());
        Medium merlinRecuperado = merlinRecuperadoOp.get();

        //Verify
        assertEquals(merlinRecuperado.getId(), merlin.getId());
        assertEquals(merlinRecuperado.getNombre(), merlin.getNombre());
        assertEquals(merlinRecuperado.getMana(), merlin.getMana());
    }

    @Test
    void testNoSePuedeRecuperarUnElementoInexistenteYTiraExcepcion(){
        //Exercise
        Optional<Medium> recuperado = servMed.recuperar(10000L);

        //Verify
        assertThrows(NoSuchElementException.class, () -> {
            recuperado.get();
        });
    }

    @Test
    void testRecuperarTodosLosMediums() {
        //SetUp
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Exercise
        List<Medium> mediumActuales = servMed.recuperarTodos();
        List<Long> idsMediums = mediumActuales.stream().map(Medium::getId).toList();

        //Verify
        assertTrue(idsMediums.contains(merlin.getId()));
        assertTrue(idsMediums.contains(meliodas.getId()));
    }

    @Test
    void testSeCreanDosMediumsYUnoSeEliminaCorrectamente() {
        //SetUp
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Exercise
        servMed.eliminar(merlin.getId());
        List<Medium> mediumsActuales = servMed.recuperarTodos();

        //Verify
        assertEquals(1, mediumsActuales.size());
        assertEquals(mediumsActuales.getFirst().getId(),meliodas.getId());
    }

    @Test
    void testAcutalizarMedium(){
        //SetUp
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);

        //Exercise
        merlin.setNombre("Hola");
        servMed.actualizar(merlin);

        Optional<Medium> mediumActualizadoOp = servMed.recuperar(merlin.getId());
        Medium mediumActualizado = mediumActualizadoOp.get();

        //Verify
        assertEquals("Hola", mediumActualizado.getNombre());
        assertNotEquals(merlin.getCreatedAt(), mediumActualizado.getUpdateAt());
    }

    @Test
   void testNoSePuedeActualizarManaNegativoYQuedaEn0(){
        //SetUp
        merlin= servMed.crear(merlin, puntoBuenosAires, descripcion1);

        //Exercise
        merlin.setMana(-100);
        servMed.actualizar(merlin);

        Optional<Medium> mediumActualizadoOp = servMed.recuperar(merlin.getId());
        Medium mediumActualizado = mediumActualizadoOp.get();

        //Verify
        assertEquals(0, mediumActualizado.getMana());
    }

    @Test
    void testNoSePuedeActualizarManaMaxNegativoYQuedaEn0(){
        //SetUp
        merlin= servMed.crear(merlin, puntoBuenosAires, descripcion1);

        //Exercise
        merlin.setManaMax(-100);
        servMed.actualizar(merlin);

        Optional<Medium> mediumActualizadoOp = servMed.recuperar(merlin.getId());
        Medium mediumActualizado = mediumActualizadoOp.get();

        //Verify
        assertEquals(0, mediumActualizado.getManaMax());
    }

    @Test
    void testNoSePuedeActualizarManaMayorAManaMaxYQuedaEnManaMax(){
        //SetUp
        merlin= servMed.crear(merlin, puntoBuenosAires, descripcion1);

        //Exercise
        merlin.setManaMax(100);
        merlin.setMana(200);
        servMed.actualizar(merlin);

        Optional<Medium> mediumActualizadoOp = servMed.recuperar(merlin.getId());
        Medium mediumActualizado = mediumActualizadoOp.get();

        //Verify
        assertEquals(100, mediumActualizado.getMana());
    }

    @Test
    void testMediumDescansaEnCementarioYSusEspiritusTambien() {
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);
        sid = servEsp.crear(sid, puntoBuenosAires, descripcion1);
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);

        servEsp.conectar(marcelito.getId(),merlin.getId());
        servEsp.conectar(dieguito.getId(),merlin.getId());
        servEsp.conectar(sid.getId(),merlin.getId());

        //Exercise
        servMed.descansar(merlin.getId());

        //Verify
        Medium merlinDescansado = servMed.recuperar(merlin.getId()).get();
        assertEquals(40, merlinDescansado.getMana());

        Espiritu marceloActualizado = servEsp.recuperar(marcelito.getId()).get();
        assertEquals(67, marceloActualizado.getNivelDeConexion());

        Espiritu dieguitoActualizado = servEsp.recuperar(dieguito.getId()).get();
        assertEquals(17, dieguitoActualizado.getNivelDeConexion());

        Espiritu sidActualizado = servEsp.recuperar(sid.getId()).get();
        assertEquals(67, sidActualizado.getNivelDeConexion());
    }

    @Test
    void testMediumDescansaEnSantuarioYSusEspiritusTambien() {

        //SetUp
        lieve = servEsp.crear(lieve, puntoUshuaia, descripcion1);
        maki = servEsp.crear(maki, puntoUshuaia, descripcion1);
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        servEsp.conectar(lieve.getId(),meliodas.getId());
        servEsp.conectar(maki.getId(),meliodas.getId());

        //Exercise
        servMed.descansar(meliodas.getId());

        //Verify
        Medium meliodasDescansado = servMed.recuperar(meliodas.getId()).get();
        assertEquals(75, meliodasDescansado.getMana());

        Espiritu lieveActualizado = servEsp.recuperar(lieve.getId()).get();
        assertEquals(62, lieveActualizado.getNivelDeConexion());

        Espiritu makiActualizado = servEsp.recuperar(maki.getId()).get();
        assertEquals(47, makiActualizado.getNivelDeConexion());
    }


    @Test
    void testUnMediumSinEspiritusDescansaYRecuperaMana(){
        //SetUp
        Medium nicole = new Medium("Nicole", 100, 80, buenosAires);
        nicole = servMed.crear(nicole, puntoBuenosAires, descripcion1);

        //Exercise
        servMed.descansar(nicole.getId());
        Medium mediumActualizado = servMed.recuperar(nicole.getId()).get();

        //Verify
        assertEquals(85, mediumActualizado.getMana());
    }

    @Test
    void testNoSePuedeRealizarElDescansoSinUnMedium(){
        //Verify
        assertThrows(RuntimeException.class, () -> {
            servMed.descansar(500L);
        });
    }

    @Test
    void testMediumDescansaYSuperaNiveleDeManaMaximo(){
        //SetUp
        Medium nicole = new Medium("Nicole", 5, 1, buenosAires);
        nicole = servMed.crear(nicole, puntoBuenosAires, descripcion1);

        //Exercise
        servMed.descansar(nicole.getId());
        Medium mediumActualizado = servMed.recuperar(nicole.getId()).get();

        //Verify
        assertEquals(5, mediumActualizado.getMana());
    }

    @Test
    void testUnMediumConectaAEspiritusYSonRecuperados() {
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);
        sid = servEsp.crear(sid, puntoBuenosAires, descripcion1);
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);

        servEsp.conectar(marcelito.getId(),merlin.getId());
        servEsp.conectar(dieguito.getId(),merlin.getId());
        servEsp.conectar(sid.getId(),merlin.getId());


        //Exercise
        List<Long> espiritus = Arrays.asList(
                marcelito.getId(),
                dieguito.getId(),
                sid.getId());

        List<Long> medEspiritus = servMed.espiritus(merlin.getId())
                .stream()
                .map(Espiritu::getId)
                .toList();

        //Verify
        assertTrue(medEspiritus.containsAll(espiritus));
    }

    @Test
    void testDosMediumsConectanAEspiritusYSonRecuperadosCorrectamenteSegunCadaMediumEnSuUbicacion() {
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);
        Espiritu leonidas = new Espiritu(TipoEspiritu.DEMONIO,99,"Leonidas", ushuaia);
        leonidas = servEsp.crear(leonidas, puntoUshuaia, descripcion1);
        Espiritu kratos = new Espiritu(TipoEspiritu.DEMONIO,99,"Kratos", ushuaia);
        kratos = servEsp.crear(kratos, puntoUshuaia, descripcion1);
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        servEsp.conectar(marcelito.getId(),merlin.getId());
        servEsp.conectar(dieguito.getId(),merlin.getId());

        servEsp.conectar(leonidas.getId(),meliodas.getId());
        servEsp.conectar(kratos.getId(),meliodas.getId());

        //Exercise
        List<Long> espiritusDeMerlin = Arrays.asList(
                marcelito.getId(),
                dieguito.getId()
        );
        List<Long> espiritusDeMeliodas = Arrays.asList(
                leonidas.getId(),
                kratos.getId()
        );

        List<Long> espRecuperadosMerlin = servMed.espiritus(merlin.getId())
                .stream()
                .map(Espiritu::getId)
                .toList();

        List<Long> espRecuperadosMeliodas = servMed.espiritus(meliodas.getId())
                .stream()
                .map(Espiritu::getId)
                .toList();

        //Verify
        assertTrue(espiritusDeMerlin.containsAll(espRecuperadosMerlin));
        assertTrue(espiritusDeMeliodas.containsAll(espRecuperadosMeliodas));
    }

    @Test
    void testUnMediumSeLePidenSusEspiritusPeroNoTieneNinguno() {
        //SetUp
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);

        //Exercise
        List<Espiritu> espiritusMerlin = servMed.espiritus(merlin.getId());

        //Verify
        assertTrue(espiritusMerlin.isEmpty());
    }

    @Test
    void mediumConManaSuficienteInvocaEspirituLibre(){
        //SetUp
        sid = servEsp.crear(sid, puntoBuenosAires, descripcion1);
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);
        int mana = meliodas.getMana();

        //Exercise
        Espiritu sidInvocado = servMed.invocar(meliodas.getId(), sid.getId());

        //Verify
        assertEquals(sidInvocado.getId(), sid.getId());
        assertFalse(meliodas.estaEnLaMismaUbicacionQueEspiritu(sid));
        assertEquals(servMed.recuperar(meliodas.getId()).get().getMana(), mana - 10);
        assertTrue(meliodas.estaEnLaMismaUbicacionQueEspiritu(sidInvocado));

    }

    @Test
    void mediumConManaSuficienteInvocaEspirituQueNoEstaLibre(){
        //SetUp
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);
        sid.setMedium(merlin);
        sid = servEsp.crear(sid, puntoBuenosAires, descripcion1);
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Exercise
        EspirituNoPuedeSerInvocado ex = assertThrows(EspirituNoPuedeSerInvocado.class, () -> {
            servMed.invocar(meliodas.getId(), sid.getId());
        });

        //Verify
        assertEquals("Sid no está libre para ser invocado.", ex.getMessage());
    }

    @Test
    void mediumConManaInsuficienteInvocaEspirituLibre(){
        //SetUp
        sid = servEsp.crear(sid, puntoBuenosAires, descripcion1);
        meliodas.setMana(5);
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);
        int mana = meliodas.getMana();

        //Exercise
        InvocacionFallidaException ex = assertThrows(InvocacionFallidaException.class, () -> {
            servMed.invocar(meliodas.getId(), sid.getId());
        });

        //Verify
        assertEquals("El medium no tiene suficiente maná o la Ubicacion no es del tipo correcto", ex.getMessage());
        assertEquals(buenosAires.getId(), sid.getUbicacion().getId() );
        assertEquals(servMed.recuperar(meliodas.getId()).get().getMana(), mana);
    }

    @Test
    void mediumConManaSuficienteInvocaEspirituLibreDeTipoEquivocado(){
        //SetUp
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        int mana = meliodas.getMana();

        //Exercise
        InvocacionFallidaException ex = assertThrows(InvocacionFallidaException.class, () -> {
            servMed.invocar(meliodas.getId(), marcelito.getId());
        });

        //Verify
        assertEquals("El medium no tiene suficiente maná o la Ubicacion no es del tipo correcto", ex.getMessage());
        assertEquals(buenosAires.getId(), marcelito.getUbicacion().getId() );
        assertEquals(servMed.recuperar(meliodas.getId()).get().getMana(), mana);
    }

    @Test
    void unMediumNoPuedeExorcizarAOtroSiNoEstanEnLaMismaUbicacion(){
       //SetUp
        //Exorcista
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);
        //A exorcizar
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Verify
        assertThrows(UbicacionDistintaEntreMediums.class, () -> {
            servMed.exorcizar(merlin.getId(), meliodas.getId());
        });

    }

    @Test
    void testMediumExorsizaConUnAngelAUnMediumConUnDemonioConAtaqueExitosoSpring(){
        //SetUp
        GeneradorDeNumerosAleatorios generador = GeneradorDeNumerosAleatorios.getGeneradorDeNumerosAleatorios();
        EstrategiaGeneracionDeNumeros EstrategiaDeterminista = new EstrategiaDeterminista();
        generador.setEstrategia(EstrategiaDeterminista);

        Espiritu walleDemon = new Espiritu(TipoEspiritu.DEMONIO,50,"DemonWalle", ushuaia);
        Espiritu walleAngel = new Espiritu(TipoEspiritu.ANGEL,60,"Walle", ushuaia);

        Medium hijitus = new Medium("Hijitus", 90, 35, ushuaia);
        Medium larguiruch = new Medium("Larguiruch", 130, 20, ushuaia);

        //Exorcista
        hijitus = servMed.crear(hijitus, puntoUshuaia, descripcion1);
        //A exorcizar
        larguiruch = servMed.crear(larguiruch, puntoUshuaia, descripcion1);

        //Tipo angel
        walleAngel = servEsp.crear(walleAngel, puntoUshuaia, descripcion1);
        //Tipo demonio
        walleDemon = servEsp.crear(walleDemon, puntoUshuaia, descripcion1);

        //Conectar esiritus

        hijitus = servEsp.conectar(walleAngel.getId(), hijitus.getId());
        larguiruch = servEsp.conectar(walleDemon.getId(), larguiruch.getId());


        Medium mHijRecuperado = servMed.recuperar(hijitus.getId()).get();
        Medium mLarRecuperado = servMed.recuperar(larguiruch.getId()).get();

        //Exercise
        servMed.exorcizar(mHijRecuperado.getId(), mLarRecuperado.getId());

        Espiritu eWallDemRecuperado = servEsp.recuperar(walleDemon.getId()).get();
        Espiritu eWallAngRecuperado = servEsp.recuperar(walleAngel.getId()).get();

        //Verify
        assertEquals(21, eWallDemRecuperado.getNivelDeConexion());
        assertEquals(67, eWallAngRecuperado.getNivelDeConexion());
    }

    @Test
    void testMediumExorsizaConUnAngelAUnMediumConUnDemonioConAtaqueExitoso(){
        //SetUp
        GeneradorDeNumerosAleatorios generador = GeneradorDeNumerosAleatorios.getGeneradorDeNumerosAleatorios();
        EstrategiaGeneracionDeNumeros EstrategiaDeterminista = new EstrategiaDeterminista();
        generador.setEstrategia(EstrategiaDeterminista);

        //Exorcista
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);
        //A exorcizar
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Tipo demonio
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        //Tipo angel
        sid = servEsp.crear(sid, puntoBuenosAires, descripcion1);

        //Invocan a los espiritus
        servMed.mover(merlin.getId(), 0.17, 0.21);
        sid = servMed.invocar(merlin.getId(), sid.getId());
        servMed.mover(meliodas.getId(), 0.11, 0.11);
        marcelito = servMed.invocar(meliodas.getId(), marcelito.getId());

        //Conectar esiritus
        merlin = servEsp.conectar(sid.getId(), merlin.getId());
        meliodas = servEsp.conectar(marcelito.getId(), meliodas.getId());

        Medium mMerRecuperado = servMed.recuperar(merlin.getId()).get();
        Medium mMelRecuperado = servMed.recuperar(meliodas.getId()).get();

        //Exercise
        servMed.mover(meliodas.getId(), 0.17, 0.21);
        servMed.exorcizar(mMerRecuperado.getId(), mMelRecuperado.getId());

        Espiritu eMarRecuperado = servEsp.recuperar(marcelito.getId()).get();
        Espiritu eSidRecuperado = servEsp.recuperar(sid.getId()).get();

        //Verify
        assertEquals(18, eMarRecuperado.getNivelDeConexion());
        assertEquals(65, eSidRecuperado.getNivelDeConexion());
    }

    @Test
    void testMediumNoPuedeExorsizaSinAngeles(){
        //SetUp
        GeneradorDeNumerosAleatorios generador = GeneradorDeNumerosAleatorios.getGeneradorDeNumerosAleatorios();
        EstrategiaGeneracionDeNumeros EstrategiaDeterminista = new EstrategiaDeterminista();
        generador.setEstrategia(EstrategiaDeterminista);

        //Exorcista
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);
        //A exorcizar
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Tipo demonio
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        nezuko = servEsp.crear(nezuko, puntoBuenosAires, descripcion1);

        //Invocan a los espiritus
        nezuko = servMed.invocar(merlin.getId(), nezuko.getId());
        servMed.mover(meliodas.getId(), 0.11, 0.11);
        marcelito = servMed.invocar(meliodas.getId(), marcelito.getId());

        //Conectar esiritus
        merlin = servEsp.conectar(nezuko.getId(), merlin.getId());
        meliodas = servEsp.conectar(marcelito.getId(), meliodas.getId());

        Medium mMerRecuperado = servMed.recuperar(merlin.getId()).get();
        Medium mMelRecuperado = servMed.recuperar(meliodas.getId()).get();

        //Verify
        assertThrows(ExorcistaSinAngelesException.class, () -> {
            servMed.exorcizar(mMerRecuperado.getId(), mMelRecuperado.getId());
        });
    }

    @Test
    void testMediumExorsizaAOtroAmbosConListasMixtasYSoloCambianLosEspiritusCorrespondientes(){
        //SetUp
        GeneradorDeNumerosAleatorios generador = GeneradorDeNumerosAleatorios.getGeneradorDeNumerosAleatorios();
        EstrategiaGeneracionDeNumeros EstrategiaDeterminista = new EstrategiaDeterminista();
        generador.setEstrategia(EstrategiaDeterminista);

        //Exorcista
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);

        //A exorcizar
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Tipo demonio
        Espiritu pedrito = new Espiritu(TipoEspiritu.DEMONIO,5,"Pedrito", ushuaia);
        pedrito = servEsp.crear(pedrito, puntoUshuaia, descripcion1);
        nezuko = servEsp.crear(nezuko, puntoBuenosAires, descripcion1);

        //Tipo angel
        sid = servEsp.crear(sid, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);

        //Invocan y se conectan a los espiritus
        nezuko = servMed.invocar(merlin.getId(), nezuko.getId());
        merlin = servEsp.conectar(nezuko.getId(), merlin.getId());

        servMed.mover(merlin.getId(), 0.17, 0.21);
        sid = servMed.invocar(merlin.getId(), sid.getId());
        merlin = servEsp.conectar(sid.getId(), merlin.getId());

        dieguito = servMed.invocar(meliodas.getId(), dieguito.getId());
        meliodas = servEsp.conectar(dieguito.getId(), meliodas.getId());
        servMed.mover(meliodas.getId(), 0.11, 0.11);
        pedrito = servMed.invocar(meliodas.getId(), pedrito.getId());
        meliodas = servEsp.conectar(pedrito.getId(), meliodas.getId());

        Medium mMerRecuperado = servMed.recuperar(merlin.getId()).get();
        Medium mMelRecuperado = servMed.recuperar(meliodas.getId()).get();

        //Exercise
        servMed.mover(merlin.getId(), 0.11, 0.11);
        servMed.exorcizar(mMerRecuperado.getId(), mMelRecuperado.getId());

        Espiritu ePedRecuperado = servEsp.recuperar(pedrito.getId()).get();
        Espiritu eSidRecuperado = servEsp.recuperar(sid.getId()).get();
        Espiritu eNezRecuperado = servEsp.recuperar(nezuko.getId()).get();
        Espiritu eDiegRecuperado = servEsp.recuperar(dieguito.getId()).get();

        //Verify
        assertEquals(0, ePedRecuperado.getNivelDeConexion()); //Demonio derrotado
        assertEquals(58, eSidRecuperado.getNivelDeConexion()); //Angel ganador (60 base + 3 por conectar - 5 por mover)
        assertEquals(65, eNezRecuperado.getNivelDeConexion()); //Demonio del exorcista (70 base + 5 por conectar - 10 por mover, no participa)
        assertEquals(15, eDiegRecuperado.getNivelDeConexion()); //Angel del exorcizado (10 base + 10 de conectar - 5 por mover, no participa)
        assertTrue(ePedRecuperado.estaLibre());
    }

    @Test
    void testMediumExorsizaConUnAngelAUnMediumConUnDemonioConDefensaExitosa(){
        //SetUp
        GeneradorDeNumerosAleatorios generador = GeneradorDeNumerosAleatorios.getGeneradorDeNumerosAleatorios();
        EstrategiaGeneracionDeNumeros EstrategiaDeterminista = new EstrategiaDeterminista();
        generador.setEstrategia(EstrategiaDeterminista);

        //Exorcista
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);

        //A exorcizar
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Tipo demonio
        nezuko = servEsp.crear(nezuko, puntoBuenosAires, descripcion1);

        //Tipo angel
        debilcito = servEsp.crear(debilcito, puntoBuenosAires, descripcion1);

        //Invocan a los espiritus
        servMed.mover(merlin.getId(), 0.17, 0.21);
        debilcito = servMed.invocar(merlin.getId(), debilcito.getId());
        servMed.mover(meliodas.getId(), 0.11, 0.11);
        nezuko = servMed.invocar(meliodas.getId(), nezuko.getId());

        //Conectar esiritus
        merlin = servEsp.conectar(debilcito.getId(), merlin.getId());
        meliodas = servEsp.conectar(nezuko.getId(), meliodas.getId());

        Medium mMerRecuperado = servMed.recuperar(merlin.getId()).get();
        Medium mMelRecuperado = servMed.recuperar(meliodas.getId()).get();

        //Exercise
        servMed.mover(merlin.getId(), 0.11, 0.11);
        servMed.exorcizar(mMerRecuperado.getId(), mMelRecuperado.getId());

        Espiritu eNezRecuperado = servEsp.recuperar(nezuko.getId()).get();
        Espiritu eDebRecuperado = servEsp.recuperar(debilcito.getId()).get();

        //Verify
        assertEquals(80, eNezRecuperado.getNivelDeConexion());
        assertEquals(15, eDebRecuperado.getNivelDeConexion());
    }
    @Test
    void testMediumExorsizaConDosAngelesAUnMediumConUnDemonioYUnoDeLosAngelesMuere(){
        //SetUp
        GeneradorDeNumerosAleatorios generador = GeneradorDeNumerosAleatorios.getGeneradorDeNumerosAleatorios();
        EstrategiaGeneracionDeNumeros EstrategiaDeterminista = new EstrategiaDeterminista();
        generador.setEstrategia(EstrategiaDeterminista);

        Espiritu marcus = new Espiritu(TipoEspiritu.ANGEL,1,"Marcus", buenosAires);

        //Exorcista
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);
        //A exorcizar
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Tipo demonio
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        marcus = servEsp.crear(marcus, puntoBuenosAires, descripcion1);

        //Tipo angel
        sid = servEsp.crear(sid, puntoBuenosAires, descripcion1);

        //Invocan a los espiritus
        servMed.mover(merlin.getId(), 0.17, 0.21);
        sid = servMed.invocar(merlin.getId(), sid.getId()); // 35 - 10 = 25 //Merlin
        marcus = servMed.invocar(merlin.getId(),marcus.getId()); //25-10 = 15 //Merlin

        servMed.mover(meliodas.getId(), 0.11, 0.11);
        marcelito = servMed.invocar(meliodas.getId(), marcelito.getId()); //60-10 = 50 //Meliodas

        //Conectar esiritus
        merlin = servEsp.conectar(sid.getId(), merlin.getId()); //60 + 3(20% de mana Merlin) = 63 //sid
        meliodas = servEsp.conectar(marcelito.getId(), meliodas.getId()); // 50 + 8(20% de Meliodas) = 58 //marcelito
        merlin = servEsp.conectar(marcus.getId(),merlin.getId()); // 1 + 3(20% de Merlin) = 4 //marcus

        Medium mMerRecuperado = servMed.recuperar(merlin.getId()).get();
        Medium mMelRecuperado = servMed.recuperar(meliodas.getId()).get();

        //Exercise
        servMed.mover(meliodas.getId(), 0.17, 0.21);
        servMed.exorcizar(mMerRecuperado.getId(), mMelRecuperado.getId()); //4 - 5 = -1 (queda en 0) //Marcus

        Espiritu eMarRecuperado = servEsp.recuperar(marcelito.getId()).get();
        Espiritu eMarcRecuperado = servEsp.recuperar(marcus.getId()).get();
        Espiritu eSidRecuperado = servEsp.recuperar(sid.getId()).get();

        //Verify
        assertEquals(0, eMarcRecuperado.getNivelDeConexion());
        assertTrue(eMarcRecuperado.estaLibre());
        assertEquals(19, eMarRecuperado.getNivelDeConexion());
        assertEquals(63, eSidRecuperado.getNivelDeConexion());
    }

    @Test
    void testMediumExorsizaAOtroUnDemonioMuereYElSegundoEsAtacado(){
        //SetUp
        GeneradorDeNumerosAleatorios generador = GeneradorDeNumerosAleatorios.getGeneradorDeNumerosAleatorios();
        EstrategiaGeneracionDeNumeros EstrategiaDeterminista = new EstrategiaDeterminista();
        generador.setEstrategia(EstrategiaDeterminista);

        //Exorcista
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);

        //A exorcizar
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Tipo demonio
        Espiritu pedrito = new Espiritu(TipoEspiritu.DEMONIO,5,"Pedrito", ushuaia);
        pedrito = servEsp.crear(pedrito, puntoUshuaia, descripcion1);
        Espiritu debilucho = new Espiritu(TipoEspiritu.DEMONIO,5,"Debilucho", ushuaia);
        debilucho = servEsp.crear((new Espiritu(TipoEspiritu.DEMONIO,5,"Debilucho", ushuaia)), puntoUshuaia, descripcion1);

        //Tipo angel
        Espiritu raulito = new Espiritu(TipoEspiritu.ANGEL,100,"Raulito", ushuaia);
        raulito = servEsp.crear(raulito, puntoUshuaia, descripcion1);
        sid = servEsp.crear(sid, puntoBuenosAires, descripcion1);

        //Invocan a los espiritus
        servMed.mover(merlin.getId(), 0.17, 0.21);
        sid = servMed.invocar(merlin.getId(), sid.getId());
        raulito = servMed.invocar(merlin.getId(), raulito.getId());

        servMed.mover(meliodas.getId(), 0.11, 0.11);
        pedrito = servMed.invocar(meliodas.getId(), pedrito.getId());
        debilucho = servMed.invocar(meliodas.getId(), debilucho.getId());

        //Conectar esiritus
        merlin = servEsp.conectar(sid.getId(), merlin.getId());
        merlin = servEsp.conectar(raulito.getId(), merlin.getId());

        meliodas = servEsp.conectar(pedrito.getId(), meliodas.getId());
        meliodas = servEsp.conectar(debilucho.getId(), meliodas.getId());

        Medium mMerRecuperado = servMed.recuperar(merlin.getId()).get();
        Medium mMelRecuperado = servMed.recuperar(meliodas.getId()).get();

        //Exercise
        servMed.mover(merlin.getId(), 0.11, 0.11);
        servMed.exorcizar(mMerRecuperado.getId(), mMelRecuperado.getId());

        Espiritu ePedRecuperado = servEsp.recuperar(pedrito.getId()).get();
        Espiritu eSidRecuperado = servEsp.recuperar(sid.getId()).get();
        Espiritu eDebRecuperado = servEsp.recuperar(debilucho.getId()).get();
        Espiritu eRaulRecuperado = servEsp.recuperar(raulito.getId()).get();

        //Verify
        assertEquals(0, ePedRecuperado.getNivelDeConexion()); //Demonio derrotado
        assertEquals(58, eSidRecuperado.getNivelDeConexion()); //Angel ganador (60 base + 3 por conectar -5 por mover)
        assertEquals(0, eDebRecuperado.getNivelDeConexion()); //Segundo demonio (termina con menos nivel de conexion por recibir ataque)
        assertEquals(95, eRaulRecuperado.getNivelDeConexion()); //Segundo angel (100base - 5 por mover,  ataca al segundo demonio y acierta)
        assertTrue(ePedRecuperado.estaLibre());
    }

    @Test
    void seDesvinculaDemonioAlTenerNivelDeConexion0(){
        //SetUp
        GeneradorDeNumerosAleatorios generador = GeneradorDeNumerosAleatorios.getGeneradorDeNumerosAleatorios();
        EstrategiaGeneracionDeNumeros EstrategiaDeterminista = new EstrategiaDeterminista();
        generador.setEstrategia(EstrategiaDeterminista);

        //Exorcista
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);

        //A exorcizar
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        //Tipo demonio
        Espiritu pepe = new Espiritu(TipoEspiritu.DEMONIO,5,"Pepe", buenosAires);
        pepe = servEsp.crear(pepe, puntoBuenosAires, descripcion1);

        //Tipo angel
        Espiritu jorge = new Espiritu(TipoEspiritu.ANGEL,100,"Jorge", buenosAires);
        jorge = servEsp.crear(jorge, puntoBuenosAires, descripcion1);


        //Conectar esiritus
        merlin = servEsp.conectar(jorge.getId(), merlin.getId());

        servMed.mover(meliodas.getId(), 0.11, 0.11);
        meliodas = servEsp.conectar(pepe.getId(), meliodas.getId());

        //Recupero el actualizado
        Medium merlinRecuperado = servMed.recuperar(merlin.getId()).get();
        Medium meliodasRecuperado = servMed.recuperar(meliodas.getId()).get();

        //Exercise
        servMed.exorcizar(merlinRecuperado.getId(), meliodasRecuperado.getId());

        Espiritu pepeRecuperado = servEsp.recuperar(pepe.getId()).get();

        //Verify
        //Verifica si el nivel de conexion de Pepe es 0 (Derrotado)
        assertEquals(0, pepeRecuperado.getNivelDeConexion());
        //Verifica que el demonio esta libre
        assertTrue(pepeRecuperado.estaLibre());
        //Verifica que el med no tenga al demonio
        assertTrue(meliodasRecuperado.getEspiritus().isEmpty());
    }

    @Test
    void unMediumYSusEspiritusSeMuevenAUnCementerioPerdiendoElAngel5DeNDC() {
        // Setup
        lieve = servEsp.crear(lieve, puntoUshuaia, descripcion1); // 50 ndc D S
        maki = servEsp.crear(maki, puntoUshuaia, descripcion1); // 25 ndc A S
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1); // 60 mana S

        servEsp.conectar(lieve.getId(), meliodas.getId()); // 62 ndc
        servEsp.conectar(maki.getId(), meliodas.getId()); // 37 ndc

        Medium mRecuperado = servMed.recuperar(meliodas.getId()).get();

        // Exercise
        servMed.mover(mRecuperado.getId(), 0.11, 0.11);

        Espiritu makiR = servEsp.recuperar(maki.getId()).get();
        Espiritu lieveR = servEsp.recuperar(lieve.getId()).get();
        Medium mRecuperado2 = servMed.recuperar(meliodas.getId()).get();

        // Verify
        assertEquals(32, makiR.getNivelDeConexion()); // Maki se mueve de S a C siendo A y queda con 32 ndc (-5)
        assertEquals(62, lieveR.getNivelDeConexion()); // Queda igual
        assertEquals(mRecuperado2.getUbicacion().getId(), buenosAires.getId());
        assertEquals(makiR.getUbicacion().getId(), buenosAires.getId());
        assertEquals(lieveR.getUbicacion().getId(), buenosAires.getId());
    }

    @Test
    void unMediumYSusEspiritusSeMuevenAUnSantuarioPerdiendoElDemonio10DeNDC() {
        // Setup
        sid = servEsp.crear(sid, puntoBuenosAires, descripcion1); // 60 ndc A C
        nezuko = servEsp.crear(nezuko, puntoBuenosAires, descripcion1); // 70 ndc D C
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1); // 35 mana C

        servEsp.conectar(sid.getId(), merlin.getId()); // 67 ndc
        servEsp.conectar(nezuko.getId(), merlin.getId()); // 77 ndc

        Medium mRecuperado = servMed.recuperar(merlin.getId()).get();

        // Exercise
        servMed.mover(mRecuperado.getId(), 0.17, 0.21);

        Espiritu nezukoR = servEsp.recuperar(nezuko.getId()).get();
        Espiritu sidR = servEsp.recuperar(sid.getId()).get();
        Medium mRecuperado2 = servMed.recuperar(merlin.getId()).get();

        // Verify
        assertEquals(67, nezukoR.getNivelDeConexion()); // Nezuko se mueve de C a S siendo D y queda con 67 ndc (-10)
        assertEquals(67, sidR.getNivelDeConexion()); // Queda igual
        assertEquals(mRecuperado2.getUbicacion().getId(), ushuaia.getId());
        assertEquals(nezukoR.getUbicacion().getId(), ushuaia.getId());
        assertEquals(sidR.getUbicacion().getId(), ushuaia.getId());
    }

    @Test
    void unMediumYSusEspiritusSeMuevenAUnSantuarioPerdiendoLaConexionDelDemonio() {
        // Setup
        sid = servEsp.crear(sid, puntoBuenosAires, descripcion1); // 60 ndc A C
        muertito = servEsp.crear(muertito, puntoBuenosAires, descripcion1); // 1 ndc D C
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1); // 35 mana C

        servEsp.conectar(sid.getId(), merlin.getId()); // 67 ndc
        servEsp.conectar(muertito.getId(), merlin.getId()); // 9 ndc

        Medium mRecuperado = servMed.recuperar(merlin.getId()).get();

        // Exercise
        servMed.mover(mRecuperado.getId(), 0.17,0.21 );

        Espiritu muertitoR = servEsp.recuperar(muertito.getId()).get();
        Espiritu sidR = servEsp.recuperar(sid.getId()).get();
        Medium mRecuperado2 = servMed.recuperar(merlin.getId()).get();

        // Verify
        assertEquals(0, muertitoR.getNivelDeConexion()); // Muertito se mueve de C a S siendo D y termina desconectandose por la perdida de ndc
        assertEquals(1, mRecuperado2.getEspiritus().size());
        assertTrue(muertitoR.estaLibre());
        assertEquals(mRecuperado2.getUbicacion().getId(), ushuaia.getId());
        assertEquals(muertitoR.getUbicacion().getId(), ushuaia.getId());
        assertEquals(sidR.getUbicacion().getId(), ushuaia.getId());
    }

    @Test
    void seIntentaCrearUnMediumConManaFueraDeRango() {
        //Verify
        assertThrows(ManaFueraDeRangoException.class, () -> {
            new Medium("Pikachu", 90, -1, buenosAires);
        });
        assertThrows(ManaFueraDeRangoException.class, () -> {
            new Medium("Charizard", 90, 100, buenosAires);
        });
    }

    @Test
    void seEliminaUnMediumConectadoAEspiritusYFalla(){
        // Setup
        marcelito = servEsp.crear(marcelito, puntoBuenosAires, descripcion1);
        dieguito = servEsp.crear(dieguito, puntoBuenosAires, descripcion1);
        merlin = servMed.crear(merlin, puntoBuenosAires, descripcion1);

        servEsp.conectar(marcelito.getId(), merlin.getId());
        servEsp.conectar(dieguito.getId(), merlin.getId());

        //Verify
        assertThrows(EntidadAELiminarEstaRelacionada.class, () -> {
            servMed.eliminar(merlin.getId());
        });
    }

    @Test
    void unMediumNoSePuedeMoverAUnaUbicacionLejana() {
        List<List<Double>> coordsPoligono3 = Arrays.asList(
                Arrays.asList(0.15, 0.20),
                Arrays.asList(0.15, 0.38),
                Arrays.asList(0.34, 0.38),
                Arrays.asList(0.34, 0.20),
                Arrays.asList(0.15, 0.20)
        );

        // Ubicación lejana se refiere a una ubicación a más de un salto de distancia.

        // Setup
        Ubicacion bariloche = new Ubicacion("Bariloche", 30, TipoUbicacion.SANTUARIO);
        bariloche = servUbi.crear(bariloche, coordsPoligono3, descripcion1);

        servUbi.conectar(buenosAires.getId(), bariloche.getId());
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        // Exercise
        assertThrows(UbicacionLejanaException.class, () -> {
            servMed.mover(meliodas.getId(), 0.22, 0.16);
        });
    }

    @Test
    void unMediumSePuedeMoverAUnaUbicacionCercana() {
        // Ubicacion cercana se refiere a una ubicacion a un salto de distancia.

        List<List<Double>> coordsPoligono3 = Arrays.asList(
                Arrays.asList(0.15, 0.20),
                Arrays.asList(0.15, 0.38),
                Arrays.asList(0.34, 0.38),
                Arrays.asList(0.34, 0.20),
                Arrays.asList(0.15, 0.20)
        );



        // Setup
        Ubicacion bariloche = new Ubicacion("Bariloche", 30, TipoUbicacion.SANTUARIO);
        bariloche = servUbi.crear(bariloche, coordsPoligono3, descripcion1);

        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);
        servUbi.conectar(ushuaia.getId(), bariloche.getId());
        Ubicacion finalBariloche = bariloche;

        // Exercise
        servMed.mover(meliodas.getId(), 0.22, 0.16);
        Medium mRecuperado = servMed.recuperar(meliodas.getId()).get();

        assertEquals(mRecuperado.getUbicacion().getId(), finalBariloche.getId());
    }

    @Test
    void mediumQuiereMoverseAMasDe30KMYNoPuede() {
        // Setup
        GeoJsonPoint punto = new GeoJsonPoint(0.38, 0.19);
        meliodas = servMed.crear(meliodas, punto, descripcion1);


        // Verify
        assertThrows(UbicacionLejanaException.class, () -> {
            servMed.mover(meliodas.getId(), 0.0, 0.0);
        });
    }

    @Test
    void testInvocarEspirituFueraDelRangoDeDistanciaLanzaExcepcion() {
        //Setup
        List<List<Double>> coordsPoligonoGrande = Arrays.asList(
                Arrays.asList(0.0, -1.0),
                Arrays.asList(2.0, -1.0),
                Arrays.asList(2.0, -3.0),
                Arrays.asList(0.0, -3.0),
                Arrays.asList(0.0, -1.0)
        );
        Ubicacion antartida = new Ubicacion("Antartida", 10, TipoUbicacion.SANTUARIO);
        antartida = servUbi.crear(antartida, coordsPoligonoGrande, descripcion1);

        //Espiritu
        GeoJsonPoint puntoEspiritu = new GeoJsonPoint(0.0, 0.0);
        sid = servEsp.crear(sid, puntoEspiritu, descripcion1);

        //Medium
        GeoJsonPoint puntoMedium = new GeoJsonPoint(1.9, -2.9);
        Medium mediumDesterrado = new Medium("Yeti", 130, 60, antartida);
        mediumDesterrado = servMed.crear(mediumDesterrado, puntoMedium, descripcion1);
        Medium mediumDesterradoRec = servMed.recuperar(mediumDesterrado.getId()).get();

        //Verify
        assertThrows(EspirituMuyLejanoException.class, () -> {
            mediumService.invocar(mediumDesterradoRec.getId(), sid.getId());
        });
    }

    @Test
    void mediumSeMueveAPUntoLimitrofrEntreUbicaciones(){
        // Setup
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        // Exercise
        servMed.mover(meliodas.getId(),0.19, 0.19 );
        Medium mRecuperado = servMed.recuperar(meliodas.getId()).get();

        // Verify
        assertEquals(mRecuperado.getUbicacion().getId(), buenosAires.getId());
    }

    @Test
    void recuperarMediumElastic(){
        // Setup
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);

        // Exercise
        MediumElastic mRecuperado = servMed.recuperarElastic(meliodas.getId()).get();

        // Verify
        assertEquals(meliodas.getId(), mRecuperado.getId());
    }

    @Test
    void mediumActualizaDescripcion(){
        // Setup
        meliodas = servMed.crear(meliodas, puntoUshuaia, descripcion1);
        MediumElastic mediumElastic = servMed.recuperarElastic(meliodas.getId()).get();

        // Exercise
        String nuevaDescripcion = "Nueva descripcion de Meliodas";
        mediumElastic.setDescripcion(nuevaDescripcion);
        servMed.actualizarDescripcion(mediumElastic);

        MediumElastic mRecuperado = servMed.recuperarElastic(meliodas.getId()).get();

        // Verify
        assertEquals(nuevaDescripcion, mRecuperado.getDescripcion());
    }

    @Test
    void testBuscarMediumSemanticamente1() {
        // SetUp
        Medium asta;

        asta = servMed.crearConIndexar(new Medium("Asta", 110, 80, ushuaia), puntoUshuaia, "Lucha con una espada que repele la magia, es fuerte fisicamente y tiene ventaja cuerpo a cuerpo.");
        meliodas = servMed.crearConIndexar(new Medium("Meliodas", 130, 60, ushuaia), puntoUshuaia, "Puede controlar el agua y la sangre pero tiene desventaja contra el fuego, es sanador.");
        merlin = servMed.crearConIndexar(new Medium("Merlin", 90, 35, buenosAires), puntoBuenosAires, "Tiene poderes de lectura de mente siendo su ventaja el combate a distancia con magia, es apoyo.");

        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // Exercise
        List<MediumElastic> resultados = servMed.buscarSemanticamente("usa una espada");

        // Verify
        assertFalse(resultados.isEmpty());
        assertEquals("Asta", resultados.get(0).getNombre());
    }

    @Test
    void testBuscarMediumSemanticamente2() {
        // SetUp
        Medium asta;

        asta = servMed.crearConIndexar(new Medium("Asta", 110, 80, ushuaia), puntoUshuaia, "Lucha con una espada que repele la magia, es fuerte fisicamente y tiene ventaja cuerpo a cuerpo.");
        meliodas = servMed.crearConIndexar(new Medium("Meliodas", 130, 60, ushuaia), puntoUshuaia, "Puede controlar el agua y la sangre pero tiene desventaja contra el fuego, es sanador.");
        merlin = servMed.crearConIndexar(new Medium("Merlin", 90, 35, buenosAires), puntoBuenosAires, "Tiene poderes de lectura de mente siendo su ventaja el combate a distancia con magia, es apoyo.");

        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // Exercise
        List<MediumElastic> resultados = servMed.buscarSemanticamente("es psiquico");

        // Verify
        assertFalse(resultados.isEmpty());
        assertEquals("Merlin", resultados.get(0).getNombre());
    }

    @Test
    void mediumInvestigaUbicaciones(){

        String descripcion2 = "Descripcion de ubicacion";

        Ubicacion mendoza = new Ubicacion("Mendoza", 10, TipoUbicacion.CEMENTERIO);


        servUbi.crear(mendoza, area3, descripcion2);

        List<UbicacionElastic> ubicacionesQueCoinciden = mediumService.investigarUbicaciones("Lorem");

        //Verify
        assertEquals(ubicacionesQueCoinciden.getFirst().getDescripcion(), descripcion1);
        assertEquals(ubicacionesQueCoinciden.get(1).getDescripcion(), descripcion1);
        assertEquals(2, ubicacionesQueCoinciden.size());
    }

    @Test
    void mediumInvestigaUbicacionesEnDetalle(){

        String descripcion2 = "Descripcion de ubicacion";

        Ubicacion mendoza = new Ubicacion("Mendoza", 10, TipoUbicacion.CEMENTERIO);


        servUbi.crear(mendoza, area3, descripcion2);

        List<UbicacionElastic> ubicacionesQueCoinciden = mediumService.investigarUbicacionesMejorado("Loren ipsum");

        //Verify
        assertEquals(ubicacionesQueCoinciden.getFirst().getDescripcion(), descripcion1);
        assertEquals(ubicacionesQueCoinciden.get(1).getDescripcion(), descripcion1);
        assertEquals(2, ubicacionesQueCoinciden.size());
    }

    @Test
    void mediumInvestigaUbicacionesEnDetalleConMuchosErrores(){

        String descripcion2 = "Descripcion de ubicacion";

        Ubicacion mendoza = new Ubicacion("Mendoza", 10, TipoUbicacion.CEMENTERIO);


        servUbi.crear(mendoza, area3, descripcion2);

        List<UbicacionElastic> ubicacionesQueCoinciden = mediumService.investigarUbicacionesMejoradoFuzzines("Lome isun", "2");

        //Verify
        assertEquals(ubicacionesQueCoinciden.getFirst().getDescripcion(), descripcion1);
        assertEquals(ubicacionesQueCoinciden.get(1).getDescripcion(), descripcion1);
        assertEquals(2, ubicacionesQueCoinciden.size()); //
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.cleanDatabase();
        neo4jDatabaseCleaner.cleanDatabase();
        mongoDBDatabaseCleaner.cleanDatabase();
        elasticSearchCleaner.cleanAllDocuments();
    }
}