package edu.horizonte.repositorio;
import edu.horizonte.modelo.Entregable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntregableRepositorio extends JpaRepository<Entregable,Long> {
    @EntityGraph(attributePaths="proyecto")
    List<Entregable> findAllByOrderByIdDesc();
    @EntityGraph(attributePaths="proyecto")
    Optional<Entregable> findById(Long id);
    List<Entregable> findByProyectoIdOrderByFechaAsc(Long proyectoId);
    boolean existsByProyectoId(Long proyectoId);
}
