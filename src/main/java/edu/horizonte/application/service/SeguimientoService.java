package edu.horizonte.application.service;
import edu.horizonte.domain.exception.*;
import edu.horizonte.domain.model.*;
import edu.horizonte.domain.port.in.SeguimientoUseCase;
import edu.horizonte.domain.port.out.*;
import java.time.LocalDate;
import java.util.List;

/** Casos de uso puros: contratos propios, sin Spring Data ni SQL. */
public final class SeguimientoService implements SeguimientoUseCase {
    private final ProyectoRepositoryPort proyectos;
    private final EntregableRepositoryPort entregables;
    public SeguimientoService(ProyectoRepositoryPort proyectos, EntregableRepositoryPort entregables) {
        this.proyectos = proyectos;
        this.entregables = entregables;
    }
    public List<Proyecto> proyectos() { return proyectos.listar(); }
    public List<Entregable> entregables() { return entregables.listar(); }
    public List<Entregable> entregablesDe(Long id) { return entregables.deProyecto(id); }
    public Proyecto proyecto(Long id) {
        return proyectos.buscar(id).orElseThrow(() -> new RegistroNoEncontrado("El proyecto seleccionado no existe."));
    }
    public Entregable entregable(Long id) {
        return entregables.buscar(id).orElseThrow(() -> new RegistroNoEncontrado("El entregable no existe."));
    }
    public Proyecto guardarProyecto(Long id, String nombre, String descripcion) {
        if (id != null) proyecto(id);
        return proyectos.guardar(new Proyecto(id, nombre, descripcion));
    }
    public Entregable guardarEntregable(Long id, String titulo, LocalDate fecha, Long proyectoId) {
        if (id != null) entregable(id);
        var proyecto = proyecto(proyectoId);
        return entregables.guardar(new Entregable(id, titulo, fecha, proyecto));
    }
    public void eliminarProyecto(Long id) {
        proyecto(id);
        if (entregables.existenDeProyecto(id)) throw new ProyectoConEntregables();
        proyectos.eliminar(id);
    }
    public void eliminarEntregable(Long id) {
        entregable(id);
        entregables.eliminar(id);
    }
}
