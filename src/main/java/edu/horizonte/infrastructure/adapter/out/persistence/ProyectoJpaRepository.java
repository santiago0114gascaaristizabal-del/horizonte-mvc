package edu.horizonte.infrastructure.adapter.out.persistence;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProyectoJpaRepository extends JpaRepository<ProyectoJpaEntity,Long> {
    List<ProyectoJpaEntity> findAllByOrderByIdDesc();
}
