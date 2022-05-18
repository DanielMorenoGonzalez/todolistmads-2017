package controllers;

import models.Tablero;
import models.Tarea;
import models.Usuario;
import models.Etiqueta;
import org.apache.commons.lang3.time.DateUtils;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import security.ActionAuthenticator;
import services.TableroService;
import services.TareaService;
import services.UsuarioService;
import services.EtiquetaService;
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

import services.MailerService;

public class GestionTablerosController {
    static private final String UNATHORIZED_MESSAGE = "Lo siento, no estás autorizado";
    static private final String USUARIO_YA_APUNTADO = "Usuario ya apuntado";
    static private final String USUARIO_LOGIN_NOT_FOUND = "No se ha encontrado un usuario con ese login";
    static private final String NO_ADD_TU_MISMO = "No te puedes añadir a ti mismo a tu tablero como participante";

    static private final String USUARI_NOT_FOUND = "Usuario no encontrado";
    static private final String USUARIO_NO_EXISTENTE = "Usuario no existente";
    static private final String NO_QUITARTE_TU = "No te puedes eliminar a ti mismo de tu tablero";

    private final UsuarioService usuarioService;
    private final TableroService tableroService;
    private final TareaService tareaService;
    private final EtiquetaService etiquetaService;
    private final FormFactory formFactory;

    private final MailerService mailerService;

