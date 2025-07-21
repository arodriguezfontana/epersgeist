package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.EspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.MediumDTO;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.MediumElastic;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.UbicacionElastic;
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
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medium")
public class MediumControllerREST {

    private final MediumService mediumService;
    private final UbicacionService ubicacionService;
    private final EspirituService espirituService;

    public MediumControllerREST(MediumService mediumService, UbicacionService ubicacionService, EspirituService espirituService) {
        this.mediumService = mediumService;
        this.ubicacionService = ubicacionService;
        this.espirituService = espirituService;
    }

    @GetMapping
    public ResponseEntity<List<MediumDTO>> getAllMediumss() {
        List<MediumDTO> mediums = mediumService.recuperarTodos().stream()
                .map(m -> MediumDTO.desdeModelo(m , mediumService.recuperarDescripcion(m.getId())))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(mediums);
    }

    @GetMapping("/{mediumId}")
    public ResponseEntity<?> getMediumById(@PathVariable Long mediumId) {
        Optional<Medium> mediumRecuperado = mediumService.recuperar(mediumId);
        if(mediumRecuperado.isPresent()) {
            Medium medium = mediumRecuperado.get();
            return ResponseEntity.status(HttpStatus.OK).body(MediumDTO.desdeModelo(medium, mediumService.recuperarDescripcion(medium.getId())));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medium no encontrado.");
    }

    @PostMapping("/crear/{latitud}/{longitud}")
    public ResponseEntity<?> createMedium(@RequestBody MediumDTO medium,
                                          @PathVariable Double latitud,
                                          @PathVariable Double longitud) {
        Optional<Ubicacion> ubicacionOptional = ubicacionService.recuperar(medium.idUbicacion());
        if (medium.nombre().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Se necesita un nombre para crear un medium.");
        }
        try {
            Ubicacion ubicacionRecuperada = ubicacionOptional.get();
            Medium nuevoMedium = new Medium(medium.nombre(), medium.manaMax(), medium.mana(), ubicacionRecuperada);
            GeoJsonPoint punto = new GeoJsonPoint(longitud, latitud);
            mediumService.crear(nuevoMedium, punto, medium.descripcion());
            return ResponseEntity.status(HttpStatus.CREATED).body("Medium creado exitosamente.");
        }
        catch (NoSuchElementException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ubicación no encontrada.");
        }
        catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Datos inválidos: " + ex.getMessage());
        }
        catch (ManaFueraDeRangoException ex){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
        }catch (PuntoNoPerteneceException ex) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("El punto no pertenece a la ubicación especificada.");
        }
    }

    @PutMapping("/{mediumId}")
    public ResponseEntity<?> saveMedium(@PathVariable Long mediumId, @RequestBody MediumDTO medium){
        try{
            Optional<Ubicacion> ubicacionOptional = ubicacionService.recuperar(medium.idUbicacion());
            Medium mediumRecuperado = mediumService.recuperar(mediumId).get();
            Ubicacion ubicacionRecuperada = ubicacionOptional.get();
            Set<Espiritu> listaEspiritus = mediumRecuperado.getEspiritus();
            Medium mediumActualizado = medium
                    .actualizarModelo(mediumRecuperado, ubicacionRecuperada, listaEspiritus);
            mediumService.actualizar(mediumActualizado);
            return ResponseEntity.status(HttpStatus.OK).body(MediumDTO.desdeModelo(mediumActualizado, mediumService.recuperarDescripcion(mediumActualizado.getId())));}
        catch (NoSuchElementException exElem){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: ids del medium o ubicacion incorrectos" );
        }
    }


    @DeleteMapping("/{mediumId}")
    public ResponseEntity<String> eliminar(@PathVariable Long mediumId) {
        try {
            mediumService.eliminar(mediumId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Si el Medium ya existia, se elimino correctamente");
        }
        catch (NoSuchElementException exElem){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: id de medium incorrecto");
        }
        catch (EntidadAELiminarEstaRelacionada exInv){
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new EntidadAELiminarEstaRelacionada().getMessage());
        }
    }

    @GetMapping("/{mediumId}/espiritus")
    public ResponseEntity<?> espiritus(@PathVariable Long mediumId) {
        return ResponseEntity.status(HttpStatus.OK).body(mediumService.espiritus(mediumId).stream()
                .map(e -> EspirituDTO.desdeModelo(e , espirituService.recuperarDescripcion(e.getId())))
                .collect(Collectors.toList()));
    }

    @PutMapping("/{mediumId}/invocar/espiritu/{espirituId}")
    public ResponseEntity<?> invocar(@PathVariable Long mediumId, @PathVariable Long espirituId) {
        try{
        return ResponseEntity.status(HttpStatus.OK)
                .body(EspirituDTO.desdeModelo(mediumService.invocar(mediumId, espirituId), mediumService.recuperarDescripcion(espirituId)));}
        catch (NoSuchElementException exElem){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: " + exElem.getMessage());
        }
        catch (InvocacionFallidaException exInv) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new InvocacionFallidaException().getMessage());
        }
        catch (EspirituNoPuedeSerInvocado exEsp) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new EspirituNoPuedeSerInvocado("Este espiritu").getMessage());
        }
        catch (EspirituMuyLejanoException exEsp) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new EspirituMuyLejanoException().getMessage());
        }
    }

    @PutMapping("/{mediumId}/mover/ubicacion/{latitud}/{longitud}")
    public ResponseEntity<String> mover(@PathVariable Long mediumId,
                                        @PathVariable Double latitud,
                                        @PathVariable Double longitud) {
        try{
        mediumService.mover(mediumId, latitud, longitud);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Movimiento exitoso");}
        catch (NoSuchElementException exElem){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: " + exElem.getMessage());
        } catch (UbicacionLejanaException exElem){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Movimiento fallido: " + exElem.getMessage());
        }catch (UbicacionNoEncontradaException exElem) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(exElem.getMessage());
        }
    }

    @PutMapping("/{mediumExorcistaId}/exorcizar/{mediumAExorcizarId}")
    public ResponseEntity<?> exorizar(@PathVariable Long mediumExorcistaId,
                                      @PathVariable Long mediumAExorcizarId) {
       try {
           mediumService.exorcizar(mediumExorcistaId, mediumAExorcizarId);
           return ResponseEntity.status(HttpStatus.OK).body("Exorcismo realizado con exito.");
       } catch (NoSuchElementException ex) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
       } catch (ExorcistaSinAngelesException ex) {
           return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                   .body(ex.getMessage());
       }
    }

    @PutMapping("/descansar/{mediumId}")
    public ResponseEntity<?> descansar(@PathVariable Long mediumId) {
        try {
            mediumService.descansar(mediumId);
            return ResponseEntity.status(HttpStatus.OK).body("Descanso realizado con exito.");
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/busqueda-semantic")
    public List<MediumElastic> buscarMediumSemantico(@RequestParam String texto) {
        return mediumService.buscarSemanticamente(texto);
    }

    @GetMapping("/investigar/{descripcion}")
    public ResponseEntity<?> investigarUbicaciones(@PathVariable String descripcion) {
        List<UbicacionElastic> ubicaciones = mediumService.investigarUbicaciones(descripcion);
        return ResponseEntity.ok(ubicaciones);
    }

    @GetMapping("/investigar-mejorado/{descripcion}")
    public ResponseEntity<?> investigarUbicacionesMejorado(@PathVariable String descripcion) {
        List<UbicacionElastic> ubicaciones = mediumService.investigarUbicacionesMejorado(descripcion);
        return ResponseEntity.ok(ubicaciones);
    }

    @GetMapping("/investigar-fuzzines/{descripcion}/{fuzzines}")
    public ResponseEntity<?> investigarUbicacionesMejoradoFuzzines(@PathVariable String descripcion,@PathVariable String fuzzines) {
        List<UbicacionElastic> ubicaciones = mediumService.investigarUbicacionesMejoradoFuzzines(descripcion, fuzzines);
        return ResponseEntity.ok(ubicaciones);
    }
}