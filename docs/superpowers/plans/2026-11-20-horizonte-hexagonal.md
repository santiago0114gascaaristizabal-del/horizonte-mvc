# Horizonte Hexagonal Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Migrar Horizonte a puertos y adaptadores manteniendo ambos CRUD y entregar código y documentación verificados.
**Architecture:** Núcleo Java sin Spring/JPA. Un puerto de entrada, dos de salida y adaptadores web/JPA con límite transaccional en infraestructura.
**Tech Stack:** Java 21, Spring Boot 3.5.16, Maven 3.9, Thymeleaf, SQL Express.
**Spec:** docs/superpowers/specs/2026-11-20-horizonte-hexagonal-design.md (aprobado).

## Global Constraints

- main conserva MVC; la entrega vive en arquitectura-hexagonal.
- Mantener esquema HorizonteMVC y rutas; ninguna eliminación de datos ajenos ni credenciales en Git.
- SQL 127.0.0.1:14333; web 8080, verificación temporal 18081.
- Publicar fuente, documento técnico y manual Word/PDF y ZIP.

## Review Focus

- Actualizar un id ausente debe fallar, no insertar: prueba unitaria de actualización inexistente.
- La reasignación debe cambiar el proyecto persistido: prueba MockMvc con segunda fila.
- Dominio desconectado no tiene dirty checking: verificar guardado explícito tras actualizar con adaptador de prueba que devuelve copias.
- Error FK en flush debe traducirse sin exponer SQL: prueba del adaptador con fallo de integridad simulado y error estable.
- Vistas de entregables deben resolver su proyecto con open-in-view=false: conservar las consultas de detalle/listado de la regresión y ejecutar navegador SQL.

### Task 1: Núcleo independiente

**Files:** Crear domain/model/{Proyecto,Entregable}.java, domain/exception/*.java, domain/port/in/SeguimientoUseCase.java, domain/port/out/{ProyectoRepositoryPort,EntregableRepositoryPort}.java, application/service/SeguimientoService.java; test/java/edu/horizonte/SeguimientoServiceTest.java.
**Interfaces:** El puerto de entrada conserva las nueve firmas del servicio MVC (proyectos(), entregables(), entregablesDe(Long), proyecto(Long), entregable(Long), guardarProyecto(Long,String,String), guardarEntregable(Long,String,LocalDate,Long), eliminarProyecto(Long), eliminarEntregable(Long)). Puertos de salida usan listar(), buscar(Long), guardar(Modelo), eliminar(Long); entregables añade deProyecto(Long), existenDeProyecto(Long).
- [x] Escribir pruebas unitarias que usen adaptadores en memoria y objetos independientes para verificar CRUD, reasignación, guardado explícito, ids inexistentes y borrado protegido.
- [x] Ejecutar mvn -Dtest=SeguimientoServiceTest test; observar fallo por núcleo inexistente.
- [x] Implementar contratos, modelos con getters y copias actualizadas, errores y servicio sin importaciones externas.
- [x] Ejecutar misma prueba, exigir verde y confirmar compilación javac del núcleo sin classpath externo.
- [x] Commit del núcleo y pruebas.

### Task 2: Integración web y persistencia

**Files:** infrastructure/adapter/in/web/*.java; infrastructure/adapter/out/persistence/*.java; infrastructure/config/{HexagonalConfig,SeguimientoTransaccional}.java; FlujoMvcTest.java, ArquitecturaTest.java, PersistenciaAdapterTest.java.
**Interfaces:** Web recibe SeguimientoUseCase; servicio recibe dos puertos de salida. Adaptadores implementan esos puertos con mapeadores y repositorios JPA. Decorador implementa las nueve operaciones dentro de TransactionTemplate.
- [x] Añadir prueba de independencia del núcleo/relaciones de dependencia y pruebas MockMvc de reasignación y actualización inexistente. Ejecutar y observar fallo con estructura MVC.
- [x] Migrar entidades JPA conservando cada columna, crear mapeadores/adaptadores y traducir integridad tras saveAndFlush/delete+flush.
- [x] Mover controladores a adaptadores web, cambiar dependencia a puerto y errores a dominio. Mantener templates/static y rutas.
- [x] Registrar núcleo mediante beans y decorador transaccional, con lecturas de solo lectura. Retirar clases MVC sustituidas.
- [x] Ejecutar mvn test/package y compilar núcleo solo con javac. Probar traducción de FK con doble de repositorio concreto.
- [x] Commit de integración verificada.

### Task 3: SQL y evidencia

**Files:** scripts de prueba en tmp/hexagonal, capturas en docs/capturas; reporte docs/Verificacion.md.
**Interfaces:** Ejecutable target/horizonte-mvc-1.0.0.jar, conexión local existente sin imprimir secretos, rutas conservadas.
- [x] Iniciar ejecutable en 18081 con config local original referenciada externamente; nunca reiniciar SQL ni modificar el servicio MVC abierto.
- [x] Recorrido navegador de crear/leer/editar/eliminar proyectos y entregables, reasignar, bloquear padre, 404 y validación. Identificar registros de prueba y retirar solo esos ids.
- [x] Capturar páginas posteriores a migración, registrar prueba real SQL y cierre del proceso propio.
- [x] Conservar evidencia de Maven y núcleo independiente. Commit.

### Task 4: Documentos y entrega

**Files:** README.md; docs/{Documento-Tecnico-Horizonte-Hexagonal,Manual-Horizonte-Hexagonal}.{docx,pdf}; docs/arquitectura.png; output/unidad2/actividad-hexagonal/*.zip.
**Interfaces:** Documentar exactamente las clases y puertos implementados y resultados reales de tareas 1 a 3.
- [x] Crear diagrama con dirección correcta de dependencias; redactar comparativa con ocho aspectos, siete reflexiones y conclusiones sin afirmar independencia de tecnología de datos sin adaptar infraestructura.
- [x] Redactar manual con JDK21, Maven, SQL, cuentas propias, puertos, tablas, configuración ejemplo, comandos, URLs y capturas nuevas. Incluir selección de rama GitHub correcta.
- [x] Crear Word y PDF, renderizar e inspeccionar todas las páginas, corregir defectos. Mantener los documentos MVC anteriores identificados como tales o fuera de docs de esta rama para no confundir entrega.
- [x] Revisión independiente final de código, esquema, transacciones y requisitos; corregir hallazgos importantes con pruebas.
- [x] Escanear archivos versionados y documentos contra credenciales locales, excluir config/target/Git del ZIP; comprobar contenido del ZIP.
- [x] Commit, push de arquitectura-hexagonal sin fusionar main, verificar HEAD remoto y enlaces. Entregar enlaces de rama, ZIP y documentos.

## Ejecución

El estudiante aprobó el diseño y pidió su implementación. Ejecución directa por el asistente con una revisión independiente final; no se añaden rondas de aprobación para decisiones operativas cubiertas por el diseño.
