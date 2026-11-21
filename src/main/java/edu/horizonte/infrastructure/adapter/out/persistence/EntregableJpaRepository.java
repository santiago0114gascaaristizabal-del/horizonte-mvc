package edu.horizonte.infrastructure.adapter.out.persistence;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntregableJpaRepository extends JpaRepository<EntregableJpaEntity,Long> {
    @EntityGraph(attributePaths="proyecto")
    List<EntregableJpaEntity> findAllByOrderByIdDesc();
    @EntityGraph(attributePaths="proyecto")
    Optional<EntregableJpaEntity> findById(Long id);
    @EntityGraph(attributePaths="proyecto")
    List<EntregableJpaEntity> findByProyectoIdOrderByFechaAsc(Long proyectoId);
    boolean existsByProyectoId(Long proyectoId);
}
