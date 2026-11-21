package edu.horizonte.domain.port.in;
import edu.horizonte.domain.model.*;
import java.time.LocalDate;
import java.util.List;

/** Operaciones ofrecidas a cualquier cliente, independientemente de HTTP. */
public interface SeguimientoUseCase {
    List<Proyecto> proyectos();
    List<Entregable> entregables();
    List<Entregable> entregablesDe(Long proyectoId);
    Proyecto proyecto(Long id);
    Entregable entregable(Long id);
    Proyecto guardarProyecto(Long id, String nombre, String descripcion);
    Entregable guardarEntregable(Long id, String titulo, LocalDate fecha, Long proyectoId);
    void eliminarProyecto(Long id);
    void eliminarEntregable(Long id);
}
