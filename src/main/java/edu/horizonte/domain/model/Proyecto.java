package edu.horizonte.domain.model;

/** Modelo de negocio independiente del mapeo de base de datos. */
public final class Proyecto {
    private final Long id;
    private final String nombre;
    private final String descripcion;

    public Proyecto(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre.strip();
        this.descripcion = descripcion.strip();
    }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}
