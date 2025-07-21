package ar.edu.unq.epersgeist.modelo.excepcion;

public class EntidadAELiminarEstaRelacionada extends RuntimeException {
  public EntidadAELiminarEstaRelacionada() {

    super("La Entidad que intenta eliminar esta relacionada con otra/s entidad/es");
  }
}
