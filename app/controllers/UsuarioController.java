package controllers;

import models.Usuario;
import models.VistaCalendario;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.ActionAuthenticator;
import services.UsuarioService;
import views.html.*;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class UsuarioController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Inject
    private UsuarioService usuarioService;

    public Result saludo(String mensaje) {
        return ok(saludo.render("El mensaje que he recibido es: " + mensaje));
    }

    public Result formularioRegistro() {
        if(comprobarSession()){
            String connectedUserStr = session("connected");
            Long idUsuario = Long.valueOf(connectedUserStr);
            return redirect(controllers.routes.GestionTareasController.listaTareasPendientes(idUsuario)); // Si ya está la sesión iniciada redirige a sus tareas
        } else {
            return ok(formRegistro.render(formFactory.form(Registro.class), ""));
        }
    }

    public Result registroUsuario() {
        Form<Registro> form = formFactory.form(Registro.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(formRegistro.render(form, "Hay errores en el formulario"));
        }
        Registro datosRegistro = form.get();

        if (usuarioService.findUsuarioPorLogin(datosRegistro.login) != null) {
            return badRequest(formRegistro.render(form, "Login ya existente: escoge otro"));
        }

        if (usuarioService.findUsuarioPorEmail(datosRegistro.email) != null) {
            return badRequest(formRegistro.render(form, "Email ya existente: escoge otro"));
        }

        if (!datosRegistro.password.equals(datosRegistro.confirmacion)) {
            return badRequest(formRegistro.render(form, "No coinciden la contraseña y la confirmación"));
        }
        Usuario usuario = usuarioService.creaUsuario(datosRegistro.login, datosRegistro.email, datosRegistro.password);
        return redirect(controllers.routes.UsuarioController.formularioLogin());
    }

    public Result formularioLogin() {
        if (comprobarSession()) {
            String connectedUserStr = session("connected");
            Long idUsuario = Long.valueOf(connectedUserStr);
            return redirect(controllers.routes.GestionTareasController.listaTareasPendientes(idUsuario)); // Si ya está la sesión iniciada redirige a sus tareas
        } else {
            return ok(formLogin.render(formFactory.form(Login.class), ""));
        }
    }

    public Result loginUsuario() {
        Form<Login> form = formFactory.form(Login.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(formLogin.render(form, "Hay errores en el formulario"));
        }
        Login login = form.get();
        Usuario usuario = usuarioService.login(login.username, login.password);
        if (usuario == null) {
            return notFound(formLogin.render(form, "Login y contraseña no existentes"));
        } else {
            // Añadimos el id del usuario a la clave `connected` de
            // la sesión de Play
            // https://www.playframework.com/documentation/2.5.x/JavaSessionFlash
            session("connected", usuario.getId().toString());
            session("username", usuario.getLogin());
            return redirect(controllers.routes.GestionTareasController.listaTareasPendientes(usuario.getId()));
        }
    }

    // Comprobamos si hay alguien logeado con @Security.Authenticated(ActionAuthenticator.class)
    // https://alexgaribay.com/2014/06/15/authentication-in-play-framework-using-java/
    @Security.Authenticated(ActionAuthenticator.class)
    public Result logout() {
        String connectedUserStr = session("connected");
        session().remove("connected");
        session().remove(session("username"));
        return redirect(controllers.routes.UsuarioController.loginUsuario());
    }

    /* To Do
    @Security.Authenticated(ActionAuthenticator.class)
    public Result registroFacebook() {

    }
    */

    @Security.Authenticated(ActionAuthenticator.class)
    public Result detalleUsuario(Long id) {
        String connectedUserStr = session("connected");
        Long connectedUser = Long.valueOf(connectedUserStr);
        if (!Objects.equals(connectedUser, id)) {
            return unauthorized("Lo siento, no estás autorizado");
        } else {
            Usuario usuario = usuarioService.findUsuarioPorId(id);
            if (usuario == null) {
                return notFound("Usuario no encontrado");
            } else {
                Logger.debug("Encontrado usuario " + usuario.getId() + ": " + usuario.getLogin());
                String aviso = flash("aviso");
                return ok(detalleUsuario.render(usuario, aviso));
            }
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result formularioActualizarPerfil(Long idUsuario) {
        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
        if (usuario == null) {
            return notFound("Usuario no encontrado");
        } else {
            String connectedUserStr = session("connected");
            Long connectedUser = Long.valueOf(connectedUserStr);
            if (connectedUser != idUsuario) {
                return unauthorized("Lo siento, no estás autorizado");
            } else {
                return ok(formActualizarPerfil.render(usuario, ""));
            }
        }
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result actualizarPerfilUsuario(Long idUsuario) throws ParseException {
        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
        DynamicForm requestData = formFactory.form().bindFromRequest();

        // Al ser un campo opcional se puede dejar vacío (su login será el que tenía anteriormente)
        String nuevoLogin = requestData.get("login");
        if (nuevoLogin.isEmpty()) nuevoLogin = usuario.getLogin();
        else {
            // Comprobamos si el usuario ha elegido un login ya existente
            // En caso de que el usuario escoja su propio login de nuevo no dará error
            Usuario usuarioRegistrado = usuarioService.findUsuarioPorLogin(nuevoLogin);
            if (usuarioRegistrado != null && !usuario.getId().equals(usuarioRegistrado.getId())) {
                return badRequest(formActualizarPerfil.render(usuario, "Login ya existente: escoge otro"));
            }
            if (nuevoLogin.length() > 20) {
                return badRequest(formActualizarPerfil.render(usuario, "Login demasiado largo. Máximo 20 caracteres"));
            }
        }

        // Al ser un campo opcional se puede dejar vacío (su email será el que tenía anteriormente)
        String nuevoEmail = requestData.get("email");
        if (nuevoEmail.isEmpty()) nuevoEmail = usuario.getEmail();
        if (nuevoEmail.length() > 30) {
            return badRequest(formActualizarPerfil.render(usuario, "Email demasiado largo. Máximo 20 caracteres"));
        }

        // Al ser un campo opcional se puede dejar vacío (su password será la que tenía anteriormente)
        String nuevaPassword = requestData.get("password");
        if (nuevaPassword.isEmpty()) nuevaPassword = usuario.getPassword();
        if (nuevaPassword.length() > 20) {
            return badRequest(formActualizarPerfil.render(usuario, "Contraseña demasiado larga. Máximo 10 caracteres"));
        }

        // Al ser un campo opcional se puede dejar vacío (su nombre será el que tenía anteriormente)
        String nuevoNombre = requestData.get("nombre");
        if (nuevoNombre.isEmpty()) {
            // Necesario para que no aparezca una pantalla de "NullPointerException"
            if (usuario.getNombre() == null) {
                nuevoNombre = "";
            }
        }
        if (nuevoNombre.length() > 50) {
            return badRequest(formActualizarPerfil.render(usuario, "Nombre demasiado largo. Máximo 50 caracteres"));
        }

        // Al ser un campo opcional se puede dejar vacío (sus apellidos serán los que tenían anteriormente)
        String nuevosApellidos = requestData.get("apellidos");
        if (nuevosApellidos.isEmpty()) {
            // Necesario para que no aparezca una pantalla de "NullPointerException"
            if (usuario.getApellidos() == null) {
                nuevosApellidos = "";
            }
        }
        if (nuevosApellidos.length() > 50) {
            return badRequest(formActualizarPerfil.render(usuario, "Apellidos demasiado largos. Máximo 50 caracteres"));
        }

        String diaStr = requestData.get("diadropdown");
        String mesStr = requestData.get("mesdropdown");
        String anyoStr = requestData.get("anyodropdown");

        if (diaStr.isEmpty() || mesStr.isEmpty() || anyoStr.isEmpty()) {
            return badRequest(formActualizarPerfil.render(usuario, "La fecha no es válida"));
        }

        try {
            Calendar c = new GregorianCalendar();
            c.setLenient(false);
            // Los meses están indexados de 0 a 11
            c.set(Integer.parseInt(anyoStr), Integer.parseInt(mesStr) - 1, Integer.parseInt(diaStr));
            c.getTime();
        } catch (IllegalArgumentException e) {
            return badRequest(formActualizarPerfil.render(usuario, "La fecha no es válida"));
        }

        String nuevaFechaNacimientoStr = "" + diaStr + "-" + mesStr + "-" + anyoStr;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date nuevaFechaNacimiento;
        try {
            nuevaFechaNacimiento = sdf.parse(nuevaFechaNacimientoStr);
            // Fecha actual
            Date fechaActual = new Date();
            if (nuevaFechaNacimiento.after(fechaActual)) {
                return badRequest(formActualizarPerfil.render(usuario, "La fecha no es válida"));
            }
        } catch (ParseException e) {
            return badRequest(formActualizarPerfil.render(usuario, "La fecha no es válida"));
        }

        try {
            final String vistaCalendarioStr = requestData.get("vistaCalendario");
            final VistaCalendario vistaCalendario = !StringUtils.isBlank(vistaCalendarioStr) ?
                VistaCalendario.valueOf(vistaCalendarioStr)
                : null;
            usuarioService.actualizarPerfil(idUsuario, nuevoLogin, nuevoEmail, nuevaPassword, nuevoNombre, nuevosApellidos, nuevaFechaNacimiento, vistaCalendario);
        } catch (ParseException e) {
            Logger.debug("La fecha no tiene un formato válido: " + e);
            return badRequest(formActualizarPerfil.render(usuario, "La fecha no es válida"));
        } catch (IllegalArgumentException e) {
            return badRequest(formActualizarPerfil.render(usuario, "Esa vista de calendario todavía no existe en nuestro servidor"));
        }
        flash("aviso", "El perfil se ha actualizado correctamente");
        return redirect(controllers.routes.UsuarioController.detalleUsuario(idUsuario));
    }

    private boolean comprobarSession() {
        boolean sesionIniciada = true;
        String connectedUserStr = session("connected");
        if (connectedUserStr == null) sesionIniciada = false;
        if (connectedUserStr != null && !connectedUserStr.equals("")) {
            Long idUsuario = Long.valueOf(connectedUserStr);
            if (idUsuario != null) {
                Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
                if (usuario == null) { // Si el usuario es null es que no existe
                    session().remove("connected");
                    session().remove(session("username"));
                    sesionIniciada = false;
                }
            }
        }
        return sesionIniciada;
    }
}
