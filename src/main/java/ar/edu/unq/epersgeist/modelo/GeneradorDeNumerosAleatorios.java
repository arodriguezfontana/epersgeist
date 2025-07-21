package ar.edu.unq.epersgeist.modelo;

public class GeneradorDeNumerosAleatorios{
    private static GeneradorDeNumerosAleatorios generadorDeNumerosAleatorios;
    private EstrategiaGeneracionDeNumeros estrategia;
    
    private GeneradorDeNumerosAleatorios() {
        this.estrategia = new EstrategiaAleatoria();
    }

    public static GeneradorDeNumerosAleatorios getGeneradorDeNumerosAleatorios() {
        if (generadorDeNumerosAleatorios == null) {
            generadorDeNumerosAleatorios = new GeneradorDeNumerosAleatorios();
        }
        return generadorDeNumerosAleatorios;
    }

    public void setEstrategia(EstrategiaGeneracionDeNumeros estrategia) {
        this.estrategia = estrategia;
    }

    public int numeroAleatorioEntre(int hasta) {
        return estrategia.numeroAleatorioEntre(hasta);
    }
}
