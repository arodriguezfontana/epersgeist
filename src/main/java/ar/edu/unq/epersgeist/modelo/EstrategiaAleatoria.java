package ar.edu.unq.epersgeist.modelo;

import java.util.Random;

public class EstrategiaAleatoria implements EstrategiaGeneracionDeNumeros {

    private final Random random = new Random();

    @Override
    public int numeroAleatorioEntre(int hasta) {
        return random.nextInt(hasta);
    }
}