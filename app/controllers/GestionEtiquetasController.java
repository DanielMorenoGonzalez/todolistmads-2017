package controllers;

import models.Etiqueta;
import models.Tarea;
import models.Usuario;
import models.Tablero;
import org.apache.commons.lang3.time.DateUtils;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Result;
import play.Logger;
import play.mvc.Security;
import play.twirl.api.Html;
import security.ActionAuthenticator;
import services.EtiquetaService;
import services.UsuarioService;
import services.TableroService;
import utilities.TableroUsuarioUtil;
import views.html.*;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static play.mvc.Controller.flash;
import static play.mvc.Controller.session;
import static play.mvc.Results.*;

public class GestionEtiquetasController {
    static private final String UNATHORIZED_MESSAGE = "Lo siento, no estás autorizado";

    private final UsuarioService usuarioService;
    private final EtiquetaService etiquetaService;
    private final TableroService tableroService;
    private final FormFactory formFactory;

    @Inject
    @SuppressWarnings("CdiInjectionPointsInspection")
    public GestionEtiquetasController(@NotNull final UsuarioService usuarioService, @NotNull final EtiquetaService
                etiquetaService, @NotNull final TableroService tableroService, @NotNull final FormFactory formFactory) {
        this.usuarioService = usuarioService;
        this.etiquetaService = etiquetaService;
        this.tableroService = tableroService;
        this.formFactory = formFactory;
    }

    private boolean isUserAuthorized(final @NotNull long userId) {
        final String connectedUserIdStr = session("connected");
        final long connectedUserId = Long.valueOf(connectedUserIdStr);
        return Objects.equals(connectedUserId, userId);
    }

    private Long getConnectedUserId() {
        String connectedUserStr = session("connected");
        return Long.valueOf(connectedUserStr);
    }

