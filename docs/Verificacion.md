# Verificación de Horizonte hexagonal

Fecha: 20 de noviembre de 2026. Comparación con MVC en main, commit 897dfbe.

## Pruebas automatizadas

Maven package y copia limpia de Git: BUILD SUCCESS; 12 pruebas, 0 fallos, 0 errores, 0 omitidas.

| Grupo | Total | Evidencia |
| --- | --- | --- |
| SeguimientoServiceTest | 3 | Núcleo sin Spring/SQL, guardado explícito, reasignación, inexistentes. |
| FlujoMvcTest | 3 | Ambos CRUD, relación, validación, eliminación y 404 con H2. |
| ArquitecturaTest | 2 | Núcleo compilado solo con JDK; dependencias mediante contratos. |
| InfraestructuraTest | 4 | Integridad traducida, lectura de solo lectura y rollback. |

## SQL Express y navegador

Ejecutable iniciado en 127.0.0.1:18081 con la conexión existente a HorizonteMVC, puerto SQL 14333. El arranque confirmó base versión 16.0 y validación del esquema.

Chrome verificó crear y editar proyectos, crear/consultar/editar entregable, reasignarlo, comprobar la relación, bloquear el padre con hijos, validar nombre en blanco, eliminar y comprobar 404. Se recargó el detalle editado para comprobar la consulta persistida.

Se retiraron únicamente los ids creados por el recorrido. Los conjuntos de ids anteriores y posteriores coincidieron. No se vaciaron tablas ni se cambió el esquema ni el servicio SQL. Las capturas son de la versión hexagonal posterior a la migración.

## Revisión de portabilidad

La revisión independiente detectó que config/ excluía también las clases Java de infraestructura. El fallo se reprodujo en una exportación limpia, se limitó el patrón a /config/, se incorporaron las clases y se repitieron con éxito compilación y 12 pruebas sobre otra exportación.

La configuración privada, target y metadatos Git se excluyen del paquete de fuente. main conserva la entrega previa.
