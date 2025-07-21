package ar.edu.unq.epersgeist.modelo;

public class EstrategiaDeterminista implements EstrategiaGeneracionDeNumeros {

    @Override
    public int numeroAleatorioEntre(int hasta) {
        return hasta / 2;
    }
}
