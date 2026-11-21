package edu.horizonte;

import edu.horizonte.domain.exception.*;
import edu.horizonte.domain.model.*;
import edu.horizonte.domain.port.in.SeguimientoUseCase;
import edu.horizonte.infrastructure.adapter.out.persistence.*;
import edu.horizonte.infrastructure.config.SeguimientoTransaccional;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InfraestructuraTest {
    @Test void falloFkAlVaciarCambiosSeTraduceAReglaDelDominio() {
        var jpa = mock(ProyectoJpaRepository.class);
        doThrow(new DataIntegrityViolationException("detalle técnico SQL")).when(jpa).flush();
        var adapter = new ProyectoJpaAdapter(jpa);
        var error = assertThrows(ProyectoConEntregables.class, () -> adapter.eliminar(1L));
        assertFalse(error.getMessage().contains("SQL"));
        verify(jpa).deleteById(1L);
    }

    @Test void falloAlGuardarRelacionNoFiltraExcepcionDeSpring() {
        var proyectos = mock(ProyectoJpaRepository.class);
        var jpa = mock(EntregableJpaRepository.class);
        when(proyectos.findById(1L)).thenReturn(Optional.of(new ProyectoJpaEntity("P", "Descripción")));
        when(jpa.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("FK técnica"));
        var adapter = new EntregableJpaAdapter(jpa, proyectos);
        assertThrows(RelacionNoValida.class, () -> adapter.guardar(
                new Entregable(null, "Informe", LocalDate.of(2026,12,1), new Proyecto(1L,"P","Descripción"))));
    }

    @Test void consultaSeEjecutaEnTransaccionDeSoloLectura() {
        var manager = new TransaccionesPrueba();
        var caso = mock(SeguimientoUseCase.class);
        when(caso.proyectos()).thenAnswer(invocation -> {
            assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            return List.of();
        });
        new SeguimientoTransaccional(caso, manager).proyectos();
        assertTrue(manager.soloLectura);
        assertEquals(1, manager.commits);
    }

    @Test void escrituraFallidaRevierteTransaccionCompleta() {
        var manager = new TransaccionesPrueba();
        var caso = mock(SeguimientoUseCase.class);
        doThrow(new ProyectoConEntregables()).when(caso).eliminarProyecto(1L);
        var tx = new SeguimientoTransaccional(caso, manager);
        assertThrows(ProyectoConEntregables.class, () -> tx.eliminarProyecto(1L));
        assertFalse(manager.soloLectura);
        assertEquals(1, manager.rollbacks);
        assertEquals(0, manager.commits);
    }

    static class TransaccionesPrueba extends AbstractPlatformTransactionManager {
        boolean soloLectura;
        int commits;
        int rollbacks;
        protected Object doGetTransaction() { return new Object(); }
        protected void doBegin(Object tx, TransactionDefinition def) { soloLectura = def.isReadOnly(); }
        protected void doCommit(DefaultTransactionStatus status) { commits++; }
        protected void doRollback(DefaultTransactionStatus status) { rollbacks++; }
    }
}
