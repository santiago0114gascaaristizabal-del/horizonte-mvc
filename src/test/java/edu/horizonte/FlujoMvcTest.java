package edu.horizonte;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Evita que config/application.properties reemplace la base aislada de pruebas.
@SpringBootTest(classes=HorizonteApplication.class,
 properties="spring.config.location=classpath:/application.properties")
@AutoConfigureMockMvc
class FlujoMvcTest {
 @Autowired MockMvc mvc;
 @Autowired JdbcTemplate sql;

 @Test void crudCompletoConRelacion() throws Exception {
  mvc.perform(get("/")).andExpect(status().isOk());
  mvc.perform(get("/proyectos/nuevo")).andExpect(status().isOk());
  mvc.perform(post("/proyectos").param("nombre","Proyecto de prueba").param("descripcion","Seguimiento académico"))
     .andExpect(status().is3xxRedirection());
  long p=sql.queryForObject("select id from proyectos where nombre='Proyecto de prueba'",Long.class);
  mvc.perform(get("/proyectos")).andExpect(content().string(containsString("Proyecto de prueba")));
  mvc.perform(get("/proyectos/"+p)).andExpect(status().isOk());
  mvc.perform(get("/proyectos/"+p+"/editar")).andExpect(status().isOk());
  mvc.perform(post("/proyectos/"+p).param("nombre","Proyecto actualizado").param("descripcion","Nueva descripción"))
     .andExpect(status().is3xxRedirection());
  assertEquals("Proyecto actualizado",sql.queryForObject("select nombre from proyectos where id=?",String.class,p));
  mvc.perform(get("/entregables/nuevo")).andExpect(content().string(containsString("Proyecto actualizado")));
  mvc.perform(post("/entregables").param("titulo","Informe inicial").param("fecha","2026-11-30").param("proyectoId",""+p))
     .andExpect(status().is3xxRedirection());
  long e=sql.queryForObject("select id from entregables where titulo='Informe inicial'",Long.class);
  assertEquals(p,sql.queryForObject("select proyecto_id from entregables where id=?",Long.class,e));
  mvc.perform(get("/entregables")).andExpect(content().string(containsString("Informe inicial")));
  mvc.perform(get("/entregables/"+e)).andExpect(content().string(containsString("Proyecto actualizado")));
  mvc.perform(get("/entregables/"+e+"/editar")).andExpect(status().isOk());
  mvc.perform(post("/entregables/"+e).param("titulo","Informe final").param("fecha","2026-12-01").param("proyectoId",""+p))
     .andExpect(status().is3xxRedirection());
  assertEquals("Informe final",sql.queryForObject("select titulo from entregables where id=?",String.class,e));
  mvc.perform(post("/proyectos/"+p+"/eliminar")).andExpect(flash().attributeExists("error"));
  assertEquals(1,sql.queryForObject("select count(*) from proyectos where id=?",Integer.class,p));
  mvc.perform(post("/entregables/"+e+"/eliminar")).andExpect(status().is3xxRedirection());
  mvc.perform(post("/proyectos/"+p+"/eliminar")).andExpect(status().is3xxRedirection());
  mvc.perform(get("/entregables/"+e)).andExpect(status().isNotFound());
  mvc.perform(get("/proyectos/"+p)).andExpect(status().isNotFound());
 }

 @Test void rechazaDatosInvalidos() throws Exception {
  mvc.perform(post("/proyectos").param("nombre"," ").param("descripcion",""))
     .andExpect(status().isOk()).andExpect(content().string(containsString("Escribe un nombre")));
  mvc.perform(post("/entregables").param("titulo","No guardar").param("fecha","2026-11-30").param("proyectoId","99999999"))
     .andExpect(status().isOk()).andExpect(content().string(containsString("El proyecto seleccionado no existe")));
  assertEquals(0,sql.queryForObject("select count(*) from entregables where titulo='No guardar'",Integer.class));
 }
}
