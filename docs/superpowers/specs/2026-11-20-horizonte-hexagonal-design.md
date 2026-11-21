# Diseño de migración de Horizonte a arquitectura hexagonal

Estado: propuesta para revisión del estudiante; pendiente de aprobación antes de implementar.
Revisión independiente de planificación: completada, sin preguntas importantes pendientes.
Autor del proyecto: Santiago Gasca Aristizabal.
Rama elegida por el estudiante: `arquitectura-hexagonal` en el repositorio existente.
Punto de partida MVC: commit `897dfbe463e80e2bc376b16ac13c1a4f120160c8` de `main`.

## 1. Propósito y requisitos

Transformar el proyecto académico Horizonte ya entregado, conservando los CRUD de Proyecto y Entregable, su relación uno a muchos, la interfaz Thymeleaf y la persistencia en SQL Server Express. La finalidad es demostrar separación de responsabilidades, inversión de dependencias, bajo acoplamiento y alta cohesión mediante cambios verificables de código.

Fuentes de requisitos: `Actividad_Final_Arquitectura_Software_MVC_a_Hexagonal (1).pdf` y `Guia_Migracion_MovilidadU_Arquitectura_Hexagonal.pdf`, suministrados en Downloads. La primera define la entrega; la segunda es una referencia de implementación de otro proyecto. Los nombres, tecnologías y reglas específicas de MovilidadU no se convierten en requisitos de Horizonte.

La modalidad de la nueva guía indica equipos. Se mantiene a Santiago como autor individual, conforme al trabajo anterior; no se inventan integrantes. La aceptación de esta modalidad corresponde a la profesora.

## 2. Alternativas y decisión

- Misma cuenta y repositorio, nueva rama `arquitectura-hexagonal`: permite comparar el resultado con `main`, que conserva la entrega MVC. Es la opción seleccionada por el estudiante.
- Repositorio independiente derivado del MVC: separa los enlaces de entrega, pero fragmenta el historial. No fue seleccionado.

Decisiones técnicas propuestas por el asistente: mantener Java 21, Spring Boot 3.5.16, Maven, Thymeleaf y SQL Server Express; usar un núcleo Java independiente de Spring/JPA y configurar su integración desde infraestructura. No se propone actualizar versiones durante esta refactorización.

## 3. Alcance y límites

Se conservan los campos existentes: Proyecto(id, nombre, descripcion) y Entregable(id, titulo, fecha, proyecto). Se conservan los nombres de tablas y columnas, las longitudes, NVARCHAR, los identificadores autogenerados y la clave foránea sin borrado en cascada. No se migra ni elimina información de usuario.

Se mantienen las rutas `/`, `/proyectos`, `/entregables` y las operaciones de nuevo, detalle, edición y eliminación. Se mantienen las vistas, los mensajes principales y las validaciones de formularios. El objetivo es reorganizar el interior sin obligar al usuario a aprender otro flujo.

No se agregan entidades, autenticación, roles, cargas de archivos, API REST, microservicios ni otro motor de base de datos. MVC continúa siendo el mecanismo del adaptador web; la arquitectura hexagonal cambia las dependencias del sistema, no elimina el uso de Spring MVC para servir páginas.

## 4. Organización propuesta

```text
edu.horizonte
  HorizonteApplication
  domain
    model                  Proyecto, Entregable (Java sin anotaciones JPA)
    exception              errores de negocio y de registros inexistentes
    port
      in                   SeguimientoUseCase
      out                  ProyectoRepositoryPort, EntregableRepositoryPort
  application
    service                SeguimientoService (Java sin Spring)
  infrastructure
    adapter
      in
        web                controladores, formularios y manejo de errores web
      out
        persistence        entidades JPA, repositorios Spring Data, mapeadores,
                           adaptadores que implementan los puertos de salida
    config                 ensamblaje de beans y límite transaccional
```

La ubicación de los puertos en domain sigue la estructura de referencia de la actividad. La guía MovilidadU los sitúa en application; ambas posiciones cumplen la regla de dependencia hacia el núcleo. En Horizonte se documentará y aplicará una sola convención.

## 5. Contratos y dependencias

`SeguimientoUseCase` expondrá las nueve operaciones del servicio actual: listar proyectos, listar entregables, listar entregables de un proyecto, consultar proyecto, consultar entregable, guardar proyecto, guardar entregable, eliminar proyecto y eliminar entregable. Guardar acepta un identificador opcional: ausente para crear y presente para actualizar.

Los puertos de salida expresarán las consultas y escrituras necesarias usando tipos del dominio y de Java: List, Optional, Long y LocalDate. No extenderán JpaRepository ni devolverán entidades JPA, Page o tipos HTTP.

`SeguimientoService` implementará el puerto de entrada y recibirá los dos puertos de salida por constructor. Consultará la existencia de los registros antes de actualizar o eliminar y verificará la existencia del proyecto antes de registrar o reasignar un entregable. Bloqueará el borrado de proyectos con entregables.

Los controladores dependerán del puerto de entrada. Los repositorios Spring Data solo serán utilizados por adaptadores de persistencia. Los controladores recibirán modelos de dominio ya materializados, sin proxies JPA.

Dependencias de código:

```text
Web -> puerto de entrada <- servicio de aplicación
Servicio de aplicación -> puertos de salida <- adaptadores JPA
Adaptadores JPA -> repositorios Spring Data -> SQL Server Express
Dominio: no depende de ninguna tecnología externa
Configuración: conoce las implementaciones y ensambla el sistema
```

El diagrama técnico final distinguirá llamadas de ejecución e implementación de interfaces para no confundir el flujo de datos con la dirección de las dependencias.

## 6. Dominio y persistencia

