package models;

import org.apache.commons.lang3.time.DateUtils;
import play.data.format.Formats;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Etiqueta {
    // Relación muchos-a-uno entre etiquetas y usuario
    @ManyToOne
    // Nombre de la columna en la BD que guarda físicamente
    // el ID del usuario con el que está asociado una etiqueta
    @JoinColumn(name = "usuarioId")
    public Usuario usuario;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String titulo;
    private String color;
    @Formats.DateTime(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date fechaCreacion = getCurrentDate();
    // Relación uno-a-muchos entre etiquetas y tareas
    @OneToMany(mappedBy = "etiqueta", fetch = FetchType.EAGER)
    private Set<Tarea> tareas = new HashSet<>();
    // Relación muchos-a-uno entre etiquetas y usuario
    @ManyToOne
    @JoinColumn(name = "tableroId")
    private Tablero tablero;

    public Etiqueta(Usuario usuario, String titulo, String color) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.color = color;
    }

    public Etiqueta() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    private static Date getCurrentDate() {
        return DateUtils.truncate(new Date(), Calendar.MINUTE);
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Set<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(Set<Tarea> tareas) {
        this.tareas = tareas;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((titulo == null) ? 0 : titulo.hashCode());
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + ((fechaCreacion == null) ? 0 : fechaCreacion.hashCode());
        result = prime * result + ((getUsuario() == null) ? 0 : getUsuario().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Etiqueta)) return false;
        Etiqueta etiqueta = (Etiqueta) o;
        // Si tenemos los ID, comparamos por ID
        if (id != null && etiqueta.id != null) return Objects.equals(id, etiqueta.id);
        // Si no, comparamos por campos obligatorios
        return Objects.equals(titulo, etiqueta.titulo) &&
                Objects.equals(color, etiqueta.color) &&
                Objects.equals(fechaCreacion, etiqueta.fechaCreacion) &&
                Objects.equals(usuario, etiqueta.usuario);
    }
}
