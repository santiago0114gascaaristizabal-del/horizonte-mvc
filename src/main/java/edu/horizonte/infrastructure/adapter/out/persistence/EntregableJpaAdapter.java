package edu.horizonte.infrastructure.adapter.out.persistence;

import edu.horizonte.domain.exception.*;
import edu.horizonte.domain.model.Entregable;
import edu.horizonte.domain.port.out.EntregableRepositoryPort;
import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class EntregableJpaAdapter implements EntregableRepositoryPort {
    private final EntregableJpaRepository jpa;
    private final ProyectoJpaRepository proyectos;
    public EntregableJpaAdapter(EntregableJpaRepository jpa, ProyectoJpaRepository proyectos) {
        this.jpa = jpa;
        this.proyectos = proyectos;
    }
    public List<Entregable> listar() {
        return jpa.findAllByOrderByIdDesc().stream().map(PersistenciaMapper::aDominio).toList();
    }
    public Optional<Entregable> buscar(Long id) { return jpa.findById(id).map(PersistenciaMapper::aDominio); }
    public List<Entregable> deProyecto(Long id) {
        return jpa.findByProyectoIdOrderByFechaAsc(id).stream().map(PersistenciaMapper::aDominio).toList();
    }
    public boolean existenDeProyecto(Long id) { return jpa.existsByProyectoId(id); }
    public Entregable guardar(Entregable e) {
        var proyecto = proyectos.findById(e.getProyecto().getId())
                .orElseThrow(() -> new RegistroNoEncontrado("El proyecto seleccionado no existe."));
        var entity = e.getId() == null ? new EntregableJpaEntity(e.getTitulo(), e.getFecha(), proyecto)
                : jpa.findById(e.getId()).orElseThrow(() -> new RegistroNoEncontrado("El entregable no existe."));
        entity.actualizar(e.getTitulo(), e.getFecha(), proyecto);
        try {
            return PersistenciaMapper.aDominio(jpa.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            throw new RelacionNoValida();
        }
    }
    public void eliminar(Long id) { jpa.deleteById(id); jpa.flush(); }
}
