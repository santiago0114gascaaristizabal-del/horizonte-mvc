package edu.horizonte.infrastructure.adapter.out.persistence;

import edu.horizonte.domain.exception.*;
import edu.horizonte.domain.model.Proyecto;
import edu.horizonte.domain.port.out.ProyectoRepositoryPort;
import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class ProyectoJpaAdapter implements ProyectoRepositoryPort {
    private final ProyectoJpaRepository jpa;
    public ProyectoJpaAdapter(ProyectoJpaRepository jpa) { this.jpa = jpa; }
    public List<Proyecto> listar() {
        return jpa.findAllByOrderByIdDesc().stream().map(PersistenciaMapper::aDominio).toList();
    }
    public Optional<Proyecto> buscar(Long id) { return jpa.findById(id).map(PersistenciaMapper::aDominio); }
    public Proyecto guardar(Proyecto p) {
        var entity = p.getId() == null ? new ProyectoJpaEntity(p.getNombre(), p.getDescripcion())
                : jpa.findById(p.getId()).orElseThrow(() -> new RegistroNoEncontrado("El proyecto seleccionado no existe."));
        entity.actualizar(p.getNombre(), p.getDescripcion());
        return PersistenciaMapper.aDominio(jpa.saveAndFlush(entity));
    }
    public void eliminar(Long id) {
        try {
            jpa.deleteById(id);
            jpa.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ProyectoConEntregables();
        }
    }
}
