package ar.edu.unq.epersgeist.dao;

import ar.edu.unq.epersgeist.dao.Utils.DatabaseCleaner;
import ar.edu.unq.epersgeist.dao.Utils.ElasticSearchCleaner;
import ar.edu.unq.epersgeist.dao.Utils.MongoDBDatabaseCleaner;
import ar.edu.unq.epersgeist.dao.Utils.Neo4jDatabaseCleaner;
import ar.edu.unq.epersgeist.modelo.*;
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

@SpringBootTest
public class BusquedaSemanticaTest {

    @Autowired
    private EspirituServiceImpl eService;

    @Autowired
    private MediumServiceImpl mService;

    @Autowired
    private UbicacionServiceImpl uService;

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

    private Ubicacion argentina;
    private Ubicacion noruega;

    private GeoJsonPoint puntoArgentina;
    private GeoJsonPoint puntoNoruega;

    @BeforeEach
    void setUp() {
        // databaseCleaner.cleanDatabase(); neo4jDatabaseCleaner.cleanDatabase(); mongoDBDatabaseCleaner.cleanDatabase(); elasticSearchCleaner.cleanAllDocuments();
    }

    // @Test
    // void testBorrarTodo() {}

    @Test
    void testCrearTodo() {

        List<List<Double>> area0 = Arrays.asList(
                Arrays.asList(0.00, 0.00),
                Arrays.asList(0.00, 0.19),
                Arrays.asList(0.19, 0.19),
                Arrays.asList(0.19, 0.00),
                Arrays.asList(0.00, 0.00)
        );

        List<List<Double>> area1 = Arrays.asList(
                Arrays.asList(0.20, 0.00),
                Arrays.asList(0.20, 0.19),
                Arrays.asList(0.39, 0.19),
                Arrays.asList(0.39, 0.00),
                Arrays.asList(0.20, 0.00)
        );

        argentina = uService.crearConIndexar(new Ubicacion("Argentina", 10, TipoUbicacion.CEMENTERIO), area0, "Zona urbana vibrante con fuerte contraste energético. Espíritus se manifiestan en plazas llenas de arte, túneles históricos y teatros antiguos. Lugar ideal para rituales de conexión cultural y memoria ancestral.");
        noruega = uService.crearConIndexar(new Ubicacion("Noruega", 10, TipoUbicacion.SANTUARIO), area1,"Región montañosa nevada y frias, envuelta por bosques milenarios. Sus valles silenciosos son escenario de encuentros con entidades de luz y prácticas de meditación profunda. Excelente para rituales de alineación espiritual y limpieza áurica.");

        puntoArgentina = new GeoJsonPoint(new Point(0.11, 0.11));
        puntoNoruega = new GeoJsonPoint(new Point(0.21, 0.17));

        List<List<Double>> area2 = Arrays.asList(
                Arrays.asList(0.40, 0.00),
                Arrays.asList(0.40, 0.19),
                Arrays.asList(0.59, 0.19),
                Arrays.asList(0.59, 0.00),
                Arrays.asList(0.40, 0.00)
        );

        List<List<Double>> area3 = Arrays.asList(
                Arrays.asList(0.60, 0.00),
                Arrays.asList(0.60, 0.19),
                Arrays.asList(0.79, 0.19),
                Arrays.asList(0.79, 0.00),
                Arrays.asList(0.60, 0.00)
        );

        List<List<Double>> area4 = Arrays.asList(
                Arrays.asList(0.80, 0.00),
                Arrays.asList(0.80, 0.19),
                Arrays.asList(0.99, 0.19),
                Arrays.asList(0.99, 0.00),
                Arrays.asList(0.80, 0.00)
        );

        List<List<Double>> area5 = Arrays.asList(
                Arrays.asList(0.00, 0.20),
                Arrays.asList(0.00, 0.39),
                Arrays.asList(0.19, 0.39),
                Arrays.asList(0.19, 0.20),
                Arrays.asList(0.00, 0.20)
        );

        List<List<Double>> area6 = Arrays.asList(
                Arrays.asList(0.20, 0.20),
                Arrays.asList(0.20, 0.39),
                Arrays.asList(0.39, 0.39),
                Arrays.asList(0.39, 0.20),
                Arrays.asList(0.20, 0.20)
        );

        List<List<Double>> area7 = Arrays.asList(
                Arrays.asList(0.40, 0.20),
                Arrays.asList(0.40, 0.39),
                Arrays.asList(0.59, 0.39),
                Arrays.asList(0.59, 0.20),
                Arrays.asList(0.40, 0.20)
        );

        List<List<Double>> area8 = Arrays.asList(
                Arrays.asList(0.60, 0.20),
                Arrays.asList(0.60, 0.39),
                Arrays.asList(0.79, 0.39),
                Arrays.asList(0.79, 0.20),
                Arrays.asList(0.60, 0.20)
        );

        List<List<Double>> area9 = Arrays.asList(
                Arrays.asList(0.80, 0.20),
                Arrays.asList(0.80, 0.39),
                Arrays.asList(0.99, 0.39),
                Arrays.asList(0.99, 0.20),
                Arrays.asList(0.80, 0.20)
        );

        List<List<Double>> area10 = Arrays.asList(
                Arrays.asList(0.00, 0.40),
                Arrays.asList(0.00, 0.59),
                Arrays.asList(0.19, 0.59),
                Arrays.asList(0.19, 0.40),
                Arrays.asList(0.00, 0.40)
        );

        List<List<Double>> area11 = Arrays.asList(
                Arrays.asList(0.20, 0.40),
                Arrays.asList(0.20, 0.59),
                Arrays.asList(0.39, 0.59),
                Arrays.asList(0.39, 0.40),
                Arrays.asList(0.20, 0.40)
        );

        List<List<Double>> area12 = Arrays.asList(
                Arrays.asList(0.40, 0.40),
                Arrays.asList(0.40, 0.59),
                Arrays.asList(0.59, 0.59),
                Arrays.asList(0.59, 0.40),
                Arrays.asList(0.40, 0.40)
        );

        List<List<Double>> area13 = Arrays.asList(
                Arrays.asList(0.60, 0.40),
                Arrays.asList(0.60, 0.59),
                Arrays.asList(0.79, 0.59),
                Arrays.asList(0.79, 0.40),
                Arrays.asList(0.60, 0.40)
        );

        List<List<Double>> area14 = Arrays.asList(
                Arrays.asList(0.80, 0.40),
                Arrays.asList(0.80, 0.59),
                Arrays.asList(0.99, 0.59),
                Arrays.asList(0.99, 0.40),
                Arrays.asList(0.80, 0.40)
        );

        List<List<Double>> area15 = Arrays.asList(
                Arrays.asList(0.00, 0.60),
                Arrays.asList(0.00, 0.79),
                Arrays.asList(0.19, 0.79),
                Arrays.asList(0.19, 0.60),
                Arrays.asList(0.00, 0.60)
        );

        List<List<Double>> area16 = Arrays.asList(
                Arrays.asList(0.20, 0.60),
                Arrays.asList(0.20, 0.79),
                Arrays.asList(0.39, 0.79),
                Arrays.asList(0.39, 0.60),
                Arrays.asList(0.20, 0.60)
        );

        List<List<Double>> area17 = Arrays.asList(
                Arrays.asList(0.40, 0.60),
                Arrays.asList(0.40, 0.79),
                Arrays.asList(0.59, 0.79),
                Arrays.asList(0.59, 0.60),
                Arrays.asList(0.40, 0.60)
        );

        List<List<Double>> area18 = Arrays.asList(
                Arrays.asList(0.60, 0.60),
                Arrays.asList(0.60, 0.79),
                Arrays.asList(0.79, 0.79),
                Arrays.asList(0.79, 0.60),
                Arrays.asList(0.60, 0.60)
        );

        List<List<Double>> area19 = Arrays.asList(
                Arrays.asList(0.80, 0.60),
                Arrays.asList(0.80, 0.79),
                Arrays.asList(0.99, 0.79),
                Arrays.asList(0.99, 0.60),
                Arrays.asList(0.80, 0.60)
        );

        List<List<Double>> area20 = Arrays.asList(
                Arrays.asList(0.00, 0.80),
                Arrays.asList(0.00, 0.99),
                Arrays.asList(0.19, 0.99),
                Arrays.asList(0.19, 0.80),
                Arrays.asList(0.00, 0.80)
        );

        List<List<Double>> area21 = Arrays.asList(
                Arrays.asList(0.20, 0.80),
                Arrays.asList(0.20, 0.99),
                Arrays.asList(0.39, 0.99),
                Arrays.asList(0.39, 0.80),
                Arrays.asList(0.20, 0.80)
        );

        List<List<Double>> area22 = Arrays.asList(
                Arrays.asList(0.40, 0.80),
                Arrays.asList(0.40, 0.99),
                Arrays.asList(0.59, 0.99),
                Arrays.asList(0.59, 0.80),
                Arrays.asList(0.40, 0.80)
        );

        List<List<Double>> area23 = Arrays.asList(
                Arrays.asList(0.60, 0.80),
                Arrays.asList(0.60, 0.99),
                Arrays.asList(0.79, 0.99),
                Arrays.asList(0.79, 0.80),
                Arrays.asList(0.60, 0.80)
        );

        List<List<Double>> area24 = Arrays.asList(
                Arrays.asList(0.80, 0.80),
                Arrays.asList(0.80, 0.99),
                Arrays.asList(0.99, 0.99),
                Arrays.asList(0.99, 0.80),
                Arrays.asList(0.80, 0.80)
        );

        uService.crearConIndexar(new Ubicacion("Japón", 20, TipoUbicacion.CEMENTERIO), area2, "Bosque templado entre montañas nevadas. Aquí se realizan rituales de purificación con máscaras y faroles encendidos.");
        uService.crearConIndexar(new Ubicacion("Egipto", 25, TipoUbicacion.CEMENTERIO), area3, "Ruinas desérticas bajo el sol abrasador, un calor abrumador. Tumbas alineadas con las estrellas. Ideal para invocaciones solares y sellos ancestrales.");
        uService.crearConIndexar(new Ubicacion("México", 10, TipoUbicacion.SANTUARIO), area4, "Colorido y caluroso poblado con altares callejeros. Se celebran festivales espirituales con danzas, velas y música ancestral.");
        uService.crearConIndexar(new Ubicacion("China", 28, TipoUbicacion.CEMENTERIO), area5, "Región montañosa y brumosa. Las almas descansan entre pagodas vacías. Se hacen ofrendas silenciosas al amanecer.");
        uService.crearConIndexar(new Ubicacion("Grecia", 18, TipoUbicacion.SANTUARIO), area6, "Colinas secas con ruinas abiertas al cielo. Lugar sagrado para canalizar sueños proféticos y contacto con oráculos.");
        uService.crearConIndexar(new Ubicacion("Islandia", 5, TipoUbicacion.CEMENTERIO), area7, "Terreno gélido, helado, sin presencia humana. Ideal para rituales de transición, meditación profunda y liberación del alma.");
        uService.crearConIndexar(new Ubicacion("Brasil", 27, TipoUbicacion.SANTUARIO), area8, "Playas vibrantes y calurosas con aguas cristalinss. Aquí se hacen invocaciones naturales, fiestas con danzas lunares y ritos con animales guía.");
        uService.crearConIndexar(new Ubicacion("Francia", 22, TipoUbicacion.CEMENTERIO), area9, "Bosque encantado con caminos olvidados. Espíritus caminan entre la niebla. Excelente para comunicarse con muertos antiguos.");
        uService.crearConIndexar(new Ubicacion("Italia", 16, TipoUbicacion.CEMENTERIO), area10, "Pasajes subterráneos bajo ruinas medievales. Energía intensa usada para cerrar portales y cortar lazos espirituales.");
        uService.crearConIndexar(new Ubicacion("Canadá", 30, TipoUbicacion.SANTUARIO), area11, "Bosques boreales bajo auroras. Zona remota usada para sesiones de aislamiento, descanso del alma y conexión con guías.");
        uService.crearConIndexar(new Ubicacion("Alemania", 8, TipoUbicacion.CEMENTERIO), area12, "Bosque denso donde la luna filtra los secretos. Lugar de invocaciones rúnicas y protección de espíritus guardianes.");
        uService.crearConIndexar(new Ubicacion("Australia", 11, TipoUbicacion.SANTUARIO), area13, "Tierra seca y sagrada. Aquí se realizan cantos ancestrales y meditaciones bajo estrellas del hemisferio sur.");
        uService.crearConIndexar(new Ubicacion("Suecia", 23, TipoUbicacion.CEMENTERIO), area14, "Montañas cubiertas de niebla espesa. Refugio de linajes antiguos. Ideal para contactar con la sangre espiritual.");
        uService.crearConIndexar(new Ubicacion("Groenlandia", 19, TipoUbicacion.SANTUARIO), area15, "Cuevas muy frias y oscuras. Se practican regresiones, introspecciones y recuperación de vidas pasadas.");
        uService.crearConIndexar(new Ubicacion("Marruecos", 9, TipoUbicacion.CEMENTERIO), area16, "Desierto extenso y silencioso. Se escucha el eco de los ancestros durante las tormentas de arena.");
        uService.crearConIndexar(new Ubicacion("Corea", 3, TipoUbicacion.SANTUARIO), area17, "Templo escondido entre pinos. Aquí se hacen rituales de conexión con guías espirituales y limpieza de aura.");
        uService.crearConIndexar(new Ubicacion("Chile", 4, TipoUbicacion.SANTUARIO), area18, "Zona rural con capillas en ruinas. Se realizan rituales de despedida, visión y restauración energética.");
        uService.crearConIndexar(new Ubicacion("Rusia", 13, TipoUbicacion.CEMENTERIO), area19, "Paisaje nevado sin vida urbana. Terreno poderoso para meditaciones de silencio absoluto, liberación de cargas y descanso.");
        uService.crearConIndexar(new Ubicacion("Colombia", 14, TipoUbicacion.CEMENTERIO), area20, "Cementerio selvático. Espíritus se manifiestan con lluvias. Se hacen pactos de protección con guardianes del agua.");
        uService.crearConIndexar(new Ubicacion("Estados Unidos", 2, TipoUbicacion.SANTUARIO), area21, "Ciudad topada de gente. Se celebran sesiones de contacto espiritual y sanación por palabra.");
        uService.crearConIndexar(new Ubicacion("Tailandia", 1, TipoUbicacion.CEMENTERIO), area22, "Zona tropical con aromas florales. Ideal para encuentros con entidades suaves y renovación de energía vital.");
        uService.crearConIndexar(new Ubicacion("Suiza", 20, TipoUbicacion.CEMENTERIO), area23, "Praderas abiertas con vistas claras al cielo. Lugar pacífico para rituales de perdón y descanso eterno.");
        uService.crearConIndexar(new Ubicacion("España", 29, TipoUbicacion.SANTUARIO), area24, "Monte despejado y cálido. Se hacen ceremonias de apertura espiritual y conexiones grupales al atardecer.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 50, "Nicolas", argentina), puntoArgentina, "Demonio de la distorsión. Controla espejismos y crea ilusiones para confundir la mente de sus enemigos. Débil ante sonidos armónicos.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 70, "Melisa", argentina), puntoArgentina, "Invocadora de la desesperanza. Extrae energía de los corazones rotos. Fuerte en entornos abandonados o cerrados.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 35, "Sebastian", argentina), puntoArgentina, "Espíritu errático que lanza cuchillas etéreas. Rápido, pero inestable emocionalmente.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 65, "Tomas", argentina), puntoArgentina, "Vibra incendiaria. Su cuerpo emana fuego negro. Vulnerable al agua bendita.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 33, "Pablo", argentina), puntoArgentina, "Demonio del eco. Repite pensamientos oscuros. Se esconde en grietas y paredes.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 80, "Walter", argentina), puntoArgentina, "Entidad de fuerza bruta. Desgarra portales con garras ígneas.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 55, "Lorenzo", argentina), puntoArgentina, "Maestro del vacío. Disuelve recuerdos ajenos con una mirada. Necesita contacto visual.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 44, "Ivan", argentina), puntoArgentina, "Portador del fuego ancestral. Su cuerpo arde con llamas azules que purifican y destruyen por igual.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 38, "Fabian", argentina), puntoArgentina, "Habita en objetos antiguos. Susurra pensamientos oscuros para manipular la mente de quienes lo rodean.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 49, "Valentin", argentina), puntoArgentina, "Torcedor de sueños. Manipula realidades durante el sueño. Fuerte en horas nocturnas.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 62, "Tobias", argentina), puntoArgentina, "Espectro de la penumbra. Viaja entre sombras. Puede materializar cuchillas hechas de oscuridad.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 29, "Santiago", argentina), puntoArgentina, "Demonio territorial. Protege cementerios y ruinas. Usa una lanza espiritual corrupta.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.DEMONIO, 47, "Alejandra", argentina), puntoArgentina, "Controla el frío espiritual. Su aliento congela a los vivos. Vulnerable a fuentes de luz pura.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 19, "Malena", noruega), puntoNoruega, "Ángel de la calma. Su voz disuelve conflictos. Usa una lira de energía cristalina.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 40, "Ornella", noruega), puntoNoruega, "Espectro sigiloso que se funde con las sombras. Camina entre dimensiones ocultas y sólo se manifiesta cuando alguien siente culpa.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 14, "Marcos", noruega), puntoNoruega, "Guía de almas perdidas. Porta una linterna de éter que muestra el camino a otros espíritus.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 17, "Elian", noruega), puntoNoruega, "Ángel del descanso. Su manto sella pesadillas. Habilidad: inducir sueños reparadores.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 21, "Guido", noruega), puntoNoruega, "Sanador silente. Con su toque, limpia el aura. No puede hablar, pero su energía comunica paz.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 29, "Joaquin", noruega), puntoNoruega, "Portador de espadas gemelas de luz. Defiende portales sagrados. Fortaleza: combate directo.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 31, "Julian", noruega), puntoNoruega, "Transformador de la culpa. Aparece en momentos de arrepentimiento. Fragancia celestial al manifestarse.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 23, "Gabriela", noruega), puntoNoruega, "Guardián de los inocentes. Su escudo sagrado bloquea ataques espirituales. Débil ante mentiras.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 27, "Fernando", noruega), puntoNoruega, "Mensajero del tiempo. Usa un reloj solar para anticiparse a eventos. Muy sabio, poco ágil.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 35, "Aron", noruega), puntoNoruega, "Justiciero de energías corruptas. Usa cadenas de luz para contener demonios. Persistente.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 36, "Martin", noruega), puntoNoruega, "Meditador eterno. Emite ondas curativas desde su corazón. Su energía sana cuerpos y mentes.");
        eService.crearConIndexar(new Espiritu(TipoEspiritu.ANGEL, 39, "Mauro", noruega), puntoNoruega, "Centinela del ritual. Aparece solo en ceremonias sagradas. Sostiene una flama que purifica el ambiente.");
        mService.crearConIndexar(new Medium("Nehuen", 110, 80, noruega), puntoNoruega, "Maestro del equilibrio espiritual. Su aura es dorada y combate con abanicos de luz que desvían energías oscuras.");
        mService.crearConIndexar(new Medium("Josefina", 120, 85, argentina), puntoArgentina, "Controla la energía del tiempo con un reloj astral. Puede acelerar o ralentizar emociones a su favor.");
        mService.crearConIndexar(new Medium("Lautaro", 100, 70, noruega), puntoNoruega, "Especialista en invocaciones lunares. Sus cánticos generan escudos emocionales en quienes lo rodean.");
        mService.crearConIndexar(new Medium("Luciana", 115, 90, argentina), puntoArgentina, "Guerrera espiritual. Usa una lanza luminosa que disuelve vínculos negativos y crea campos de protección.");
        mService.crearConIndexar(new Medium("Matias", 112, 88, noruega), puntoNoruega, "Vidente del viento. Detecta presencias a través de corrientes de aire y porta un bastón que vibra ante el peligro.");
        mService.crearConIndexar(new Medium("Enzo", 130, 100, argentina), puntoArgentina, "Canalizador de fuego sagrado. Sus ojos emiten calor durante los rituales.");
        mService.crearConIndexar(new Medium("Lucia", 105, 65, noruega), puntoNoruega, "Manos que sanan. Puede cerrar heridas energéticas tocando el alma de quien sufre.");
        mService.crearConIndexar(new Medium("Axel", 125, 90, argentina), puntoArgentina, "Chamán del rayo. Invoca tormentas controladas que purifican zonas cargadas de odio.");
        mService.crearConIndexar(new Medium("Chiara", 108, 75, noruega), puntoNoruega, "Domina la resonancia. En los ritos usa campanas magicas que alteran las intenciones de espíritus malignos.");
        mService.crearConIndexar(new Medium("Candela", 120, 95, argentina), puntoArgentina, "Conjuradora rúnica. Usa espadas talladas con símbolos de poder al combatir. Gran fuerza física, débil ante magia mental.");
        mService.crearConIndexar(new Medium("Joel", 110, 85, noruega), puntoNoruega, "Especialista en descanso espiritual. Usa mantos de seda para inducir trances restauradores.");
        mService.crearConIndexar(new Medium("Carolina", 118, 89, argentina), puntoArgentina, "Empática poderosa. Absorbe emociones ajenas y las transforma en energía curativa.");
        mService.crearConIndexar(new Medium("Cristian", 102, 72, noruega), puntoNoruega, "Guardia de portales. Usa un espejo fractal para detectar puertas dimensionales. Preciso, pero frágil.");
        mService.crearConIndexar(new Medium("Damian", 124, 98, argentina), puntoArgentina, "Paladín del aura. Manipula campos energéticos con anillos de obsidiana. Poderoso físicamente.");
        mService.crearConIndexar(new Medium("Emilia", 116, 90, noruega), puntoNoruega, "Hechicera de la armonía. Realiza invocaciones florales para calmar almas errantes.");
        mService.crearConIndexar(new Medium("Lourdes", 129, 110, argentina), puntoArgentina, "Dominadora del éter. Puede levitar objetos y alterar la percepción del tiempo con su voz.");
        mService.crearConIndexar(new Medium("Francisco", 111, 79, noruega), puntoNoruega, "Bendecido con visión astral. Usa cristales templados y túnicas de visión para guiar a otros.");
        mService.crearConIndexar(new Medium("Franco", 123, 94, argentina), puntoArgentina, "Invocador de bestias espirituales. Su voz celestial llama a entidades protectoras en combate.");
        mService.crearConIndexar(new Medium("Hernan", 114, 88, noruega), puntoNoruega, "Mente brillante, pero físicamente débil. Tiene una piedra ancestral que amplifica su intuición y predicción.");
        mService.crearConIndexar(new Medium("Ian", 117, 92, argentina), puntoArgentina, "Mentalista puro. Manipula pensamientos débiles, pero no puede controlar los de espíritu fuerte.");
        mService.crearConIndexar(new Medium("Lucas", 109, 77, noruega), puntoNoruega, "Líder ritualista. Conduce danzas mágicas con máscaras talladas, ideal para sesiones grupales.");
        mService.crearConIndexar(new Medium("Lucrecia", 128, 99, argentina), puntoArgentina, "Portadora de la piedra solar. Su energía revitaliza el entorno. Inmune a ataques mágicos básicos.");
        mService.crearConIndexar(new Medium("Abril", 106, 71, noruega), puntoNoruega, "Guardiana de secretos. Su voz puede abrir cofres espirituales que contienen memorias olvidadas.");
        mService.crearConIndexar(new Medium("Mateo", 132, 108, argentina), puntoArgentina, "Oráculo de eclipses. Sus poderes se activan solo en noches oscuras, cuando los cielos se alinean.");
        mService.crearConIndexar(new Medium("Maxi", 107, 76, noruega), puntoNoruega, "Canalizador de sanación. Con sus manos transmite energía luminosa que disuelve traumas antiguos.");

    }

    // @AfterEach void deleteDatabases() {databaseCleaner.cleanDatabase();neo4jDatabaseCleaner.cleanDatabase();mongoDBDatabaseCleaner.cleanDatabase();elasticSearchCleaner.cleanAllDocuments();}
}