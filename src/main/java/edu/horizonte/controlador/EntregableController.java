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
@RequestMapping("/entregables")
public class EntregableController {
    private final SeguimientoService servicio;
    public EntregableController(SeguimientoService servicio) { this.servicio=servicio; }

    @ModelAttribute
    public void comunes(Model m) {
        m.addAttribute("proyectoVista", false);
        m.addAttribute("entidad","Entregable");
        m.addAttribute("plural","Entregables");
        m.addAttribute("ruta","/entregables");
    }
    @GetMapping
    public String listar(Model m) {
        m.addAttribute("filas",servicio.entregables());
        return "lista";
    }
    @GetMapping("/{id}")
    public String consultar(@PathVariable Long id, Model m) {
        m.addAttribute("registro",servicio.entregable(id));

        return "detalle";
    }
    @GetMapping("/nuevo")
    public String nuevo(Model m) {
        m.addAttribute("form", new EntregableForm(null,null,null));
        return formulario(m,null);
    }
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model m) {
        var e=servicio.entregable(id);
        m.addAttribute("form",new EntregableForm(e.getTitulo(),e.getFecha(),e.getProyecto().getId()));
        return formulario(m,id);
    }
    @PostMapping
    public String crear(@Valid @ModelAttribute("form") EntregableForm f, BindingResult errores, Model m, RedirectAttributes aviso) {
        return guardar(null,f,errores,m,aviso);
    }
    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") EntregableForm f, BindingResult errores, Model m, RedirectAttributes aviso) {
        servicio.entregable(id);
        return guardar(id,f,errores,m,aviso);
    }
    private String guardar(Long id, EntregableForm f, BindingResult errores, Model m, RedirectAttributes aviso) {
        if (errores.hasErrors()) return formulario(m,id);
        var guardado=guardarSiExisteProyecto(id,f,errores);
        if (guardado==null) return formulario(m,id);
        aviso.addFlashAttribute("exito",id==null?"Registro creado.":"Cambios guardados.");
        return "redirect:/entregables/"+guardado.getId();
    }
    private edu.horizonte.modelo.Entregable guardarSiExisteProyecto(Long id, EntregableForm f, BindingResult errores) {
        try { return servicio.guardarEntregable(id,f.titulo(),f.fecha(),f.proyectoId()); }
        catch (RegistroNoEncontrado ex) {
            errores.rejectValue("proyectoId","proyecto.inexistente",ex.getMessage());
            return null;
        }
        catch (DataIntegrityViolationException ex) {
            errores.rejectValue("proyectoId","proyecto.relacion","No se pudo guardar la relación. Comprueba el proyecto y vuelve a intentar.");
            return null;
        }
    }
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes aviso) {
        servicio.eliminarEntregable(id);
        aviso.addFlashAttribute("exito","Registro eliminado.");
        return "redirect:/entregables";
    }
    private String formulario(Model m,Long id) {
        m.addAttribute("accion", id==null?"/entregables":"/entregables/"+id);
        m.addAttribute("editar",id!=null);
        m.addAttribute("proyectos",servicio.proyectos());
        return "formulario";
    }
}
