package ar.edu.unq.epersgeist.modelo;

public enum TipoUbicacion {
    SANTUARIO, CEMENTERIO;

    public TipoEspiritu getTipoAsociado() {
        return switch (this) {
            case SANTUARIO -> TipoEspiritu.ANGEL;
            case CEMENTERIO -> TipoEspiritu.DEMONIO;
        };
    }
}
