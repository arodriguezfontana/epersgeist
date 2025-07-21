package ar.edu.unq.epersgeist.controller;
import ar.edu.unq.epersgeist.controller.dto.ClosenessResult; //ver este import
import ar.edu.unq.epersgeist.controller.dto.EspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.MediumDTO;
import ar.edu.unq.epersgeist.modelo.EspirituElastic;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.UbicacionElastic;
import ar.edu.unq.epersgeist.modelo.UbicacionMongo;
import ar.edu.unq.epersgeist.modelo.excepcion.*;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ar.edu.unq.epersgeist.controller.dto.UbicacionDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/ubicacion")
public class UbicacionControllerREST {
    private final UbicacionService ubicacionService;
    private final MediumService mediumService;

    public UbicacionControllerREST(UbicacionService ubicacionService, MediumService mediumService) {
        this.ubicacionService = ubicacionService;
        this.mediumService = mediumService;
    }

    @PostMapping
    public ResponseEntity<?> createUbicacion(@RequestBody UbicacionDTO ubicacion){
        try {
            ubicacionService.crear(ubicacion.aModelo(), ubicacion.coordenadas().coordenadas(), ubicacion.descripcion());
            return ResponseEntity.status(HttpStatus.CREATED).body("Ubicacion creada exitosamente.");
        }
        catch (NivelDeEnergiaFueraDeRango exInv){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new NivelDeEnergiaFueraDeRango().getMessage());
        }
        catch (UbicacionSuperpuesta exInv){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new UbicacionSuperpuesta().getMessage());
        }
        catch (ConflictException exInv){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ConflictException().getMessage());
        }
        catch (IllegalArgumentException exInv) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(exInv.getMessage());
        }
    }

    @GetMapping("/{ubicacionId}")
    public ResponseEntity<?> getUbicacionById(@PathVariable Long ubicacionId){
        Optional<Ubicacion> ubicacionRecuperada = ubicacionService.recuperar(ubicacionId);

        if(ubicacionRecuperada.isPresent()){
            Ubicacion ubicacion = ubicacionRecuperada.get();
            UbicacionMongo ubicacionMongo = ubicacionService.recuperarMongo(ubicacionId).get();
            return  ResponseEntity.status(HttpStatus.OK)
                    .body(UbicacionDTO.desdeModelo(ubicacion, ubicacionMongo.getCoordenadas(), ubicacionService.recuperarDescripcion(ubicacion.getId())));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ubicacion no encontrada.");
    }

    @GetMapping
    public ResponseEntity<List<UbicacionDTO>> getUbicaciones(){
        List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();
        List<UbicacionDTO> ubicacionesDTO = this.recuperarUbicacionesConCoordenadas(ubicaciones);
        return ResponseEntity.status(HttpStatus.OK).body(ubicacionesDTO);
    }

    @PutMapping("/{ubicacionId}")
    public ResponseEntity<?> updateUbicacion(@PathVariable Long ubicacionId,
                                             @RequestBody UbicacionDTO ubicacion){
        try {
            Ubicacion ubicacionRecuperada = ubicacionService.recuperar(ubicacionId).get();
            Ubicacion ubicacionActualizada = ubicacion.actualizarModelo(ubicacionRecuperada);
            UbicacionMongo ubicacionMongo = ubicacionService.recuperarMongo(ubicacionId).get();
            ubicacionService.actualizar(ubicacionActualizada);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(UbicacionDTO.desdeModelo(ubicacionActualizada, ubicacionMongo.getCoordenadas(), ubicacion.descripcion()));
        }
        catch (NoSuchElementException exElem) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: id de ubicacion incorrecto");
        }
        catch (NivelDeEnergiaFueraDeRango exInv){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new NivelDeEnergiaFueraDeRango().getMessage());
        }
    }

    @DeleteMapping("{ubicacionId}")
    public ResponseEntity<String> deleteUbicacion(@PathVariable Long ubicacionId){
        try {
            ubicacionService.eliminar(ubicacionId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Si la Ubicacion existia, fue eliminada exitosamente");
        }
        catch (NoSuchElementException exElem) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: id de ubicacion incorrecto");
        }
        catch (EntidadAELiminarEstaRelacionada exInv){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new EntidadAELiminarEstaRelacionada().getMessage());
        }
    }

    @GetMapping("/espiritusEn/{ubicacionId}")
    public ResponseEntity<List<EspirituDTO>> getEspiritusEn(@PathVariable Long ubicacionId){
        List<EspirituDTO> espiritus = ubicacionService.espiritusEn(ubicacionId).stream()
                .map(EspirituDTO::desdeModelo)
                .toList();
            return ResponseEntity.status(HttpStatus.OK).body(espiritus);
    }

    @GetMapping("/mediumsSinEsp/{ubicacionId}")
    public ResponseEntity<List<MediumDTO>> getMediumsSinEspEn(@PathVariable Long ubicacionId){
        List<MediumDTO> mediums = ubicacionService.mediumsSinEspiritusEn(ubicacionId).stream()
                .map(m -> MediumDTO.desdeModelo(m , mediumService.recuperarDescripcion(m.getId())))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(mediums);
    }

    @PutMapping("/conectar/{idOrigen}/to/{idDestino}")
    public ResponseEntity<String> connect(@PathVariable Long idOrigen, @PathVariable Long idDestino){
        try {
            ubicacionService.conectar(idOrigen, idDestino);
            return ResponseEntity.status(HttpStatus.OK).body("Ubicaciones conectadas exitosamente!");
        }
        catch (NoSuchElementException exElem){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: id/s de ubicacion/es incorrecto/s");
        }
    }


    @GetMapping("/estanConectadas/{idOrigen}/to/{idDestino}")
    public ResponseEntity<String> getEstanConectadas(@PathVariable Long idOrigen,
                                                     @PathVariable Long idDestino){
        try {
            boolean estanConectadas = ubicacionService.estanConectadas(idOrigen, idDestino);
            if(estanConectadas){
                return ResponseEntity.status(HttpStatus.OK).body("Estan conectadas!");
            } else {
                return ResponseEntity.status(HttpStatus.OK).body("No están conectadas!");
            }
        }
        catch (NoSuchElementException exElem){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: id/s de ubicacion/es incorrecto/s");
        }
    }

    @GetMapping("/caminoMasCorto/{idOrigen}/to/{idDestino}")
    public ResponseEntity<?> getCaminoMasCorto(@PathVariable Long idOrigen,
                                               @PathVariable Long idDestino){
        try{
            List<Ubicacion> ubicaciones = ubicacionService.caminoMasCorto(idOrigen, idDestino);
            List<UbicacionDTO> ubicacionesDTO = this.recuperarUbicacionesConCoordenadas(ubicaciones);
            return ResponseEntity.status(HttpStatus.OK).body(ubicacionesDTO);
        }
        catch (NoSuchElementException exElem){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Por lo menos un id es incorrecto");
        }
        catch (UbicacionesNoConectadasException exElem){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Las ubicaciones no estan conectadas");
        }
    }


    @GetMapping("/closenessOf")
    public ResponseEntity<?> getClosenessOf(@RequestBody List<Long> ubicacionIds){
        List<ClosenessResult> closenessList = ubicacionService.closenessOf(ubicacionIds);
        return ResponseEntity.status(HttpStatus.OK).body(closenessList);
    }

    @GetMapping("/harmonicOf")
    public ResponseEntity<?> getHarmonicOf(@RequestBody List<Long> ubicacionIds){
        List<ClosenessResult> closenessList = ubicacionService.harmonicOf(ubicacionIds);
        return ResponseEntity.status(HttpStatus.OK).body(closenessList);
    }


    public List<UbicacionDTO> recuperarUbicacionesConCoordenadas(List<Ubicacion> ubicaciones) {
        List<Long> ids = ubicaciones.stream()
                .map(Ubicacion::getId)
                .toList();

        List<UbicacionMongo> ubicacionesMongo = ubicacionService.recuperarTodasMongo(ids);

        Map<Long, GeoJsonPolygon> coordenadasPorId = ubicacionesMongo.stream()
                .collect(Collectors.toMap(UbicacionMongo::getId, UbicacionMongo::getCoordenadas));

        return ubicaciones.stream()
                .map(ubi -> {
                    GeoJsonPolygon area = coordenadasPorId.get(ubi.getId());
                    return UbicacionDTO.desdeModelo(ubi, area, ubicacionService.recuperarDescripcion(ubi.getId()));
                })
                .toList();
    }

    @GetMapping("/busqueda-semantic")
    public List<UbicacionElastic> buscarMediumSemantico(@RequestParam String texto) {
        return ubicacionService.buscarSemanticamente(texto);
    }
}
