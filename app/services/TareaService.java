package services;

import models.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class TareaService {
    private UsuarioRepository usuarioRepository;
    private TareaRepository tareaRepository;
    private TableroRepository tableroRepository;

    @Inject
    public TareaService(UsuarioRepository usuarioRepository, TareaRepository tareaRepository,
                        TableroRepository tableroRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;
        this.tableroRepository = tableroRepository;
    }

    // Devuelve la lista de tareas de un usuario, ordenadas por su id
    // (equivalente al orden de creación)
    public List<Tarea> allTareasUsuario(Long idUsuario) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        List<Tarea> tareas = new ArrayList<>(usuario.getTareas());
        tareas.sort(Comparator.comparingLong(Tarea::getId));
        return tareas;
    }

    public List<Tarea> tareasPendientesEnTablero(Long idTablero) {
        Tablero tablero = getTableroOrThrow(idTablero);
        List<Tarea> tareas = new ArrayList<>();
        for (Tarea tarea : tablero.getTareas()) {
            if(!tarea.isTerminada()) tareas.add(tarea);
        }
        tareas.sort(Comparator.comparingLong(Tarea::getId));
        return tareas;
    }

    public List<Tarea> tareasTerminadasEnTablero(Long idTablero) {
        Tablero tablero = getTableroOrThrow(idTablero);
        List<Tarea> tareas = new ArrayList<>();
        for (Tarea tarea : tablero.getTareas()) {
            if(tarea.isTerminada()) tareas.add(tarea);
        }
        tareas.sort(Comparator.comparingLong(Tarea::getId));
        return tareas;
    }

    public List<Tarea> tareasTerminadasUsuario(Long idUsuario) {
        getUsuarioOrThrow(idUsuario);
        return tareaRepository.findTareasTerminadas(idUsuario);
    }

    public List<Tarea> tareasPendientesUsuario(Long idUsuario) {
        getUsuarioOrThrow(idUsuario);
        return tareaRepository.findTareasPendientes(idUsuario);
    }

    public Tarea nuevaTarea(Long idUsuario, String titulo, String cuerpo, final Date fechaInicio, Date fechaLimite) {
        validateDateRange(fechaInicio, fechaLimite);
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        if (titulo.isEmpty()) throw new TareaServiceException("Nombre de tarea nulo");
        Tarea tarea = new Tarea(usuario, titulo, cuerpo, fechaInicio, fechaLimite);
        return tareaRepository.add(tarea);
    }

    public Tarea obtenerTarea(Long idTarea) {
        return tareaRepository.findById(idTarea);
    }

    public Tarea modificaTarea(Long idTarea, String nuevoTitulo, String nuevoCuerpo, final Date nuevaFechaInicio, Date nuevaFechaLimite) {
        validateDateRange(nuevaFechaInicio, nuevaFechaLimite);
        Tarea tarea = getTareaOrThrow(idTarea);
        if (nuevoTitulo.isEmpty()) nuevoTitulo = tarea.getTitulo();
        tarea.setTitulo(nuevoTitulo);
        tarea.setCuerpo(nuevoCuerpo);
        tarea.setFechaInicio(nuevaFechaInicio);
        tarea.setFechaLimite(nuevaFechaLimite);
        tarea = tareaRepository.update(tarea);
        return tarea;
    }

    public Tarea terminarTarea(Long idTarea) {
        final Tarea tarea = getTareaOrThrow(idTarea);
        if (tarea.isTerminada()) throw new TareaServiceException("Tarea ya terminada");
        tarea.setTerminada(true);
        return tareaRepository.update(tarea);
    }

    public Tarea restaurarTarea(Long idTarea) {
        final Tarea tarea = getTareaOrThrow(idTarea);
        if (!tarea.isTerminada()) throw new TareaServiceException("Tarea ya restaurada");
        tarea.setTerminada(false);
        return tareaRepository.update(tarea);
    }

    public void borraTarea(Long idTarea) {
        Tarea tarea = tareaRepository.findById(idTarea);
        getTareaOrThrow(idTarea);
        tareaRepository.delete(idTarea);
    }

    private Usuario getUsuarioOrThrow(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario);
        if (usuario == null) {
            throw new TareaServiceException("Usuario no existente");
        }
        return usuario;
    }

    private Tarea getTareaOrThrow(Long idTarea) {
        Tarea tarea = tareaRepository.findById(idTarea);
        if (tarea == null)
            throw new TareaServiceException("Tarea no existente");
        return tarea;
    }

    private Tablero getTableroOrThrow(Long idTablero) {
        Tablero tablero = tableroRepository.findById(idTablero);
        if (tablero == null)
            throw new TareaServiceException("Tablero no existente");
        return tablero;
    }

    private void validateDateRange(final Date fechaInicio, final Date fechaLimite) {
        if (fechaInicio != null && fechaLimite != null && fechaLimite.before(fechaInicio)) {
            throw new TareaServiceException("Fecha límite anterior a la de inicio");
        }
    }
}
