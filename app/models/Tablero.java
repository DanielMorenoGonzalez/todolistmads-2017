package models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Tablero {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nombre;
    @ManyToOne
    @JoinColumn(name = "administradorId")
    private Usuario administrador;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Persona_Tablero")
    private Set<Usuario> participantes = new HashSet<>();
    // Relación uno-a-muchos entre tablero y tareas
    @OneToMany(mappedBy = "tablero", fetch = FetchType.EAGER)
    private Set<Tarea> tareas = new HashSet<>();
    private Boolean privado = false; // Por defecto los tableros serán públicos
    // Relación uno-a-muchos entre tablero y etiquetas
    @OneToMany(mappedBy = "tablero", fetch = FetchType.EAGER)
    private Set<Etiqueta> etiquetas = new HashSet<>();

    public Tablero() {
    }

    public Tablero(Usuario administrador, String nombre, boolean privado) {
        this.nombre = nombre;
        this.administrador = administrador;
        this.privado = privado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Usuario getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Usuario usuario) {
        this.administrador = administrador;
    }

    public Set<Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(Set<Usuario> participantes) {
        this.participantes = participantes;
    }

    public Set<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(Set<Tarea> tareas) {
        this.tareas = tareas;
    }

    public Boolean isPrivado() {
        return privado;
    }

    public void setPrivado(Boolean privado) {
        this.privado = privado;
    }

    public Set<Etiqueta> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(Set<Etiqueta> etiquetas) {
        this.etiquetas = etiquetas;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + ((nombre == null) ? 0 : nombre.hashCode());
        result = result + ((administrador == null) ? 0 : administrador.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tablero)) return false;
        Tablero tablero = (Tablero) o;
        // Si tenemos los ID, comparamos por ID
        if (id != null && tablero.id != null) return Objects.equals(id, tablero.id);
        // Si no, comparamos por campos obligatorios
        return Objects.equals(nombre, tablero.nombre) &&
                Objects.equals(administrador, tablero.administrador) &&
                Objects.equals(participantes, tablero.participantes);
    }
}
