package ar.edu.unq.epersgeist.modelo.excepcion;

public class ValorNegativoDePaginacion extends RuntimeException {
    public ValorNegativoDePaginacion() {
        super("El tamaño de pagina o la cantidad de valores por pagina no deben ser negativos");
    }
}
