package services;

import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import javax.inject.Inject;
import java.io.File;
import org.apache.commons.mail.EmailAttachment;
import playconfig.ConfigModule;
import playconfig.ConfigProvider;

import models.Usuario;
import models.Tablero;

public class MailerService {
  @Inject MailerClient mailerClient;

  static private final String NOTIFICACION_ADDED_TABLERO = "Participación en tablero ";
  static private final String comillasDobles = "\"";

  public void enviarMail(Usuario usuarioOrigen, Usuario usuarioDestino, Tablero tablero) {
      if(tablero.isPrivado()) {
          String datosUsuarioOrigen = usuarioOrigen.getNombre() != null ? usuarioOrigen.getNombre()+" ("+usuarioOrigen.getLogin()+")" : usuarioOrigen.getLogin();

          String urlTablero = "http://localhost/tableros/"+tablero.getId()+"/participantes";
          String cid = "1234";
          Email email = new Email()
          .setSubject(NOTIFICACION_ADDED_TABLERO+tablero.getNombre())
          .setFrom("TodoList Equipo 1 <a@gmail.com>")
          .addTo(usuarioDestino.getLogin()+" <"+usuarioDestino.getEmail()+">")
          // adds cid attachment
          //.addAttachment("image.jpg", new File("/some/path/image.jpg"), cid)
          // sends text, HTML or both...
          .setBodyText("A text message")
          .setBodyHtml("<html><body><p>Has sido añadido al tablero <b>"+tablero.getNombre()+"</b> por "+datosUsuarioOrigen+".</p> <p>Pincha <a href="+comillasDobles+urlTablero+comillasDobles+">aquí</a> para acceder al tablero.</p></body></html>");
          mailerClient.send(email);
      }
  }
}
