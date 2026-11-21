package edu.horizonte.infrastructure.adapter.out.persistence;

import edu.horizonte.domain.model.*;

/** La traducción se ejecuta dentro de la transacción; no salen proxies JPA. */
public final class PersistenciaMapper {
    private PersistenciaMapper() {}
    public static Proyecto aDominio(ProyectoJpaEntity p) {
        return new Proyecto(p.getId(), p.getNombre(), p.getDescripcion());
    }
    public static Entregable aDominio(EntregableJpaEntity e) {
        return new Entregable(e.getId(), e.getTitulo(), e.getFecha(), aDominio(e.getProyecto()));
    }
}
