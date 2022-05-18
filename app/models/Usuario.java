package models;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import play.data.format.*;

@Entity
public class Usuario {
    @Id
    @GeneratedValue
    private Long id;
    private String login;
    private String email;
    private String password;
    private String nombre;
    private String apellidos;
    @Formats.DateTime(pattern="dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    private VistaCalendario vistaCalendario;
    // Relación uno-a-muchos entre usuario y tarea
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    private Set<Tarea> tareas = new HashSet<>();
    // Relación uno-a-muchos entre usuario y etiqueta
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    private Set<Etiqueta> etiquetas = new HashSet<>();
    @OneToMany(mappedBy = "administrador", fetch = FetchType.EAGER)
    private Set<Tablero> administrados = new HashSet<>();
    @ManyToMany(mappedBy = "participantes", fetch = FetchType.EAGER)
    private Set<Tablero> tableros = new HashSet<>();

    // Un constructor vacío necesario para JPA
    public Usuario() {
    }

    // El constructor principal con los campos obligatorios
    public Usuario(String login, String email) {
        this.login = login;
        this.email = email;
    }

    // Getters y setters necesarios para JPA

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Set<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(Set<Tarea> tareas) {
        this.tareas = tareas;
    }

    public Set<Etiqueta> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(Set<Etiqueta> etiquetas) {
        this.etiquetas = etiquetas;
    }

    public Set<Tablero> getAdministrados() {
        return administrados;
    }

    public void setAdministrados(Set<Tablero> administrados) {
        this.administrados = administrados;
    }

    public Set<Tablero> getTableros() {
        return tableros;
    }

    public void setTableros(Set<Tablero> tableros) {
        this.tableros = tableros;
    }

    public VistaCalendario getVistaCalendario() {
        return vistaCalendario;
    }

    public void setVistaCalendario(final VistaCalendario vistaCalendario) {
        this.vistaCalendario = vistaCalendario;
    }

    public String toString() {
        String fechaStr = null;
        if (fechaNacimiento != null) {
            SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
            fechaStr = formateador.format(fechaNacimiento);
        }
        return String.format("Usuario id: %s login: %s password: %s nombre: %s " +
                        "apellidos: %s e-mail: %s fechaNacimiento: %s",
                id, login, password, nombre, apellidos, email, fechaNacimiento);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + ((login == null) ? 0 : login.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        // Si tenemos los ID, comparamos por ID
        if (id != null && usuario.id != null) return Objects.equals(id, usuario.id);
        // Si no, comparamos por campos obligatorios
        return Objects.equals(login, usuario.login) &&
                Objects.equals(email, usuario.email) &&
                Objects.equals(password, usuario.password) &&
                Objects.equals(nombre, usuario.nombre) &&
                Objects.equals(apellidos, usuario.apellidos) &&
                Objects.equals(fechaNacimiento, usuario.fechaNacimiento) &&
                Objects.equals(vistaCalendario, usuario.vistaCalendario) &&
                Objects.equals(tareas, usuario.tareas) &&
                Objects.equals(etiquetas, usuario.etiquetas);
    }
}
