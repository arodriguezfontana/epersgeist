package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.EspirituElastic;
import ar.edu.unq.epersgeist.modelo.MediumElastic;
import ar.edu.unq.epersgeist.modelo.UbicacionElastic;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BusquedaSemantica {

    private static final String BASE_URL = "http://localhost:5050";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BusquedaSemantica() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public void indexarMedium(MediumElastic medium) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(medium);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/indexar-medium"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error al indexar medium semánticamente: " + response.body());
        }
    }

    public void indexarEspiritu(EspirituElastic medium) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(medium);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/indexar-espiritu"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error al indexar espiritu semánticamente: " + response.body());
        }
    }

    public void indexarUbicacion(UbicacionElastic medium) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(medium);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/indexar-ubicacion"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error al indexar ubicacion semánticamente: " + response.body());
        }
    }

    public List<Long> buscar(String texto) throws IOException, InterruptedException {
        Map<String, String> payload = Map.of("texto", texto);
        String json = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/buscar-medium"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error al buscar medium semánticamente: " + response.body());
        }

        List<Map<String, Object>> resultados = objectMapper.readValue(
                response.body(), new TypeReference<>() {}
        );

        return resultados.stream()
                .map(r -> Long.valueOf(r.get("id").toString()))
                .collect(Collectors.toList());
    }
}