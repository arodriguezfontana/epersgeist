package ar.edu.unq.epersgeist.modelo;

public class ValidadorDeRangos {

    public static int validarRango(int valor, int minimo, int maximo, RuntimeException excepcion) {
        if (valor >= minimo && valor <= maximo) {
            return valor;
        } else {
            throw excepcion;
        }
    }

    public static int validarMinimo(int valor, int minimo, RuntimeException excepcion) {
        if (valor >= minimo) {
            return valor;
        } else {
            throw excepcion;
        }
    }
}
