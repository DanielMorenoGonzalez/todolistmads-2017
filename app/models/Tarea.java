package models;

import org.apache.commons.lang3.time.DateUtils;
import play.data.format.Formats;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Entity
public class Tarea {
    // Relación muchos-a-uno entre tareas y usuario
    @ManyToOne
    // Nombre de la columna en la BD que guarda físicamente
    // el ID del usuario con el que está asociado una tarea
    @JoinColumn(name = "usuarioId")
    public Usuario usuario;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String titulo;
    private String cuerpo;
    private Boolean terminada = false;
    @Formats.DateTime(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date fechaCreacion = getCurrentDate();
    @Formats.DateTime(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;
    @Formats.DateTime(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaLimite;
    @ManyToOne
    @JoinColumn(name = "tableroId")
    private Tablero tablero;
    @ManyToOne
    @JoinColumn(name = "etiquetaId")
    private Etiqueta etiqueta;

    public Tarea(Usuario usuario, String titulo, String cuerpo, Date fechaInicio, Date fechaLimite) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        setFechaInicio(fechaInicio);
        setFechaLimite(fechaLimite);
    }

    public Tarea() {
    }

    private static Date getCurrentDate() {
        return DateUtils.truncate(new Date(), Calendar.MINUTE);
    }

    // Getters y setters necesarios para JPA

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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(final Date fechaInicio) {
        this.fechaInicio = fechaInicio != null ? DateUtils.truncate(fechaInicio, Calendar.MINUTE) : null;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite != null ? DateUtils.truncate(fechaLimite, Calendar.MINUTE) : null;
    }

    public Boolean isTerminada() {
        return terminada;
    }

    public void setTerminada(Boolean terminada) {
        this.terminada = terminada;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    public Etiqueta getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(Etiqueta etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return String.format("Tarea id: %s titulo: %s cuerpo: %s usuario: %s",
            id, titulo, cuerpo, usuario.toString());
    }

    @Override
    public int hashCode() {
        int result = getUsuario() != null ? getUsuario().hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (titulo != null ? titulo.hashCode() : 0);
        result = 31 * result + (cuerpo != null ? cuerpo.hashCode() : 0);
        result = 31 * result + (terminada != null ? terminada.hashCode() : 0);
        result = 31 * result + (fechaCreacion != null ? fechaCreacion.hashCode() : 0);
        result = 31 * result + (fechaInicio != null ? fechaInicio.hashCode() : 0);
        result = 31 * result + (fechaLimite != null ? fechaLimite.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tarea)) return false;
        Tarea tarea = (Tarea) o;
        // Si tenemos los ID, comparamos por ID
        if (id != null && tarea.id != null) return (Objects.equals(id, tarea.id));
        // Si no, comparamos por campos obligatorios
        return Objects.equals(usuario, tarea.usuario) &&
            Objects.equals(titulo, tarea.titulo) &&
            Objects.equals(cuerpo, tarea.cuerpo) &&
            Objects.equals(terminada, tarea.terminada) &&
            Objects.equals(fechaCreacion, tarea.fechaCreacion) &&
            Objects.equals(fechaInicio, tarea.fechaInicio) &&
            Objects.equals(fechaLimite, tarea.fechaLimite);
    }
}
