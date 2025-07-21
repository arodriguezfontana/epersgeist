package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.Ubicacion;

public record ClosenessResult(Ubicacion ubicacion, Double closeness) {}
