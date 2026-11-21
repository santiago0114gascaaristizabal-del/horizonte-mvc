package edu.horizonte.infrastructure.adapter.out.persistence;
import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name="proyectos")
public class ProyectoJpaEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Nationalized @Column(nullable=false,length=120)
    private String nombre;
    @Nationalized @Column(nullable=false,length=500)
    private String descripcion;

    protected ProyectoJpaEntity() {}
    public ProyectoJpaEntity(String nombre, String descripcion) { actualizar(nombre, descripcion); }
    public void actualizar(String nombre, String descripcion) {
        this.nombre=nombre.strip();
        this.descripcion=descripcion.strip();
    }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}
