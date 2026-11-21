package edu.horizonte.domain.port.out;
import edu.horizonte.domain.model.Entregable;
import java.util.List;
import java.util.Optional;

public interface EntregableRepositoryPort {
    List<Entregable> listar();
    Optional<Entregable> buscar(Long id);
    List<Entregable> deProyecto(Long proyectoId);
    boolean existenDeProyecto(Long proyectoId);
    Entregable guardar(Entregable entregable);
    void eliminar(Long id);
}
