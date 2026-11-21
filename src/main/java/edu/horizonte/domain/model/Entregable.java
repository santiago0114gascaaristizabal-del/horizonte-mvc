package edu.horizonte.domain.model;

import java.time.LocalDate;

public final class Entregable {
    private final Long id;
    private final String titulo;
    private final LocalDate fecha;
    private final Proyecto proyecto;

    public Entregable(Long id, String titulo, LocalDate fecha, Proyecto proyecto) {
        this.id = id;
        this.titulo = titulo.strip();
        this.fecha = fecha;
        this.proyecto = proyecto;
    }
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public LocalDate getFecha() { return fecha; }
    public Proyecto getProyecto() { return proyecto; }
}
