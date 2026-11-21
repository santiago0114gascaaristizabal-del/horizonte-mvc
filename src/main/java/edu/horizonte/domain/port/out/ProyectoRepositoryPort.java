package edu.horizonte.domain.port.out;
import edu.horizonte.domain.model.Proyecto;
import java.util.List;
import java.util.Optional;

public interface ProyectoRepositoryPort {
    List<Proyecto> listar();
    Optional<Proyecto> buscar(Long id);
    Proyecto guardar(Proyecto proyecto);
    void eliminar(Long id);
}
