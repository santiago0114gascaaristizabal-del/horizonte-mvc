package edu.horizonte.modelo;
import jakarta.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name="entregables")
public class Entregable {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Nationalized @Column(nullable=false,length=120)
    private String titulo;
    @Column(nullable=false)
    private LocalDate fecha;
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="proyecto_id",nullable=false,foreignKey=@ForeignKey(name="fk_entregables_proyecto"))
    private Proyecto proyecto;

    protected Entregable() {}
    public Entregable(String titulo, LocalDate fecha, Proyecto proyecto) { actualizar(titulo, fecha, proyecto); }
    public void actualizar(String titulo, LocalDate fecha, Proyecto proyecto) {
        this.titulo=titulo.strip();
        this.fecha=fecha;
        this.proyecto=proyecto;
    }
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public LocalDate getFecha() { return fecha; }
    public Proyecto getProyecto() { return proyecto; }
}
