package edu.horizonte.infrastructure.config;

import edu.horizonte.application.service.SeguimientoService;
import edu.horizonte.domain.port.in.SeguimientoUseCase;
import edu.horizonte.domain.port.out.*;
import org.springframework.context.annotation.*;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HexagonalConfig {
    @Bean
    public SeguimientoUseCase seguimientoUseCase(ProyectoRepositoryPort proyectos,
            EntregableRepositoryPort entregables, PlatformTransactionManager manager) {
        return new SeguimientoTransaccional(new SeguimientoService(proyectos, entregables), manager);
    }
}
