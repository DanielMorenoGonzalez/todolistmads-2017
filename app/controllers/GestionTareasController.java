package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Tablero;
import models.Tarea;
import models.Usuario;
import models.Etiqueta;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.ActionAuthenticator;
import services.TableroService;
import services.TareaService;
import services.TareaServiceException;
import services.UsuarioService;
import services.EtiquetaService;
import utilities.TableroUsuarioUtil;
import views.html.*;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class GestionTareasController extends Controller {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    @Inject private FormFactory formFactory;
    @Inject private UsuarioService usuarioService;
    @Inject private TareaService tareaService;
    @Inject private EtiquetaService etiquetaService;
    @Inject private TableroService tableroService;

    // Comprobamos si hay alguien logeado con @Security.Authenticated(ActionAuthenticator.class)
    // https://alexgaribay.com/2014/06/15/authentication-in-play-framework-using-java/
    @Security.Authenticated(ActionAuthenticator.class)
    public Result formularioNuevaTarea(Long idUsuario) {
        Long connectedUser = getConnectedUserId();
        if (!Objects.equals(connectedUser, idUsuario)) {
            return unauthorized("Lo siento, no estás autorizado");
        } else {
            Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
            return ok(formNuevaTarea.render(usuario, formFactory.form(Tarea.class), ""));
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result creaNuevaTarea(Long idUsuario) {
        Long connectedUser = getConnectedUserId();
        if (!Objects.equals(connectedUser, idUsuario)) {
            return unauthorized("Lo siento, no estás autorizado");
        } else {
            Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
            Form<Tarea> tareaForm = formFactory.form(Tarea.class).bindFromRequest();
            if (tareaForm.hasErrors()) {
                return badRequest(formNuevaTarea.render(usuario, formFactory.form(Tarea.class), "Hay errores en el formulario"));
            }
            Tarea tarea = tareaForm.get();
            if (tarea.getTitulo().isEmpty()) {
                return badRequest(formNuevaTarea.render(usuario, formFactory.form(Tarea.class), "El titulo de la tarea no puede estar vacío"));
            }
            final Date fechaLimite = tarea.getFechaLimite();
            final Date fechaInicio = tarea.getFechaInicio();
            if (fechaInicio != null && fechaLimite != null && fechaLimite.before(fechaInicio)) {
                return badRequest(formNuevaTarea.render(usuario, formFactory.form(Tarea.class), "La fecha límite de la tarea no puede ser anterior a la de inicio"));
            }
            // No quiero que se valide la fecha de inicio. Puede ser cualquiera
            tareaService.nuevaTarea(idUsuario, tarea.getTitulo(), tarea.getCuerpo(), fechaInicio, fechaLimite);
            flash("aviso", "La tarea se ha grabado correctamente");
            return redirect(controllers.routes.GestionTareasController.listaTareasPendientes(idUsuario));
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result listaTareasPendientes(Long idUsuario) {
        Long connectedUser = getConnectedUserId();
        if (!Objects.equals(connectedUser, idUsuario)) {
            return unauthorized("Lo siento, no estás autorizado");
        } else {
            String aviso = flash("aviso");
            Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
            List<Tarea> tareas = tareaService.tareasPendientesUsuario(idUsuario);
            List<Etiqueta> etiquetas = etiquetaService.allEtiquetasUsuario(idUsuario);
            return ok(listaTareas.render(tareas, usuario, aviso, "pendientes", etiquetas));
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result listaTareasTerminadas(Long idUsuario) {
        Long connectedUser = getConnectedUserId();
        if (!Objects.equals(connectedUser, idUsuario)) {
            return unauthorized("Lo siento, no estás autorizado");
        } else {
            String aviso = flash("aviso");
            Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
            List<Tarea> tareas = tareaService.tareasTerminadasUsuario(idUsuario);
            List<Etiqueta> etiquetas = etiquetaService.allEtiquetasUsuario(idUsuario);
            return ok(listaTareas.render(tareas, usuario, aviso, "terminadas", etiquetas));
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result listaTareasCalendario(Long idUsuario) {
        Long connectedUser = getConnectedUserId();
        if (!Objects.equals(connectedUser, idUsuario)) {
            return unauthorized("Lo siento, no estás autorizado");
        } else {
            String aviso = flash("aviso");
            Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
            List<Tarea> tareas = tareaService.tareasPendientesUsuario(idUsuario);
            return ok(calendarioTareas.render(tareas, usuario, aviso));
        }
    }

    private boolean puedeAccederATablero(Usuario usuario, Tablero tablero){
        boolean puede = TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTableroEnTableroPrivado(usuario,tablero); // Si el usuario no participa ni es administrador de un tablero privado no
        return !puede;
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result listaTareasTerminadasTablero(Long tableroId) {

        Tablero tablero = tableroService.obtenerTablero(tableroId);
        Usuario usuario = usuarioService.findUsuarioPorId(Long.valueOf(session("connected")));
        Long connectedUser = getConnectedUserId();
        if (tablero == null) return notFound("Tablero no encontrado");
        if (!Objects.equals(connectedUser, usuario.getId()) || !puedeAccederATablero(usuario,tablero)) {
            return unauthorized("Lo siento, no estás autorizado");
        }

        boolean pertenece = TableroUsuarioUtil.usuarioParticipaOEsAdministradorDeTablero(usuario, tablero);
        String aviso = flash("aviso");
        List<Tarea> tareas = tareaService.tareasTerminadasEnTablero(tableroId);
        return ok(listaTareasTablero.render(tareas, usuario, tablero, aviso, "terminadas", pertenece));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result listaTareasPendientesTablero(Long tableroId) {
        Tablero tablero = tableroService.obtenerTablero(tableroId);
        Long connectedUser = getConnectedUserId();
        Usuario usuario = usuarioService.findUsuarioPorId(Long.valueOf(session("connected")));
        if (tablero == null) return notFound("Tablero no encontrado");
        if (!Objects.equals(connectedUser, usuario.getId()) || !puedeAccederATablero(usuario,tablero)) {
            return unauthorized("Lo siento, no estás autorizado");
        }
        boolean pertenece = TableroUsuarioUtil.usuarioParticipaOEsAdministradorDeTablero(usuario, tablero);
        String aviso = flash("aviso");
        List<Tarea> tareas = tareaService.tareasPendientesEnTablero(tableroId);
        return ok(listaTareasTablero.render(tareas, usuario, tablero, aviso, "pendientes", pertenece));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result listaTareasCalendarioTablero(Long tableroId) {
        Tablero tablero = tableroService.obtenerTablero(tableroId);
        Usuario usuario = usuarioService.findUsuarioPorId(Long.valueOf(session("connected")));
        Long connectedUser = getConnectedUserId();
        if (tablero == null) return notFound("Tablero no encontrado");
        if (!Objects.equals(connectedUser, usuario.getId()) || !puedeAccederATablero(usuario,tablero)) {
            return unauthorized("Lo siento, no estás autorizado");
        }

        boolean pertenece = TableroUsuarioUtil.usuarioParticipaOEsAdministradorDeTablero(usuario, tablero);
        String aviso = flash("aviso");
        List<Tarea> tareas = tareaService.tareasPendientesEnTablero(tableroId);
        return ok(calendarioTareasTablero.render(tareas, usuario, tablero, aviso, tablero.getNombre()));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result formularioEditaTarea(Long idTarea) {
        Tarea tarea = tareaService.obtenerTarea(idTarea);
        if (tarea == null) {
            return notFound("Tarea no encontrada");
        } else {
            Long connectedUser = getConnectedUserId();
            Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);
            if (!Objects.equals(connectedUser, tarea.getUsuario().getId())
                && TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTablero(usuario, tarea.getTablero())) {
                return unauthorized("Lo siento, no estás autorizado");
            } else if(tarea.isTerminada()) {
                return unauthorized("Lo siento, no se pueden editar tareas terminadas. Restaura la tarea y podrás editarla.");
            } else {
                final List<Etiqueta> etiquetas = etiquetaService.allEtiquetasUsuario(usuario.getId());
                final Date fechaLimite = tarea.getFechaLimite();
                final Date fechaInicio = tarea.getFechaInicio();
                return ok(formModificacionTarea.render(tarea.getUsuario(),
                    tarea.getId(),
                    tarea.getTitulo(),
                    tarea.getCuerpo(),
                    "",
                    fechaInicio != null ? SDF.format(fechaInicio) : "",
                    fechaLimite != null ? SDF.format(fechaLimite) : "",
                    tarea.getTablero(),
                    etiquetas));
            }
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result grabaTareaModificada(Long idTarea) {
        Long connectedUser = getConnectedUserId();
        Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);
        Tarea tareaResultado = tareaService.obtenerTarea(idTarea);
        if (!Objects.equals(connectedUser, tareaResultado.getUsuario().getId())
            && TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTablero(usuario, tareaResultado.getTablero())) {
            return unauthorized("Lo siento, no estás autorizado");
        }
        DynamicForm requestData = formFactory.form().bindFromRequest();
        String nuevoTitulo = requestData.get("titulo");
        if (nuevoTitulo.isEmpty()) {
            nuevoTitulo = tareaResultado.getTitulo();
        }
        final Date fechaInicio = tareaResultado.getFechaInicio();
        final Date fechaLimite = tareaResultado.getFechaLimite();
        try {
            final String nuevoCuerpo = requestData.get("cuerpo");
            final String nuevaFechaInicioStr = requestData.get("fecha_inicio");
            final String nuevaFechaLimiteStr = requestData.get("fecha_limite");
            final Date nuevaFechaInicio = !StringUtils.isBlank(nuevaFechaInicioStr)
                ? SDF.parse(nuevaFechaInicioStr)
                : null;
            final Date nuevaFechaLimite = !StringUtils.isBlank(nuevaFechaLimiteStr)
                ? SDF.parse(nuevaFechaLimiteStr)
                : null;
            if (nuevaFechaInicio != null && nuevaFechaLimite != null && nuevaFechaLimite.before(nuevaFechaInicio)) {
                final List<Etiqueta> etiquetas = etiquetaService.allEtiquetasUsuario(usuario.getId());
                return badRequest(formModificacionTarea.render(tareaResultado.getUsuario(),
                    idTarea,
                    tareaResultado.getTitulo(),
                    tareaResultado.getCuerpo(),
                    "La fecha límite de la tarea no puede ser anterior a la de inicio",
                    fechaInicio != null ? SDF.format(fechaInicio) : "",
                    fechaLimite != null ? SDF.format(fechaLimite) : "",
                    tareaResultado.getTablero(),
                    etiquetas));
            }
            final Tarea tarea = tareaService.modificaTarea(idTarea, nuevoTitulo, nuevoCuerpo, nuevaFechaInicio, nuevaFechaLimite);
            // En caso de que la tarea tenga un tablero asociado, se redirigirá a las tareas del tablero,
            // no a las del usuario
            if(tarea.getTablero() != null) {
                return redirect(controllers.routes.GestionTareasController.listaTareasPendientesTablero(tarea.getTablero().getId()));
            }
            else {
                return redirect(controllers.routes.GestionTareasController.listaTareasPendientes(tarea.getUsuario().getId()));
            }

        } catch (ParseException e) {
            final List<Etiqueta> etiquetas = etiquetaService.allEtiquetasUsuario(usuario.getId());
            return badRequest(formModificacionTarea.render(tareaResultado.getUsuario(),
                idTarea,
                tareaResultado.getTitulo(),
                tareaResultado.getCuerpo(),
                "No se ha podido procesar la fecha. Asegúrate de que esté en el formato dd/MM/yyyy HH:mm",
                fechaInicio != null ? SDF.format(fechaInicio) : "",
                fechaLimite != null ? SDF.format(fechaLimite) : "",
                tareaResultado.getTablero(),
                etiquetas));
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    @BodyParser.Of(BodyParser.Json.class)
    public Result modificarFechaTarea(Long idTarea) {
        JsonNode json = request().body().asJson();
        if(json == null) return badRequest("Objeto JSON requerido en la llamada");
        if(!json.has("fechaInicio")) return badRequest("Falta el parámetro [fechaInicio] en el JSON");
        if(!json.has("fechaLimite")) return badRequest("Falta el parámetro [fechaLimite] en el JSON");
        try {
            final Date nuevaFechaInicio = SDF.parse(json.findPath("fechaInicio").textValue());
            final Date nuevaFechaLimite = SDF.parse(json.findPath("fechaLimite").textValue());
            final Tarea tarea = tareaService.obtenerTarea(idTarea);
            tareaService.modificaTarea(idTarea, tarea.getTitulo(), tarea.getCuerpo(), nuevaFechaInicio, nuevaFechaLimite);
            return ok();
        } catch (ParseException e) {
            return badRequest("El formato de las fechas del JSON no es el correcto. Esperado el formato dd/MM/yyyy HH:mm");
        } catch (TareaServiceException e) {
            return badRequest(e.getMessage());
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result grabaTareaTerminada(Long idTarea) {
        Tarea tarea = tareaService.terminarTarea(idTarea);
        flash("aviso", "Tarea " + tarea.getTitulo() + " terminada");
        if (tarea.getTablero() != null) {
            return redirect(controllers.routes.GestionTareasController.listaTareasPendientesTablero(tarea.getTablero().getId()));
        }
        else {
            return redirect(controllers.routes.GestionTareasController.listaTareasPendientes(tarea.getUsuario().getId()));
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result restaurarTareaTerminada(Long idTarea) { // Devolverá la tarea del listado de terminadas al de pendientes, donde sí se podrá editar
        Tarea tarea = tareaService.restaurarTarea(idTarea);
        flash("aviso", "Tarea " + tarea.getTitulo() + " restaurada");
        if (tarea.getTablero() != null) {
            return redirect(controllers.routes.GestionTareasController.listaTareasTerminadasTablero(tarea.getTablero().getId()));
        }
        else {
            return redirect(controllers.routes.GestionTareasController.listaTareasTerminadas(tarea.getUsuario().getId()));
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result borraTarea(Long idTarea) {
        tareaService.borraTarea(idTarea);
        flash("aviso", "Tarea borrada correctamente");
        return ok();
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result formularioAsociarEtiquetaATarea(Long idTarea) {
        Tarea tarea = tareaService.obtenerTarea(idTarea);
        if (tarea == null) return notFound("Tarea no encontrada");
        else {
            Long connectedUser = getConnectedUserId();
            Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);
            if (!Objects.equals(connectedUser, tarea.getUsuario().getId())
                && TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTablero(usuario, tarea.getTablero())) {
                return unauthorized("Lo siento, no estás autorizado");
            } else {
                List<Etiqueta> etiquetas = new ArrayList<>();
                if (tarea.getTablero() != null) {
                    etiquetas = etiquetaService.allEtiquetasTablero(tarea.getTablero().getId());
                }
                else {
                    etiquetas = etiquetaService.allEtiquetasUsuario(usuario.getId());
                }
                return ok(formAsociarEtiquetaATarea.render(usuario, tarea, etiquetas));
            }
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result asociarEtiquetaATarea(Long idTarea) {
        Long connectedUser = getConnectedUserId();
        Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);
        Tarea tareaResultado = tareaService.obtenerTarea(idTarea);
        if (!Objects.equals(connectedUser, tareaResultado.getUsuario().getId())
            && TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTablero(usuario, tareaResultado.getTablero())) {
            return unauthorized("Lo siento, no estás autorizado");
        }

        DynamicForm requestData = formFactory.form().bindFromRequest();
        String tituloEtiqueta = requestData.get("titulo");

        Etiqueta etiqueta;
        if (tareaResultado.getTablero() != null) {
            etiqueta = etiquetaService.findEtiquetaPorTituloConTablero(tituloEtiqueta, tareaResultado.getTablero().getId());
        }
        else {
            etiqueta = etiquetaService.findEtiquetaPorTituloConUsuario(tituloEtiqueta, usuario.getId());
        }

        etiqueta = etiquetaService.addTareaAEtiqueta(etiqueta.getId(), idTarea);
        if (tareaResultado.getTablero() != null) {
            return redirect(controllers.routes.GestionTareasController.listaTareasPendientesTablero(tareaResultado.getTablero().getId()));
        }
        else {
            return redirect(controllers.routes.GestionTareasController.listaTareasPendientes(usuario.getId()));
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result quitarEtiquetaDeTarea(Long idEtiqueta, Long idTarea) {
        etiquetaService.quitarEtiquetaDeTarea(idEtiqueta, idTarea);
        flash("aviso", "Has quitado la etiqueta de la tarea correctamente");
        return ok();
    }

    private Long getConnectedUserId() {
        String connectedUserStr = session("connected");
        return Long.valueOf(connectedUserStr);
    }
}
