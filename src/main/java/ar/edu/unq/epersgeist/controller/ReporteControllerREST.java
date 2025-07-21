package ar.edu.unq.epersgeist.controller;


import ar.edu.unq.epersgeist.controller.dto.ReporteDTO;
import ar.edu.unq.epersgeist.controller.dto.SnapShotDTO;
import ar.edu.unq.epersgeist.modelo.ReporteSantuarioMasCorrupto;
import ar.edu.unq.epersgeist.modelo.SnapShot;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reporte")
public class ReporteControllerREST {


    private final EstadisticaService estadisticaService;


    public ReporteControllerREST(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }


    @GetMapping
    public ResponseEntity<?> santuarioCorrupto(){
        try {
            ReporteSantuarioMasCorrupto reporte = estadisticaService.santuarioCorrupto();
            return ResponseEntity.status(HttpStatus.OK).body(ReporteDTO.desdeModelo(reporte));
        }catch (NoSuchElementException exElem){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Recurso no encontrado: " + exElem.getMessage());
        }
    }
    @PostMapping("/crearSnapshoot")
    public ResponseEntity<?> crearSnapshot() {
        estadisticaService.crearSnapshot();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("El SnapShot fue creado exitosamente");
    }
    @GetMapping("/snapshoot")
    public ResponseEntity<?> obtenerSnapShot(@RequestBody LocalDate fecha) {
        try {
            SnapShot snapshot = estadisticaService.obtenerSnapshot(fecha);
            return ResponseEntity.status(HttpStatus.OK).body(SnapShotDTO.desdeModelo(snapshot));
        } catch(NoSuchElementException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body("Recurso no encontrado: " + e.getMessage());
        }

    }

}
