package edu.horizonte.domain.exception;
public class ProyectoConEntregables extends RuntimeException {
    public ProyectoConEntregables() { super("Elimina o reasigna los entregables antes de borrar el proyecto."); }
}
