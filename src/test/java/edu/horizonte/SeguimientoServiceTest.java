package edu.horizonte;

import edu.horizonte.application.service.SeguimientoService;
import edu.horizonte.domain.exception.*;
import edu.horizonte.domain.model.*;
import edu.horizonte.domain.port.out.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SeguimientoServiceTest {
    private final ProyectosMemoria proyectos = new ProyectosMemoria();
    private final EntregablesMemoria entregables = new EntregablesMemoria();
    private final SeguimientoService caso = new SeguimientoService(proyectos, entregables);
    private final LocalDate fecha = LocalDate.of(2026, 12, 1);

    @Test void actualizacionGuardaExplicitamenteObjetosDesconectados() {
        var p = caso.guardarProyecto(null, " Proyecto ", " Descripción ");
        caso.guardarProyecto(p.getId(), "Nuevo nombre", "Nueva descripción");
        assertEquals("Nuevo nombre", caso.proyecto(p.getId()).getNombre());
        assertEquals(2, proyectos.escrituras);
        assertEquals(1, caso.proyectos().size());
        var e = caso.guardarEntregable(null, "Inicial", fecha, p.getId());
        caso.guardarEntregable(e.getId(), "Final", fecha.plusDays(1), p.getId());
        assertEquals("Final", caso.entregable(e.getId()).getTitulo());
        assertEquals(fecha.plusDays(1), caso.entregable(e.getId()).getFecha());
        assertEquals(2, entregables.escrituras);
    }

    @Test void relacionReasignacionYEliminacionConservanReglas() {
        var a = caso.guardarProyecto(null, "A", "Primero");
        var b = caso.guardarProyecto(null, "B", "Segundo");
        var e = caso.guardarEntregable(null, "Informe", fecha, a.getId());
        assertEquals(1, caso.entregables().size());
        assertEquals(1, caso.entregablesDe(a.getId()).size());
        assertThrows(ProyectoConEntregables.class, () -> caso.eliminarProyecto(a.getId()));
        assertEquals(2, caso.proyectos().size());
        caso.guardarEntregable(e.getId(), "Reasignado", fecha, b.getId());
        assertEquals(b.getId(), caso.entregable(e.getId()).getProyecto().getId());
        assertTrue(caso.entregablesDe(a.getId()).isEmpty());
        caso.eliminarProyecto(a.getId());
        caso.eliminarEntregable(e.getId());
        caso.eliminarProyecto(b.getId());
        assertTrue(caso.proyectos().isEmpty());
        assertTrue(caso.entregables().isEmpty());
    }

    @Test void idsInexistentesNoInsertanNiEliminanOtrosRegistros() {
        var p = caso.guardarProyecto(null, "Existente", "Descripción");
        assertThrows(RegistroNoEncontrado.class, () -> caso.guardarProyecto(99L, "Otro", "Dato"));
        assertThrows(RegistroNoEncontrado.class, () -> caso.guardarEntregable(99L, "Otro", fecha, p.getId()));
        assertThrows(RegistroNoEncontrado.class, () -> caso.guardarEntregable(null, "Otro", fecha, 99L));
        assertThrows(RegistroNoEncontrado.class, () -> caso.eliminarProyecto(99L));
        assertThrows(RegistroNoEncontrado.class, () -> caso.eliminarEntregable(99L));
        assertEquals(1, proyectos.escrituras);
        assertEquals(0, entregables.escrituras);
        assertTrue(caso.entregables().isEmpty());
    }

    static class ProyectosMemoria implements ProyectoRepositoryPort {
        final Map<Long, Proyecto> datos = new LinkedHashMap<>();
        long secuencia;
        int escrituras;
        private Proyecto copia(Proyecto p) { return new Proyecto(p.getId(), p.getNombre(), p.getDescripcion()); }
        public List<Proyecto> listar() { return datos.values().stream().map(this::copia).toList(); }
        public Optional<Proyecto> buscar(Long id) { return Optional.ofNullable(datos.get(id)).map(this::copia); }
        public Proyecto guardar(Proyecto p) {
            escrituras++;
            var guardado = new Proyecto(p.getId() == null ? ++secuencia : p.getId(), p.getNombre(), p.getDescripcion());
            datos.put(guardado.getId(), copia(guardado));
            return copia(guardado);
        }
        public void eliminar(Long id) { datos.remove(id); }
    }

    static class EntregablesMemoria implements EntregableRepositoryPort {
        final Map<Long, Entregable> datos = new LinkedHashMap<>();
        long secuencia;
        int escrituras;
        private Entregable copia(Entregable e) { return new Entregable(e.getId(), e.getTitulo(), e.getFecha(), e.getProyecto()); }
        public List<Entregable> listar() { return datos.values().stream().map(this::copia).toList(); }
        public Optional<Entregable> buscar(Long id) { return Optional.ofNullable(datos.get(id)).map(this::copia); }
        public List<Entregable> deProyecto(Long id) { return listar().stream().filter(e -> e.getProyecto().getId().equals(id)).toList(); }
        public boolean existenDeProyecto(Long id) { return !deProyecto(id).isEmpty(); }
        public Entregable guardar(Entregable e) {
            escrituras++;
            var guardado = new Entregable(e.getId() == null ? ++secuencia : e.getId(), e.getTitulo(), e.getFecha(), e.getProyecto());
            datos.put(guardado.getId(), copia(guardado));
            return copia(guardado);
        }
        public void eliminar(Long id) { datos.remove(id); }
    }
}
