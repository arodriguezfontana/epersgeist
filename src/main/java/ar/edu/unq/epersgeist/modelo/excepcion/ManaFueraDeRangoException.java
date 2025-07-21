package ar.edu.unq.epersgeist.modelo.excepcion;

public class ManaFueraDeRangoException extends RuntimeException {
    public ManaFueraDeRangoException() {

        super("El mana y manaMax deben ser valores mayores o iguales a 0 y el mana no debe superar el mana maximo." );
    }
}
