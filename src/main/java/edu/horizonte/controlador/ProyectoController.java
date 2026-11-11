package edu.horizonte.controlador;
import edu.horizonte.servicio.*;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proyectos")
public class ProyectoController {
    private final SeguimientoService servicio;
    public ProyectoController(SeguimientoService servicio) { this.servicio=servicio; }

    @ModelAttribute
    public void comunes(Model m) {
        m.addAttribute("proyectoVista", true);
        m.addAttribute("entidad","Proyecto");
        m.addAttribute("plural","Proyectos");
        m.addAttribute("ruta","/proyectos");
    }
    @GetMapping
    public String listar(Model m) {
        m.addAttribute("filas",servicio.proyectos());
        return "lista";
    }
    @GetMapping("/{id}")
    public String consultar(@PathVariable Long id, Model m) {
        m.addAttribute("registro",servicio.proyecto(id));
        m.addAttribute("relacionados",servicio.entregablesDe(id));
        return "detalle";
    }
    @GetMapping("/nuevo")
    public String nuevo(Model m) {
        m.addAttribute("form", new ProyectoForm(null,null));
        return formulario(m,null);
    }
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model m) {
        var e=servicio.proyecto(id);
        m.addAttribute("form",new ProyectoForm(e.getNombre(),e.getDescripcion()));
        return formulario(m,id);
    }
    @PostMapping
    public String crear(@Valid @ModelAttribute("form") ProyectoForm f, BindingResult errores, Model m, RedirectAttributes aviso) {
        return guardar(null,f,errores,m,aviso);
    }
    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") ProyectoForm f, BindingResult errores, Model m, RedirectAttributes aviso) {
        servicio.proyecto(id);
        return guardar(id,f,errores,m,aviso);
    }
    private String guardar(Long id, ProyectoForm f, BindingResult errores, Model m, RedirectAttributes aviso) {
        if (errores.hasErrors()) return formulario(m,id);
        var guardado=servicio.guardarProyecto(id,f.nombre(),f.descripcion());
        aviso.addFlashAttribute("exito",id==null?"Registro creado.":"Cambios guardados.");
        return "redirect:/proyectos/"+guardado.getId();
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes aviso) {
        try { servicio.eliminarProyecto(id); }
        catch (ProyectoConEntregables | DataIntegrityViolationException ex) {
            aviso.addFlashAttribute("error","Elimina o reasigna los entregables antes de borrar el proyecto.");
            return "redirect:/proyectos/"+id;
        }
        aviso.addFlashAttribute("exito","Registro eliminado.");
        return "redirect:/proyectos";
    }
    private String formulario(Model m,Long id) {
        m.addAttribute("accion", id==null?"/proyectos":"/proyectos/"+id);
        m.addAttribute("editar",id!=null);

        return "formulario";
    }
}
