package edu.horizonte.servicio;
import edu.horizonte.modelo.*;
import edu.horizonte.repositorio.*;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
public class SeguimientoService {
    private final ProyectoRepositorio proyectos;
    private final EntregableRepositorio entregables;
    public SeguimientoService(ProyectoRepositorio proyectos, EntregableRepositorio entregables) {
        this.proyectos=proyectos;
        this.entregables=entregables;
    }
    public List<Proyecto> proyectos() { return proyectos.findAllByOrderByIdDesc(); }
    public List<Entregable> entregables() { return entregables.findAllByOrderByIdDesc(); }
    public List<Entregable> entregablesDe(Long id) { return entregables.findByProyectoIdOrderByFechaAsc(id); }
    public Proyecto proyecto(Long id) {
        return proyectos.findById(id).orElseThrow(() -> new RegistroNoEncontrado("El proyecto seleccionado no existe."));
    }
    public Entregable entregable(Long id) {
        return entregables.findById(id).orElseThrow(() -> new RegistroNoEncontrado("El entregable no existe."));
    }
    @Transactional
    public Proyecto guardarProyecto(Long id, String nombre, String descripcion) {
        if (id==null) return proyectos.save(new Proyecto(nombre,descripcion));
        Proyecto p=proyecto(id);
        p.actualizar(nombre,descripcion);
        return p;
    }
    @Transactional
    public Entregable guardarEntregable(Long id, String titulo, LocalDate fecha, Long proyectoId) {
        Proyecto p=proyecto(proyectoId);
        if (id==null) return entregables.save(new Entregable(titulo,fecha,p));
        Entregable e=entregable(id);
        e.actualizar(titulo,fecha,p);
        return e;
    }
    @Transactional
    public void eliminarProyecto(Long id) {
        Proyecto p=proyecto(id);
        if (entregables.existsByProyectoId(id)) throw new ProyectoConEntregables();
        proyectos.delete(p);
        proyectos.flush();
    }
    @Transactional
    public void eliminarEntregable(Long id) { entregables.delete(entregable(id)); }
}