Se separarán `Proyecto` y `Entregable` de `ProyectoJpaEntity` y `EntregableJpaEntity`. El dominio conservará su relación explícita: el entregable referencia un proyecto del dominio. Las entidades JPA usarán la misma relación y esquema del MVC.

Los mapeadores convertirán explícitamente entre los dos modelos. El mapeo del entregable materializará también su proyecto dentro de la transacción, evitando LazyInitializationException con open-in-view deshabilitado. No se añadirán colecciones bidireccionales que produzcan ciclos innecesarios.

Actualizar deberá guardar explícitamente mediante el puerto: el dominio ya no dependerá de la detección automática de cambios de una entidad administrada. Crear conservará la generación de id en la base de datos. El diseño evitará convertir un id inexistente en una inserción accidental.

## 7. Transacciones y errores

El núcleo quedará libre de `@Service`, `@Transactional` y clases de Spring. Infraestructura registrará el caso de uso mediante `@Bean` y un decorador transaccional del puerto de entrada, con TransactionTemplate. Las consultas tendrán transacciones de lectura y las escrituras transacciones completas que abarquen comprobaciones y guardado.

Cada llamada al puerto de entrada es un límite de transacción. Las operaciones del servicio interno no atravesarán de nuevo el decorador. La configuración dejará un solo bean público del puerto para evitar ambigüedades de inyección.

La comprobación de hijos en aplicación expresa la regla; la clave foránea de SQL protege frente a cambios concurrentes. El adaptador traducirá los fallos de integridad pertinentes a errores propios del núcleo. La traducción deberá suceder dentro del alcance en que se ejecuta el flush, evitando depender de una excepción que aparezca solo al cerrar la transacción. Se conservan los mensajes útiles para el usuario; los errores técnicos no se expondrán como detalle interno.

## 8. Base de datos y ejecución

Se conservarán `HorizonteMVC`, el puerto SQL local 14333 y `ddl-auto=validate`. Los scripts existentes seguirán sirviendo para una instalación nueva; no se ejecutarán scripts destructivos ni se recrearán las tablas existentes.

Las credenciales permanecerán exclusivamente en la configuración local ignorada por Git o en variables de entorno. No se publicarán contraseñas ni se crearán nuevas cuentas SQL. Se usará la conexión local ya configurada, sin mostrar su contenido.

El comando del ejecutable final se verificará después de compilar. Se conservará el nombre de artefacto actual para reducir cambios; el manual distinguirá la rama y el directorio de trabajo. La aplicación escucha en 127.0.0.1:8080; para verificarla junto a una instancia MVC abierta se usará temporalmente un puerto libre, por ejemplo 18081.

## 9. Verificación y criterios de aceptación

1. Conservar y ejecutar las pruebas MockMvc de los dos CRUD, formularios inválidos, relación, bloqueo de borrado y respuestas 404.
2. Añadir pruebas unitarias del caso de uso con adaptadores en memoria escritos para las pruebas: sin Spring ni SQL. Cubrir actualización, persistencia explícita, reasignación, registros inexistentes y bloqueo de borrado con hijos.
3. Comprobar que domain y application no importan Spring, JPA, Hibernate ni infraestructura, y que web no usa repositorios concretos o entidades JPA. La comprobación automatizada será significativa y no dependerá solo de los nombres de carpetas.
4. Ejecutar el flujo real de crear, consultar, editar y eliminar ambas entidades con SQL Express. Usar exclusivamente registros creados para la comprobación, identificar sus ids y retirar únicamente esos registros; no borrar filas ajenas. No reiniciar ni reconfigurar SQL para esta migración.
5. Capturar pantallas del resultado migrado y registrar la evidencia de compilación, pruebas y conexión. No reutilizar capturas MVC como si fueran evidencia posterior.
6. Confirmar que config, target, credenciales y datos locales quedan fuera de Git y del ZIP. Verificar la correspondencia entre rama, ZIP y documentación.

Las pruebas automatizadas usarán H2 en memoria con configuración explícita de classpath para impedir que el archivo local SQL reemplace accidentalmente su conexión.

## 10. Entregables y publicación

- Fuente completa en la rama `arquitectura-hexagonal`, manteniendo `main` como versión MVC.
- Documento técnico en Word y PDF: contexto, diagnóstico MVC, paquetes reales, componentes implementados, diagrama, comparación, las siete reflexiones respondidas y conclusiones específicas de Horizonte.
- Manual del sistema en Word y PDF: requisitos, base y puertos, configuración sin secretos, compilación, ejecución, módulos, URLs, funcionalidades y capturas verificadas.
- ZIP limpio de la rama con fuentes, scripts y documentos. Los archivos finales se organizarán en `output/unidad2/actividad-hexagonal` y los documentos de entrega se incorporarán a docs en la rama.

La publicación de esta rama continuará en el repositorio ya elegido para las entregas. No se reemplazará ni se fusionará sobre main durante esta actividad. Se proporcionará el enlace de la rama para evitar entregar por error la versión MVC.

Para trabajar se podrá utilizar un directorio separado ligado al mismo repositorio (worktree) y a esta rama, conservando disponible el directorio de la versión MVC. Es una decisión operativa del asistente; no crea otra aplicación ni otro repositorio. La ubicación concreta deberá permanecer dentro del espacio permitido o contar con el acceso correspondiente.

## 11. Decisiones menores a cargo de implementación

El implementador puede decidir los nombres exactos de mapeadores, decorador y errores, descomponer archivos de prueba y ajustar el formato de los documentos sin alterar el alcance. El resultado debe ser legible para que Santiago pueda explicarlo y demostrarlo. Se documentarán limitaciones del prototipo sin atribuirle autenticación ni funciones no implementadas.
