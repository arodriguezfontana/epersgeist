package ar.edu.unq.epersgeist.modelo;



import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.geo.Point;
import java.util.ArrayList;
import java.util.List;

public class CoordenadaGenerator {

    public static GeoJsonPolygon generarPoligono(List<List<Double>> coordenadas) {
        List<Point> puntos = new ArrayList<>();

        for (List<Double> par : coordenadas) {
            puntos.add(new Point(par.get(0), par.get(1)));
        }

        return new GeoJsonPolygon(puntos);
    }

}