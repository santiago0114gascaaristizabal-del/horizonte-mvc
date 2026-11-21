package edu.horizonte;

import java.nio.file.*;
import java.util.*;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class ArquitecturaTest {
    @TempDir Path temporal;

    @Test void nucleoCompilaSinSpringJpaNiInfraestructura() throws Exception {
        var raiz = Path.of("src/main/java/edu/horizonte");
        var fuentes = new ArrayList<String>();
        for (var paquete : List.of("domain", "application")) {
            try (var archivos = Files.walk(raiz.resolve(paquete))) {
                fuentes.addAll(archivos.filter(p -> p.toString().endsWith(".java")).map(Path::toString).toList());
            }
        }
        assertTrue(fuentes.size() >= 9, "Deben existir modelos, puertos y caso de uso reales");
        var vacio = Files.createDirectory(temporal.resolve("sin-librerias"));
        var salida = Files.createDirectory(temporal.resolve("clases"));
        var args = new ArrayList<>(List.of("--release", "21", "-classpath", vacio.toString(), "-d", salida.toString()));
        args.addAll(fuentes);
        assertEquals(0, ToolProvider.getSystemJavaCompiler().run(null, null, null, args.toArray(String[]::new)),
                "El núcleo debe compilar solo con el JDK, sin clases de Spring/JPA");
    }

    @Test void controladoresUsanPuertoYAdaptadoresImplementanContratos() throws Exception {
        var entrada = Class.forName("edu.horizonte.domain.port.in.SeguimientoUseCase");
        for (var nombre : List.of("ProyectoController", "EntregableController")) {
            var tipo = Class.forName("edu.horizonte.infrastructure.adapter.in.web." + nombre);
            assertArrayEquals(new Class<?>[]{entrada}, tipo.getConstructors()[0].getParameterTypes());
        }
        for (var nombre : List.of("Proyecto", "Entregable")) {
            var puerto = Class.forName("edu.horizonte.domain.port.out." + nombre + "RepositoryPort");
            var adaptador = Class.forName("edu.horizonte.infrastructure.adapter.out.persistence." + nombre + "JpaAdapter");
            assertTrue(puerto.isAssignableFrom(adaptador));
        }
        var servicio = Class.forName("edu.horizonte.application.service.SeguimientoService");
        assertTrue(entrada.isAssignableFrom(servicio));
        for (var dependencia : servicio.getConstructors()[0].getParameterTypes()) {
            assertTrue(dependencia.getName().startsWith("edu.horizonte.domain.port.out."));
        }
    }
}
