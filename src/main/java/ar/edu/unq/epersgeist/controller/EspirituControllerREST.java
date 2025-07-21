package ar.edu.unq.epersgeist.controller;


import ar.edu.unq.epersgeist.controller.dto.EspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.MediumDTO;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.excepcion.*;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/espiritu")
public class EspirituControllerREST {

    private final EspirituService espirituService;
    private final UbicacionService ubicacionService;
    private final MediumService mediumService;

    public EspirituControllerREST(EspirituService espirituService, UbicacionService ubicacionService, MediumService mediumService) {
        this.espirituService = espirituService;
        this.ubicacionService = ubicacionService;
        this.mediumService = mediumService;
    }

    @GetMapping
    public ResponseEntity<List<EspirituDTO>> getAllEspiritus() {
        List<EspirituDTO> espirtusDTO = espirituService.recuperarTodos().stream()
                .map(e -> EspirituDTO.desdeModelo(e , espirituService.recuperarDescripcion(e.getId())))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(espirtusDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEspirituById(@PathVariable Long id) {
        Optional<Espiritu> espirituRec = espirituService.recuperar(id);
        if(espirituRec.isPresent()) {
            Espiritu espiritu = espirituRec.get();
            return ResponseEntity.ok(EspirituDTO.desdeModelo(espiritu, espirituService.recuperarDescripcion(espiritu.getId())));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Espiritu no encontrado.");
    }

    @PostMapping("/crear/{latitud}/{longitud}")
    public ResponseEntity<?> createEspiritu(@RequestBody EspirituDTO espiritu,
                                            @PathVariable Double latitud,
                                            @PathVariable Double longitud) {
        try {
            Ubicacion ubicacion = ubicacionService.recuperar(espiritu.idUbicacion()).get();
            TipoEspiritu tipoEsp = this.convertirTipo(espiritu.tipo());
            GeoJsonPoint punto = new GeoJsonPoint(longitud, latitud);
            espirituService.crear(espiritu.aModelo(ubicacion, tipoEsp), punto, espiritu.descripcion());
            return ResponseEntity.status(HttpStatus.CREATED).body("Espíritu creado exitosamente.");
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ubicación no encontrada.");
        } catch (NivelDeConexionFueraDeRango exInv) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new NivelDeConexionFueraDeRango().getMessage());
        } catch (PuntoNoPerteneceException exInv) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new PuntoNoPerteneceException().getMessage());
        }
    }

    @PutMapping("/{espirituId}")
    public ResponseEntity<?>  updateEspiritu(@PathVariable Long espirituId,
                                             @RequestBody EspirituDTO espiritu) {
            try{
                Ubicacion ubicacionRecuperada =
                        ubicacionService.recuperar(espiritu.idUbicacion()).get();
                Espiritu espRecuperado = espirituService.recuperar(espirituId).get();
                TipoEspiritu tipoEsp = this.convertirTipo(espiritu.tipo());
                Espiritu espirituActualizado =
                        espiritu.actualizarModelo(espRecuperado, ubicacionRecuperada, tipoEsp);
                espirituService.actualizar(espirituActualizado);
                return ResponseEntity.ok(EspirituDTO.desdeModelo(espirituActualizado, espirituService.recuperarDescripcion(espirituActualizado.getId())));
            }
            catch (NoSuchElementException exElem) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Recurso no encontrado: ids del Espíritu o Ubicación incorrectos" );
            }
            catch (NivelDeConexionFueraDeRango exInv){
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(new NivelDeConexionFueraDeRango().getMessage());
            }

    }

    @DeleteMapping("/{espirituId}")
    public ResponseEntity<String> eliminar(@PathVariable Long espirituId) {
        try {
            espirituService.eliminar(espirituId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("El Espíritu fue eliminado exitosamente");
        } catch (NoSuchElementException exElem ) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: id de espiritu incorrecto");
        } catch (EntidadAELiminarEstaRelacionada exInv){
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new EntidadAELiminarEstaRelacionada().getMessage());
        }
    }


    @PutMapping("/{espirituId}/conectar/{mediumId}")
    public ResponseEntity<?> conectar(@PathVariable Long espirituId, @PathVariable Long mediumId) {
        try {
            Medium mediumConectado = espirituService.conectar(espirituId, mediumId);
            return ResponseEntity.ok(MediumDTO.desdeModelo(mediumConectado, mediumService.recuperarDescripcion(mediumId)));
        } catch (NoSuchElementException exElem) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: " + exElem.getMessage());
        } catch (NoSePuedeRealizarLaConexionExeption exInv) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new NoSePuedeRealizarLaConexionExeption().getMessage());
        } catch (EspirituDominadoException exInv) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new EspirituDominadoException().getMessage());
        }
    }

    @GetMapping("/direccion/{dir}/pagina/{pag}/cantidadPorPag/{cant}")
    public ResponseEntity<?> espiritusDemoniacos(@PathVariable String dir,
                                                 @PathVariable int pag,
                                                 @PathVariable int cant){
            try{
                List<Espiritu> espiritusDemoniacos = espirituService
                        .espiritusDemoniacos(Direccion.valueOf(dir), pag, cant);
                List<EspirituDTO> espiritusDemoniacosDTO = espiritusDemoniacos.stream()
                        .map(e -> EspirituDTO.desdeModelo(e , espirituService.recuperarDescripcion(e.getId())))
                        .toList();
                return ResponseEntity.ok(espiritusDemoniacosDTO);
            } catch (ValorNegativoDePaginacion exInv){
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(new ValorNegativoDePaginacion().getMessage());
            }
    }

    private TipoEspiritu convertirTipo(String tipo) {
        return switch (tipo) {
            case "ANGEL" -> TipoEspiritu.ANGEL;
            case "DEMONIO" -> TipoEspiritu.DEMONIO;
            default -> throw new IllegalStateException("Unexpected value: " + tipo);
        };
    }

    @PutMapping("/{espirituDominateId}/dominar/{espirituADominarId}")
    public ResponseEntity<?> dominar(@PathVariable Long espirituDominateId,
                                     @PathVariable Long espirituADominarId){
        try {
            espirituService.dominar(espirituDominateId, espirituADominarId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("El Espíritu fue dominado exitosamente");
        } catch (NoSuchElementException exElem ){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: " + exElem.getMessage());
        } catch (CondicionesDeDominioInsuficientes exInv) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new CondicionesDeDominioInsuficientes().getMessage());
        }
    }

    @GetMapping("/busqueda-semantic")
    public List<EspirituElastic> buscarMediumSemantico(@RequestParam String texto) {
        return espirituService.buscarSemanticamente(texto);
    }

    @PutMapping("/{espirituId}/teletransportar/{ubicacionId}")
    public ResponseEntity<?> teletransportar(@PathVariable Long espirituId, @PathVariable Long ubicacionId) {
        try {
            espirituService.teletransportar(ubicacionId, espirituId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("El Espíritu fue teletransportado exitosamente!");
        } catch (NoSuchElementException exElem) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: " + exElem.getMessage());
        } catch (NoSePuedeRealizarLaTeletransportacionException exInv) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new NoSePuedeRealizarLaTeletransportacionException().getMessage());
        }
    }
}
