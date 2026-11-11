package edu.horizonte.controlador;
import edu.horizonte.servicio.RegistroNoEncontrado;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpStatus;

@ControllerAdvice
public class ErrorControllerMvc {
    @ExceptionHandler(RegistroNoEncontrado.class)
    public ModelAndView noEncontrado(RegistroNoEncontrado ex) {
        return new ModelAndView("no-encontrado", java.util.Map.of("mensaje",ex.getMessage()),HttpStatus.NOT_FOUND);
    }
}
