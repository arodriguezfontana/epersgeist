package ar.edu.unq.epersgeist.modelo;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
public interface CoordenadaSimpleProjection {
    @Value("#{target.coordenadas[0]}")
    List<Double> getCoordenada(); // El nombre del método debe coincidir (ignorando 'get') con el campo proyectado
}
