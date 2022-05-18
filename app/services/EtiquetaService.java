package services;

import models.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EtiquetaService {
    private UsuarioRepository usuarioRepository;
    private EtiquetaRepository etiquetaRepository;
    private TareaRepository tareaRepository;
    private TableroRepository tableroRepository;

    @Inject
    public EtiquetaService(UsuarioRepository usuarioRepository, EtiquetaRepository etiquetaRepository,
                TareaRepository tareaRepository, TableroRepository tableroRepository) {
        this.usuarioRepository = usuarioRepository;
        this.etiquetaRepository = etiquetaRepository;
        this.tareaRepository = tareaRepository;
        this.tableroRepository = tableroRepository;
    }

    private Usuario getUsuarioOrThrow(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario);
        if (usuario == null) throw new EtiquetaServiceException("Usuario no existente");
        return usuario;
    }

    private Etiqueta getEtiquetaOrThrow(Long idEtiqueta) {
        Etiqueta etiqueta = etiquetaRepository.findById(idEtiqueta);
        if (etiqueta == null) throw new EtiquetaServiceException("Etiqueta no existente");
        return etiqueta;
    }

    private Tarea getTareaOrThrow(Long idTarea) {
        Tarea tarea = tareaRepository.findById(idTarea);
        if (tarea == null) throw new EtiquetaServiceException("Tarea no existente");
        return tarea;
    }

    private Tablero getTableroOrThrow(Long idTablero) {
        Tablero tablero = tableroRepository.findById(idTablero);
        if (tablero == null) throw new EtiquetaServiceException("Tablero no existente");
        return tablero;
    }

    public Etiqueta obtenerEtiqueta(Long idEtiqueta) {
        return etiquetaRepository.findById(idEtiqueta);
    }

    public Etiqueta findEtiquetaPorTituloConUsuario(String tituloEtiqueta, Long idUsuario) {
        return etiquetaRepository.findByTituloConUsuario(tituloEtiqueta, idUsuario);
    }

    public Etiqueta findEtiquetaPorTituloConTablero(String tituloEtiqueta, Long idTablero) {
        return etiquetaRepository.findByTituloConTablero(tituloEtiqueta, idTablero);
    }

    // Devuelve la lista de etiquetas de un usuario, ordenadas por su id
    // (equivalente al orden de creación)
    public List<Etiqueta> allEtiquetasUsuario(Long idUsuario) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        List<Etiqueta> etiquetas = new ArrayList<>();
        for (Etiqueta etiqueta : usuario.getEtiquetas()) {
            if (etiqueta.getTablero() == null) {
                etiquetas.add(etiqueta);
            }
        }
        etiquetas.sort(Comparator.comparingLong(Etiqueta::getId));
        return etiquetas;
    }

    // Devuelve la lista de etiquetas de un tablero, ordenadas por su id
    // (equivalente al orden de creación)
    public List<Etiqueta> allEtiquetasTablero(Long idTablero) {
        Tablero tablero = getTableroOrThrow(idTablero);
        List<Etiqueta> etiquetas = new ArrayList<>(tablero.getEtiquetas());
        etiquetas.sort(Comparator.comparingLong(Etiqueta::getId));
        return etiquetas;
    }

    public Etiqueta nuevaEtiqueta(Long idUsuario, String titulo, String color, Tablero tablero) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        if (titulo.isEmpty()) throw new EtiquetaServiceException("Título de etiqueta nulo");
        if (color.isEmpty()) throw new EtiquetaServiceException("Color de etiqueta nulo");
        if(tablero != null) {
            getTableroOrThrow(tablero.getId());
            for (Etiqueta etiqueta : tablero.getEtiquetas()) {
                if (findEtiquetaPorTituloConTablero(titulo, tablero.getId()) != null) {
                    throw new EtiquetaServiceException("Título de etiqueta ya existente");
                }
            }
        }
        else {
            for (Etiqueta etiqueta : usuario.getEtiquetas()) {
                if (findEtiquetaPorTituloConUsuario(titulo, idUsuario) != null) {
                    throw new EtiquetaServiceException("Título de etiqueta ya existente");
                }
            }
        }
        Etiqueta etiqueta = new Etiqueta(usuario, titulo, color);
        return etiquetaRepository.add(etiqueta);
    }

    public Etiqueta modificaEtiqueta(Long idEtiqueta, String nuevoTitulo, String nuevoColor, Tablero tablero) {
        Etiqueta etiqueta = getEtiquetaOrThrow(idEtiqueta);

        if (nuevoTitulo.isEmpty()) throw new EtiquetaServiceException("Título de etiqueta es nulo");
        if (nuevoColor.isEmpty()) throw new EtiquetaServiceException("Color de etiqueta es nulo");

        if(tablero != null) {
            getTableroOrThrow(tablero.getId());
            Etiqueta etiquetaSingleResult = findEtiquetaPorTituloConTablero(nuevoTitulo, etiqueta.getTablero().getId());
            if (findEtiquetaPorTituloConTablero(nuevoTitulo, tablero.getId()) != null
                && !etiquetaSingleResult.getId().equals(etiqueta.getId())) {
                throw new EtiquetaServiceException("Título de etiqueta ya existente");
            }
        }
        else {
            Etiqueta etiquetaSingleResult = findEtiquetaPorTituloConUsuario(nuevoTitulo, etiqueta.getUsuario().getId());
            if (findEtiquetaPorTituloConUsuario(nuevoTitulo, etiqueta.getUsuario().getId()) != null
                && !etiquetaSingleResult.getId().equals(etiqueta.getId())) {
                    throw new EtiquetaServiceException("Título de etiqueta ya existente");
            }
        }

        etiqueta.setTitulo(nuevoTitulo);
        etiqueta.setColor(nuevoColor);
        etiqueta = etiquetaRepository.update(etiqueta);
        return etiqueta;
    }

    public void borraEtiqueta(Long idEtiqueta) {
        Etiqueta etiqueta = getEtiquetaOrThrow(idEtiqueta);
        for (Tarea tarea : etiqueta.getTareas()) {
            tarea.setEtiqueta(null);
            tareaRepository.update(tarea);
        }
        etiquetaRepository.delete(idEtiqueta);
    }

    public Etiqueta addTareaAEtiqueta(Long idEtiqueta, Long idTarea) {
        Etiqueta etiqueta = getEtiquetaOrThrow(idEtiqueta);
        Tarea tarea = getTareaOrThrow(idTarea);

        etiqueta.getTareas().add(tarea);
        tarea.setEtiqueta(etiqueta);
        tareaRepository.update(tarea);
        return etiqueta;
    }

    public void quitarEtiquetaDeTarea(Long idEtiqueta, Long idTarea) {
        Etiqueta etiqueta = getEtiquetaOrThrow(idEtiqueta);
        Tarea tarea = getTareaOrThrow(idTarea);
        tarea.setEtiqueta(null);
        tareaRepository.update(tarea);
    }
}
