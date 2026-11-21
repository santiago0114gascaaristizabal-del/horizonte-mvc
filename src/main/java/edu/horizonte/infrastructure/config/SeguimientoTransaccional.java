package edu.horizonte.infrastructure.config;

import edu.horizonte.domain.model.*;
import edu.horizonte.domain.port.in.SeguimientoUseCase;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/** Límite técnico alrededor de cada caso de uso, sin anotar el núcleo. */
public final class SeguimientoTransaccional implements SeguimientoUseCase {
    private final SeguimientoUseCase caso;
    private final TransactionTemplate lectura;
    private final TransactionTemplate escritura;
    public SeguimientoTransaccional(SeguimientoUseCase caso, PlatformTransactionManager manager) {
        this.caso = caso;
        lectura = new TransactionTemplate(manager);
        lectura.setReadOnly(true);
        escritura = new TransactionTemplate(manager);
    }
    private <T> T leer(Supplier<T> accion) { return lectura.execute(status -> accion.get()); }
    private <T> T guardar(Supplier<T> accion) { return escritura.execute(status -> accion.get()); }
    public List<Proyecto> proyectos() { return leer(caso::proyectos); }
    public List<Entregable> entregables() { return leer(caso::entregables); }
    public List<Entregable> entregablesDe(Long id) { return leer(() -> caso.entregablesDe(id)); }
    public Proyecto proyecto(Long id) { return leer(() -> caso.proyecto(id)); }
    public Entregable entregable(Long id) { return leer(() -> caso.entregable(id)); }
    public Proyecto guardarProyecto(Long id, String nombre, String descripcion) {
        return guardar(() -> caso.guardarProyecto(id, nombre, descripcion));
    }
    public Entregable guardarEntregable(Long id, String titulo, LocalDate fecha, Long proyectoId) {
        return guardar(() -> caso.guardarEntregable(id, titulo, fecha, proyectoId));
    }
    public void eliminarProyecto(Long id) { escritura.executeWithoutResult(status -> caso.eliminarProyecto(id)); }
    public void eliminarEntregable(Long id) { escritura.executeWithoutResult(status -> caso.eliminarEntregable(id)); }
}
