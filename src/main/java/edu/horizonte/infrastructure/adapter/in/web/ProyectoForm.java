package edu.horizonte.infrastructure.adapter.in.web;
import jakarta.validation.constraints.*;
public record ProyectoForm(
    @NotBlank(message="Escribe un nombre.") @Size(max=120,message="Máximo 120 caracteres.") String nombre,
    @NotBlank(message="Escribe una descripción.") @Size(max=500,message="Máximo 500 caracteres.") String descripcion
) {}
