package services;

import models.Tablero;
import models.TableroRepository;
import models.Usuario;
import models.UsuarioRepository;
import models.Tarea;
import models.TareaRepository;
import models.Etiqueta;
import models.EtiquetaRepository;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

public class TableroService {

    static private final String USUARIO_NO_EXISTENTE = "Usuario no existente";

    private final TableroRepository tableroRepository;
    private final UsuarioRepository usuarioRepository;
    private final TareaRepository tareaRepository;
    private final EtiquetaRepository etiquetaRepository;

    @Inject
    @SuppressWarnings("CdiInjectionPointsInspection")
    public TableroService(@NotNull final TableroRepository tableroRepository, @NotNull final UsuarioRepository
            usuarioRepository, @NotNull final TareaRepository tareaRepository, @NotNull final EtiquetaRepository
            etiquetaRepository) {
        this.tableroRepository = tableroRepository;
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;
        this.etiquetaRepository = etiquetaRepository;
    }

    public Tablero crearTablero(@NotNull final long idUsuario, @NotNull final String nombreTablero, final boolean privado) {
        final Usuario usuario = findUsuarioByIdOrThrow(idUsuario);
        if(nombreTablero.isEmpty()) throw new TableroServiceExcepion("Nombre de tablero nulo");
        if(tableroExisteEnAdministradosPorUsuario(idUsuario, nombreTablero)) throw new TableroServiceExcepion("Tablero ya existente");
        final Tablero tablero = new Tablero(usuario, nombreTablero, privado);
        return tableroRepository.add(tablero);
    }

    public List<Tablero> allTablerosAdministradosUsuario(@NotNull final long idUsuario) {
        Usuario usuario = findUsuarioByIdOrThrow(idUsuario);
        final ArrayList<Tablero> tablerosAdministrados = new ArrayList<>(usuario.getAdministrados());
        tablerosAdministrados.sort(Comparator.comparingLong(Tablero::getId));
        return tablerosAdministrados;
    }

    public List<Tablero> allTablerosParticipaUsuario(@NotNull long idUsuario) {
        final Usuario usuario = findUsuarioByIdOrThrow(idUsuario);
        final ArrayList<Tablero> tablerosParticipa = new ArrayList<>(usuario.getTableros());
        tablerosParticipa.sort(Comparator.comparingLong(Tablero::getId));
        return tablerosParticipa;
    }

    public List<Tablero> restoTablerosUsuario(long idUsuario, boolean incluirPrivados) {
        //boolean incluirPrivados = false; // TODO esto hay que cambiarlo para que sí incluya los privados si son del usuario
        findUsuarioByIdOrThrow(idUsuario);
        return tableroRepository.findRestByUserId(idUsuario,incluirPrivados);
    }

    public Tablero apuntarUsuarioATablero(@NotNull long idUsuario, @NotNull long idTablero) {
        final Usuario usuario = findUsuarioByIdOrThrow(idUsuario);
        final Tablero tablero = findTableroByIdOrThrow(idTablero);
        // ToDo comprobar que admin no se pueda apuntar
        if (tablero.getParticipantes().contains(usuario)) throw new TableroServiceExcepion("Usuario ya apuntado");
        tablero.getParticipantes().add(usuario);
        return tableroRepository.update(tablero);
    }

    // desapuntarUsuarioDeTablero - No se deberá usar para que usuarios que no sean el Administrador borren usuarios de un tablero
    public Tablero desapuntarUsuarioDeTablero(@NotNull long idUsuario, @NotNull long idTablero) {
        final Usuario usuario = findUsuarioByIdOrThrow(idUsuario);
        final Tablero tablero = findTableroByIdOrThrow(idTablero);

        // Excepciones posibles: Usuario no existente
        if (!tablero.getParticipantes().contains(usuario)) throw new TableroServiceExcepion("El usuario no estaba apuntado al tablero");
        tablero.getParticipantes().remove(usuario);
        return tableroRepository.update(tablero);
    }

    public Tablero addTareaATablero(@NotNull final long idTablero, @NotNull final long idTarea) {
        final Tablero tablero = findTableroByIdOrThrow(idTablero);
        final Tarea tarea = findTareaByIdOrThrow(idTarea);

        tablero.getTareas().add(tarea);
        tarea.setTablero(tablero);
        tareaRepository.update(tarea);
        return tablero;
    }

    public Tablero addEtiquetaATablero(@NotNull final long idTablero, @NotNull final long idEtiqueta) {
        final Tablero tablero = findTableroByIdOrThrow(idTablero);
        final Etiqueta etiqueta = findEtiquetaByIdOrThrow(idEtiqueta);

        tablero.getEtiquetas().add(etiqueta);
        etiqueta.setTablero(tablero);
        etiquetaRepository.update(etiqueta);
        return tablero;
    }

