package edu.horizonte.servicio;
public class RegistroNoEncontrado extends RuntimeException {
    public RegistroNoEncontrado(String mensaje) { super(mensaje); }
}