    private boolean puedeAccederATablero(Usuario usuario, Tablero tablero) {
        boolean puede = TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTableroEnTableroPrivado(usuario,tablero); // Si el usuario no participa ni es administrador de un tablero privado no
        return !puede;
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result listarEtiquetas(@NotNull final long idUsuario) {
        if (!isUserAuthorized(idUsuario)){
            return unauthorized(UNATHORIZED_MESSAGE);
        }
        final String aviso = flash("aviso");
        final Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
        final List<Etiqueta> etiquetas = etiquetaService.allEtiquetasUsuario(idUsuario);
        return ok(listaEtiquetas.render(etiquetas, usuario, aviso));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result listarEtiquetasTablero(Long idTablero) {
        Tablero tablero = tableroService.obtenerTablero(idTablero);
        Usuario usuario = usuarioService.findUsuarioPorId(Long.valueOf(session("connected")));
        Long connectedUser = getConnectedUserId();
        if (tablero == null) return notFound("Tablero no encontrado");
        if (!Objects.equals(connectedUser, usuario.getId()) || !puedeAccederATablero(usuario, tablero)) {
            return unauthorized("Lo siento, no estás autorizado");
        }

        boolean pertenece = TableroUsuarioUtil.usuarioParticipaOEsAdministradorDeTablero(usuario, tablero);
        final String aviso = flash("aviso");
        final List<Etiqueta> etiquetas = etiquetaService.allEtiquetasTablero(idTablero);
        return ok(listaEtiquetasTablero.render(etiquetas, usuario, tablero, aviso, pertenece));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result formularioNuevaEtiqueta(Long idUsuario) {
        Long connectedUser = getConnectedUserId();
        if (!Objects.equals(connectedUser, idUsuario)) {
            return unauthorized("Lo siento, no estás autorizado");
        } else {
            Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
            return ok(formNuevaEtiqueta.render(usuario, formFactory.form(Etiqueta.class), ""));
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result creaNuevaEtiqueta(Long idUsuario) {
        Long connectedUser = getConnectedUserId();
        if (!Objects.equals(connectedUser, idUsuario)) {
            return unauthorized("Lo siento, no estás autorizado");
        } else {
            Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
            Form<Etiqueta> etiquetaForm = formFactory.form(Etiqueta.class).bindFromRequest();
            if (etiquetaForm.hasErrors()) {
                return badRequest(formNuevaEtiqueta.render(usuario, formFactory.form(Etiqueta.class), "Hay errores en el formulario"));
            }
            Etiqueta etiqueta = etiquetaForm.get();
            if (etiqueta.getTitulo().isEmpty()) {
                return badRequest(formNuevaEtiqueta.render(usuario, formFactory.form(Etiqueta.class), "El título de la etiqueta no puede estar vacío"));
            }
            Etiqueta etiquetaComprobacion = etiquetaService.findEtiquetaPorTituloConUsuario(etiqueta.getTitulo(), idUsuario);
            if (etiquetaComprobacion != null && etiqueta.getTitulo().equals(etiquetaComprobacion.getTitulo())) {
                return badRequest(formNuevaEtiqueta.render(usuario, formFactory.form(Etiqueta.class), "El título de la etiqueta ya existe: escoge otro"));
            }
            etiquetaService.nuevaEtiqueta(idUsuario, etiqueta.getTitulo(), etiqueta.getColor(), null);
            flash("aviso", "La etiqueta se ha grabado correctamente");
            return redirect(controllers.routes.GestionEtiquetasController.listarEtiquetas(idUsuario));
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result formularioEditaEtiqueta(Long idEtiqueta) {
        Etiqueta etiqueta = etiquetaService.obtenerEtiqueta(idEtiqueta);
        if (etiqueta == null) return notFound("Etiqueta no encontrada");
        else {
            Long connectedUser = getConnectedUserId();
            Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);
            if (etiqueta.getTablero() == null) {
                if (!Objects.equals(connectedUser, etiqueta.getUsuario().getId())) {
                    return unauthorized("Lo siento, no estás autorizado");
                }
                else {
                    return ok(formModificacionEtiqueta.render(etiqueta.getUsuario().getId(),
                        etiqueta.getId(),
                        etiqueta.getTitulo(),
                        etiqueta.getColor(),
                        "",
                        etiqueta.getTablero()));
                }
            }
            else {
                if (!Objects.equals(connectedUser, etiqueta.getUsuario().getId())
                    && TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTablero(usuario, etiqueta.getTablero())) {
                    return unauthorized("Lo siento, no estás autorizado");
                }
                else {
                    return ok(formModificacionEtiqueta.render(etiqueta.getUsuario().getId(),
                        etiqueta.getId(),
                        etiqueta.getTitulo(),
                        etiqueta.getColor(),
                        "",
                        etiqueta.getTablero()));
                }
            }
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result grabaEtiquetaModificada(Long idEtiqueta) {
        Long connectedUser = getConnectedUserId();
        Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);
        Etiqueta etiquetaResultado = etiquetaService.obtenerEtiqueta(idEtiqueta);
        if (etiquetaResultado.getTablero() == null) {
            if (!Objects.equals(connectedUser, etiquetaResultado.getUsuario().getId())) {
                return unauthorized("Lo siento, no estás autorizado");
            }
        }
        else {
            if (!Objects.equals(connectedUser, etiquetaResultado.getUsuario().getId())
                && TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTablero(usuario, etiquetaResultado.getTablero())) {
                return unauthorized("Lo siento, no estás autorizado");
            }
        }
        DynamicForm requestData = formFactory.form().bindFromRequest();
        final String nuevoTitulo = requestData.get("titulo");
        final String nuevoColor = requestData.get("color");
        if (nuevoTitulo.isEmpty()) {
            return badRequest(formModificacionEtiqueta.render(etiquetaResultado.getUsuario().getId(),
                idEtiqueta,
                etiquetaResultado.getTitulo(),
                etiquetaResultado.getColor(),
                "El título de la etiqueta no puede estar vacío",
                etiquetaResultado.getTablero()));
        }
        final Etiqueta etiquetaComprobacionTitulo;
        if(etiquetaResultado.getTablero() != null) {
            etiquetaComprobacionTitulo = etiquetaService.findEtiquetaPorTituloConTablero(nuevoTitulo, etiquetaResultado.getTablero().getId());
        }
        else {
            etiquetaComprobacionTitulo = etiquetaService.findEtiquetaPorTituloConUsuario(nuevoTitulo, etiquetaResultado.getUsuario().getId());
        }

        if (etiquetaComprobacionTitulo != null && !etiquetaResultado.getId().equals(etiquetaComprobacionTitulo.getId())) {
            return badRequest(formModificacionEtiqueta.render(etiquetaResultado.getUsuario().getId(),
                idEtiqueta,
                etiquetaResultado.getTitulo(),
                etiquetaResultado.getColor(),
                "Ya existe una etiqueta con ese título: escoge otro",
                etiquetaResultado.getTablero()));
        }
        if(etiquetaResultado.getTablero() != null) {
            final Etiqueta etiqueta = etiquetaService.modificaEtiqueta(idEtiqueta, nuevoTitulo, nuevoColor, etiquetaResultado.getTablero());
            return redirect(controllers.routes.GestionEtiquetasController.listarEtiquetasTablero(etiqueta.getTablero().getId()));
        }
        else {
            final Etiqueta etiqueta = etiquetaService.modificaEtiqueta(idEtiqueta, nuevoTitulo, nuevoColor, null);
            return redirect(controllers.routes.GestionEtiquetasController.listarEtiquetas(etiqueta.getUsuario().getId()));
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result borraEtiqueta(Long idEtiqueta) {
        etiquetaService.borraEtiqueta(idEtiqueta);
        flash("aviso", "Etiqueta borrada correctamente");
        return ok();
    }
}
