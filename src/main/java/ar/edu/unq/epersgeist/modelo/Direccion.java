package ar.edu.unq.epersgeist.modelo;

public enum Direccion {
    ASCENDENTE("ASC"), DESCENDENTE("DESC");
    public final String direccion;
    Direccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }
}
