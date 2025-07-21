package ar.edu.unq.epersgeist.controller.dto;

import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.geo.Point;

import java.util.ArrayList;
import java.util.List;

public record AreaDTO(List<List<Double>> coordenadas) {

    public static AreaDTO desdeModelo(GeoJsonPolygon geoJsonPolygon) {
        if (geoJsonPolygon == null || geoJsonPolygon.getCoordinates().isEmpty()) {
            return new AreaDTO(new ArrayList<>());
        }

        List<List<Double>> coordenadasDTO = new ArrayList<>();
        GeoJsonLineString exteriorRing = geoJsonPolygon.getCoordinates().get(0);

        for (Point point : exteriorRing.getCoordinates()) {
            List<Double> par = List.of(point.getX(), point.getY());
            coordenadasDTO.add(par);
        }

        return new AreaDTO(coordenadasDTO);
    }

}
