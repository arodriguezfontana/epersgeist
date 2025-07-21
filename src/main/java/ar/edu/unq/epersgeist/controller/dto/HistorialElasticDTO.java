package ar.edu.unq.epersgeist.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HistorialElasticDTO {
    private String id;
    private Long espirituId;
    private Long ubicacionAsociadaId;
    private GeoPointDTO ubicacion;
    private Date fecha;

    public HistorialElasticDTO() {}

    public Long getEspirituId() {
        return espirituId;
    }

    public void setEspirituId(Long espirituId) {
        this.espirituId = espirituId;
    }

    public Long getUbicacionAsociadaId() {
        return ubicacionAsociadaId;
    }

    public void setUbicacionAsociadaId(Long ubicacionAsociadaId) {
        this.ubicacionAsociadaId = ubicacionAsociadaId;
    }

    public GeoPointDTO getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(GeoPointDTO ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeoPointDTO {
        private double lat;
        private double lon;

        public GeoPointDTO() {}

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }
    }
}



