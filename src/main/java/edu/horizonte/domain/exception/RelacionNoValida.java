package edu.horizonte.domain.exception;
public class RelacionNoValida extends RuntimeException {
    public RelacionNoValida() { super("No se pudo guardar la relación. Comprueba el proyecto y vuelve a intentar."); }
}