    @Inject
    @SuppressWarnings("CdiInjectionPointsInspection")
    public GestionTablerosController(@NotNull final UsuarioService usuarioService, @NotNull final TableroService
            tableroService, @NotNull final TareaService tareaService, @NotNull final EtiquetaService etiquetaService,
            @NotNull final FormFactory formFactory, @NotNull final MailerService mailerService) {
        this.usuarioService = usuarioService;
        this.tableroService = tableroService;
        this.tareaService = tareaService;
        this.etiquetaService = etiquetaService;
        this.formFactory = formFactory;
        this.mailerService = mailerService;
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result listarTableros(@NotNull final long idUsuario) {
        if (!isUserAuthorized(idUsuario)){
            return unauthorized(UNATHORIZED_MESSAGE);
        }
        final String aviso = flash("aviso");
        final Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
        final List<Tablero> tablerosAdministrados = tableroService.allTablerosAdministradosUsuario(idUsuario);
        final List<Tablero> tablerosParticipa = tableroService.allTablerosParticipaUsuario(idUsuario);
        boolean incluirPrivados = false;
        final List<Tablero> restoTableros = tableroService.restoTablerosUsuario(idUsuario, incluirPrivados);
        return ok(listaTableros.render(tablerosAdministrados, tablerosParticipa, restoTableros, usuario, aviso));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result listaParticipantes(@NotNull final long idTablero) {
        final Tablero tablero = tableroService.obtenerTablero(idTablero);
        Usuario usuario = usuarioService.findUsuarioPorId(Long.valueOf(session("connected")));
        if (tablero == null) return notFound("Tablero no encontrado");
        if (TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTableroEnTableroPrivado(usuario,tablero)){
            return unauthorized("Lo siento, no estás autorizado");
        }
        List<Usuario> participantes = tableroService.listarParticipantesTablero(idTablero);
        final String aviso = flash("aviso");
        return ok(listaParticipantes.render(tablero, formFactory.form(Usuario.class), participantes, aviso, ""));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result formularioNuevoTablero(@NotNull final long userId) {
        if (!isUserAuthorized(userId)) return unauthorized(UNATHORIZED_MESSAGE);
        Usuario usuario = usuarioService.findUsuarioPorId(userId);
        return ok(renderFormNuevoTableroWithMessage(usuario, ""));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result crearNuevoTablero(@NotNull final long userId) {
        if (!isUserAuthorized(userId)) return unauthorized(UNATHORIZED_MESSAGE);
        final Form<Tablero> tableroForm = formFactory.form(Tablero.class).bindFromRequest();
        if (tableroForm.hasErrors()) {
            Usuario usuario = usuarioService.findUsuarioPorId(userId);
            return badRequest(renderFormNuevoTableroWithMessage(usuario, "Hay errores en el formulario"));
        }
        final Tablero tablero = tableroForm.get();
        if(tablero.getNombre().isEmpty()) {
            Usuario usuario = usuarioService.findUsuarioPorId(userId);
            return badRequest(renderFormNuevoTableroWithMessage(usuario, "El nombre del tablero no puede estar vacío"));
        }
        if(tableroService.tableroExisteEnAdministradosPorUsuario(userId, tablero.getNombre())) {
            Usuario usuario = usuarioService.findUsuarioPorId(userId);
            return badRequest(renderFormNuevoTableroWithMessage(usuario, "Ya has creado un tablero con este título. Escoge otro"));
        }

        tableroService.crearTablero(userId, tablero.getNombre(), tablero.isPrivado());
        flash("aviso", "El tablero se ha grabado correctamente");
        return redirect(controllers.routes.GestionTablerosController.listarTableros(userId));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result formularioNuevaTareaEnTablero(@NotNull final long userId, @NotNull final long tableroId) {
        if (!isUserAuthorized(userId)) return unauthorized(UNATHORIZED_MESSAGE);
        Usuario usuario = usuarioService.findUsuarioPorId(userId);
        Tablero tablero = tableroService.obtenerTablero(tableroId);
        if (tablero == null) return notFound("Tablero no encontrado");
        if (TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTablero(usuario, tablero))
            return unauthorized(UNATHORIZED_MESSAGE);
        return ok(formNuevaTareaEnTablero.render(usuario, tablero, formFactory.form(Tarea.class), ""));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result crearNuevaTareaEnTablero(@NotNull final long userId, @NotNull final long tableroId) {
        if (!isUserAuthorized(userId)) return unauthorized(UNATHORIZED_MESSAGE);
        Tablero tablero = tableroService.obtenerTablero(tableroId);
        Usuario usuario = usuarioService.findUsuarioPorId(userId);
        if (TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTablero(usuario, tablero))
            return unauthorized(UNATHORIZED_MESSAGE);
        final Form<Tarea> tareaForm = formFactory.form(Tarea.class).bindFromRequest();
        if (tareaForm.hasErrors()) {
            return badRequest(formNuevaTareaEnTablero.render(usuario, tablero, formFactory.form(Tarea.class), "Hay errores en el formulario"));
        }
        final Tarea tarea = tareaForm.get();
        if(tarea.getTitulo().isEmpty()) {
            return badRequest(formNuevaTareaEnTablero.render(usuario, tablero, formFactory.form(Tarea.class), "El nombre de la tarea no puede estar vacío"));
        }
        final Date fechaLimite = tarea.getFechaLimite();
        final Date fechaInicio = tarea.getFechaInicio();
        if (fechaInicio != null && fechaLimite != null && fechaLimite.before(fechaInicio)) {
            return badRequest(formNuevaTareaEnTablero.render(usuario, tablero, formFactory.form(Tarea.class), "La fecha límite de la tarea no puede ser anterior a la de inicio"));
        }
        // No quiero que se valide la fecha de inicio. Puede ser cualquiera
        final Tarea tareafinal = tareaService.nuevaTarea(userId, tarea.getTitulo(), tarea.getCuerpo(), fechaInicio, fechaLimite);
        tablero = tableroService.addTareaATablero(tablero.getId(), tareafinal.getId());
        flash("aviso", "La tarea se ha grabado correctamente");
        return redirect(controllers.routes.GestionTareasController.listaTareasPendientesTablero(tableroId));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result formularioNuevaEtiquetaEnTablero(@NotNull final long userId, @NotNull final long tableroId) {
        if (!isUserAuthorized(userId)) return unauthorized(UNATHORIZED_MESSAGE);
        Usuario usuario = usuarioService.findUsuarioPorId(userId);
        Tablero tablero = tableroService.obtenerTablero(tableroId);
        if (tablero == null) return notFound("Tablero no encontrado");
        if (TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTablero(usuario, tablero))
            return unauthorized(UNATHORIZED_MESSAGE);
        return ok(formNuevaEtiquetaEnTablero.render(usuario, tablero, formFactory.form(Etiqueta.class), ""));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result crearNuevaEtiquetaEnTablero(@NotNull final long userId, @NotNull final long tableroId) {
        if (!isUserAuthorized(userId)) return unauthorized(UNATHORIZED_MESSAGE);
        Tablero tablero = tableroService.obtenerTablero(tableroId);
        Usuario usuario = usuarioService.findUsuarioPorId(userId);
        if (TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTablero(usuario, tablero))
            return unauthorized(UNATHORIZED_MESSAGE);
        final Form<Etiqueta> etiquetaForm = formFactory.form(Etiqueta.class).bindFromRequest();
        if (etiquetaForm.hasErrors()) {
            return badRequest(formNuevaEtiquetaEnTablero.render(usuario, tablero, formFactory.form(Etiqueta.class), "Hay errores en el formulario"));
        }
        final Etiqueta etiqueta = etiquetaForm.get();
        if(etiqueta.getTitulo().isEmpty()) {
            return badRequest(formNuevaEtiquetaEnTablero.render(usuario, tablero, formFactory.form(Etiqueta.class), "El título de la etiqueta no puede estar vacío"));
        }
        Etiqueta etiquetaComprobacion = etiquetaService.findEtiquetaPorTituloConTablero(etiqueta.getTitulo(), tableroId);
        if (etiquetaComprobacion != null && etiqueta.getTitulo().equals(etiquetaComprobacion.getTitulo())) {
            return badRequest(formNuevaEtiquetaEnTablero.render(usuario, tablero, formFactory.form(Etiqueta.class), "El título de la etiqueta ya existe: escoge otro"));
        }
        final Etiqueta etiquetafinal = etiquetaService.nuevaEtiqueta(userId, etiqueta.getTitulo(), etiqueta.getColor(), tablero);
        tablero = tableroService.addEtiquetaATablero(tablero.getId(), etiquetafinal.getId());
        flash("aviso", "La etiqueta se ha grabado correctamente");
        return redirect(controllers.routes.GestionEtiquetasController.listarEtiquetasTablero(tableroId));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result apuntarUsuarioATablero(@NotNull final long idTablero) {
        final String connectedUserIdStr = session("connected");
        final long connectedUserId = Long.valueOf(connectedUserIdStr);
        tableroService.apuntarUsuarioATablero(connectedUserId, idTablero);
        flash("aviso", "Te has apuntado al tablero correctamente");
        return redirect(controllers.routes.GestionTablerosController.listarTableros(connectedUserId));
    }

    private Html renderFormNuevoTableroWithMessage(final Usuario usuario, final String mensaje) {
        return formNuevoTablero.render(usuario, formFactory.form(Tablero.class), mensaje);
    }

    private boolean isUserAuthorized(final @NotNull long userId) {
        final String connectedUserIdStr = session("connected");
        final long connectedUserId = Long.valueOf(connectedUserIdStr);
        return Objects.equals(connectedUserId, userId);
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result apuntarUsuarioATableroPorAdministrador(@NotNull final long idTablero) {
        final String connectedUserIdStr = session("connected");
        final long connectedUserId = Long.valueOf(connectedUserIdStr);
        Usuario administrador = usuarioService.findUsuarioPorId(connectedUserId);

        final Tablero tablero = tableroService.obtenerTablero(idTablero);
        List<Usuario> participantes = tableroService.listarParticipantesTablero(idTablero);
        Form<Usuario> form = formFactory.form(Usuario.class).bindFromRequest();

        Usuario datosUsuarioParticipante = form.get();
        String loginParticipante = datosUsuarioParticipante.getLogin();
        Usuario usuario = usuarioService.findUsuarioPorLogin(loginParticipante);
        if(usuario == null){
            return badRequest(listaParticipantes.render(tablero, formFactory.form(Usuario.class), participantes, "", USUARIO_LOGIN_NOT_FOUND));
        }
        if((usuario.getId()).equals(connectedUserId)){
            return badRequest(listaParticipantes.render(tablero, formFactory.form(Usuario.class), participantes, "", NO_ADD_TU_MISMO));
        }
        Tablero t = tableroService.addParticipanteTablero(idTablero,usuario.getId(),connectedUserId);
        if(t == null){
            return badRequest(listaParticipantes.render(tablero, formFactory.form(Usuario.class), participantes, "", USUARIO_YA_APUNTADO));
        }

        flash("aviso", "Has añadido a un nuevo usuario correctamente.");
        if(tablero.isPrivado()) {
            mailerService.enviarMail(administrador, usuario, tablero);
        }
        return redirect(controllers.routes.GestionTablerosController.listaParticipantes(tablero.getId()));
    }



    @Security.Authenticated(ActionAuthenticator.class)
    public Result formularioApuntarUsuarioATableroPorAdministrador(@NotNull final long idTablero) {
        final String connectedUserIdStr = session("connected");
        final long connectedUserId = Long.valueOf(connectedUserIdStr);
        if (!isUserAuthorized(connectedUserId)) return unauthorized(UNATHORIZED_MESSAGE);

        Usuario usuario = usuarioService.findUsuarioPorId(connectedUserId);
        Tablero tablero = tableroService.obtenerTablero(idTablero);
        List<Usuario> participantes = tableroService.listarParticipantesTablero(idTablero);
        if (tablero == null) return notFound("Tablero no encontrado");
        if(!tablero.getAdministrador().getId().equals(connectedUserId)){
            return unauthorized("No estás autorizado para añadir participantes a este tablero");
        }
        if(usuario == null) return badRequest("No se ha encontrado el usuario");

        if (TableroUsuarioUtil.usuarioNoParticipaNiEsAdministradorDeTablero(usuario, tablero))
            return unauthorized(UNATHORIZED_MESSAGE);
        return ok(listaParticipantes.render(tablero, formFactory.form(Usuario.class), participantes, "", "Accedido por url"));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result quitarAUsuarioDeTableroPorAdministrador(@NotNull final long idTablero, Long idParticipante) {
        final String connectedUserIdStr = session("connected");
        final long connectedUserId = Long.valueOf(connectedUserIdStr);
        final Tablero tablero = tableroService.obtenerTablero(idTablero);
        List<Usuario> participantes = tableroService.listarParticipantesTablero(idTablero);

        Usuario usuario = usuarioService.findUsuarioPorId(idParticipante);
        if(usuario == null){
            return badRequest(listaParticipantes.render(tablero, formFactory.form(Usuario.class), participantes, USUARI_NOT_FOUND, ""));
        }
        if((usuario.getId()).equals(connectedUserId)){
            return badRequest(listaParticipantes.render(tablero, formFactory.form(Usuario.class), participantes, NO_QUITARTE_TU, ""));
        }
        Tablero t = tableroService.removeParticipanteTablero(idTablero,idParticipante,connectedUserId);
        if(t == null){
            return badRequest(listaParticipantes.render(tablero, formFactory.form(Usuario.class), participantes, USUARIO_NO_EXISTENTE, ""));
        }

        flash("aviso", "Has eliminado al usuario "+usuario.getLogin()+" del tablero correctamente.");
        return ok(listaParticipantes.render(tablero, formFactory.form(Usuario.class), participantes, "", ""));
    }

    @Security.Authenticated(ActionAuthenticator.class)
  public Result desapuntarUsuarioDeTablero(@NotNull final long idTablero) {
      final Tablero tablero = tableroService.obtenerTablero(idTablero);
      List<Usuario> participantes = tableroService.listarParticipantesTablero(idTablero);
      final String connectedUserIdStr = session("connected");
      final long connectedUserId = Long.valueOf(connectedUserIdStr);
      Usuario usuarioConectado = usuarioService.findUsuarioPorId(connectedUserId);
      // si el usuario conectado es un participante podrá
      if(!tablero.getParticipantes().contains(usuarioConectado)){
          return badRequest(listaParticipantes.render(tablero, formFactory.form(Usuario.class), participantes, "No puedes desapuntarte de un tablero al que no estabas apuntado", ""));
      }

      tableroService.desapuntarUsuarioDeTablero(connectedUserId, idTablero);
      if(tablero.isPrivado()){
        return redirect(controllers.routes.GestionTablerosController.listarTableros(connectedUserId));
      } else {
          flash("aviso", "Te has desapuntado del tablero correctamente");
          return ok(listaParticipantes.render(tablero, formFactory.form(Usuario.class), participantes, "", ""));
      }
  }

}
