package edu.horizonte.controlador;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
public record EntregableForm(
    @NotBlank(message="Escribe un título.") @Size(max=120,message="Máximo 120 caracteres.") String titulo,
    @NotNull(message="Selecciona una fecha.") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fecha,
    @NotNull(message="Selecciona un proyecto.") @Positive(message="Selecciona un proyecto válido.") Long proyectoId
) {}
