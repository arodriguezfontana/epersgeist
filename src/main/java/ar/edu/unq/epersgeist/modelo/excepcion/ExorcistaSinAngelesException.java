package ar.edu.unq.epersgeist.modelo.excepcion;

import ar.edu.unq.epersgeist.modelo.Medium;

public class ExorcistaSinAngelesException extends RuntimeException {
    public ExorcistaSinAngelesException(Medium mediumExorcista, Medium mediumAExorcizar) {
        super(
                mediumExorcista.getNombre() + " no tiene ángeles para exorcizar a " + mediumAExorcizar.getNombre() + "."
        );
    }
}