    public Tablero obtenerTablero(Long idTablero) {
        return tableroRepository.findById(idTablero);
    }

    public Tablero findTableroPorNombre(@NotNull final String nombreTablero) {
        return tableroRepository.findByName(nombreTablero);
    }

    public boolean tableroExisteEnAdministradosPorUsuario(@NotNull long idUsuario, @NotNull String nombreTablero) {
        final Usuario usuario = findUsuarioByIdOrThrow(idUsuario);
        final ArrayList<Tablero> tablerosAdministrados = new ArrayList<>(usuario.getAdministrados());
        for(Tablero tablero : tablerosAdministrados) {
            if(tablero.getNombre().equals(nombreTablero)) {
                return true;
            }
        }
        return false;
    }

    public Tablero findTableroByIdOrThrow(@NotNull final long idTablero) {
        final Tablero tablero = tableroRepository.findById(idTablero);
        if (tablero == null) throw new TableroServiceExcepion("Tablero no existente");
        return tablero;
    }

    private Usuario findUsuarioByIdOrThrow(@NotNull final long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario);
        if (usuario == null) throw new TableroServiceExcepion("Usuario no existente");
        return usuario;
    }

    private Tarea findTareaByIdOrThrow(@NotNull final long idTarea) {
        Tarea tarea = tareaRepository.findById(idTarea);
        if (tarea == null) throw new TableroServiceExcepion("Tarea no existente");
        return tarea;
    }

    private Etiqueta findEtiquetaByIdOrThrow(@NotNull final long idEtiqueta) {
        Etiqueta etiqueta = etiquetaRepository.findById(idEtiqueta);
        if (etiqueta == null) throw new TableroServiceExcepion("Etiqueta no existente");
        return etiqueta;
    }

    public Tablero hacerTableroPrivado(Long idTablero) {
        final Tablero tablero = findTableroByIdOrThrow(idTablero);
        if (tablero.isPrivado()) throw new TableroServiceExcepion("El tablero ya es privado");
        tablero.setPrivado(true);
        return tableroRepository.update(tablero);
    }

    public Tablero hacerTableroPublico(Long idTablero) {
        final Tablero tablero = findTableroByIdOrThrow(idTablero);
        if (!tablero.isPrivado()) throw new TableroServiceExcepion("El tablero ya es público");
        tablero.setPrivado(false);
        return tableroRepository.update(tablero);
    }

    public Tablero addParticipanteTablero(Long idTablero, Long idUsuario, Long idUsuarioConectado){
        final Tablero tablero = findTableroByIdOrThrow(idTablero);
        Usuario usuario = usuarioRepository.findById(idUsuario);
        if(!tablero.getAdministrador().getId().equals(idUsuarioConectado)){ // Si el usuario actual no es administrador del tablero no puede añadir usuarios
            throw new TableroServiceExcepion("Lo siento, no estás autorizado a añadir usuarios al tablero");
        }

        if (tablero.getParticipantes().contains(usuario)) return null;
        tablero.getParticipantes().add(usuario);
        return tableroRepository.update(tablero);
    }

    public Tablero removeParticipanteTablero(Long idTablero, Long idUsuario, Long idUsuarioConectado){
        final Tablero tablero = findTableroByIdOrThrow(idTablero);
        Usuario usuario = usuarioRepository.findById(idUsuario);
        if(!tablero.getAdministrador().getId().equals(idUsuarioConectado)){ // Si el usuario actual no es administrador del tablero no puede eliminar usuarios
            throw new TableroServiceExcepion("Lo siento, no estás autorizado para eliminar usuarios del tablero");
        }

        if (!tablero.getParticipantes().contains(usuario)) return null;
        tablero.getParticipantes().remove(usuario);
        return tableroRepository.update(tablero);
    }

    public List<Usuario> listarParticipantesTablero(Long idTablero) {
        Tablero tablero = tableroRepository.findById(idTablero);
        if (tablero == null) {
            throw new TableroServiceExcepion("Tablero no existente");
        }

        final ArrayList<Usuario> participantes = new ArrayList<>(tablero.getParticipantes());
        Collections.sort(participantes, new Comparator<Usuario>() {
            @Override
            public int compare(Usuario a, Usuario b) {
                return a.getLogin().toLowerCase().compareTo(b.getLogin().toLowerCase());
            }
        });
        return participantes;
    }

}
