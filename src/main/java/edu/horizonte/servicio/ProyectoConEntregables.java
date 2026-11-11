package edu.horizonte.servicio;
public class ProyectoConEntregables extends RuntimeException {
    public ProyectoConEntregables() { super("Elimina o reasigna los entregables antes de borrar el proyecto."); }
}
