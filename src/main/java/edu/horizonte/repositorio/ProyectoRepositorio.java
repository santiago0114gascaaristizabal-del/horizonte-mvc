package edu.horizonte.repositorio;
import edu.horizonte.modelo.Proyecto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProyectoRepositorio extends JpaRepository<Proyecto,Long> {
    List<Proyecto> findAllByOrderByIdDesc();
}
