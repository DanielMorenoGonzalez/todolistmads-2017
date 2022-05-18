package services;

import common.DBSetup;
import models.Tablero;
import models.Usuario;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Environment;
import play.db.jpa.JPAApi;
import play.inject.Injector;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

public class MailerServiceTest {
    static private final String USUARIO_NO_EXISTENTE = "Usuario no existente";
    static private final String TABLERO_NO_EXISTENTE = "Tablero no existente";
    static private final String TABLERO_YA_EXISTENTE = "Tablero ya existente";
    static private final String NOMBRE_TABLERO_NULO = "Nombre de tablero nulo";
    static private final String USUARIO_YA_APUNTADO = "Usuario ya apuntado";
    static private final String USUARIO_NO_APUNTADO = "El usuario no estaba apuntado al tablero";
    private static final Date FECHA_LIMITE_EJEMPLO = new GregorianCalendar(2018, Calendar.JANUARY, 1, 12, 30).getTime();
    private static final Date FECHA_INICIO_EJEMPLO = new GregorianCalendar(2017, Calendar.DECEMBER, 31, 23, 30).getTime();
    static private final String LOOKUP_DB_NAME = "DBTest";
    static private Injector injector;


    @BeforeClass
    static public void initDatabase() {
        GuiceApplicationBuilder guiceApplicationBuilder =
                new GuiceApplicationBuilder().in(Environment.simple());
        injector = guiceApplicationBuilder.injector();
        // Instanciamos un JPAApi para que inicializar JPA
        injector.instanceOf(JPAApi.class);
    }

    @Before
    public void initData() throws Exception {
        DBSetup.initData("test/resources/usuarios_dataset.xml", LOOKUP_DB_NAME);
    }

    private TableroService newTableroService() {
        return injector.instanceOf(TableroService.class);
    }

    private UsuarioService newUsuarioService() {
        return injector.instanceOf(UsuarioService.class);
    }

    private MailerService newMailerService() {
        return injector.instanceOf(MailerService.class);
    }

    @Test
    public void enviarMail_ACorreoExistente_NoDaError() {
        final MailerService mailerService = newMailerService();
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idParticipante = 1000L;      // juangutierrez    - usuarioDestino, con correo aplicationt1@gmail.com
        final long idAdministrador = 1005L;     // pepe             - usuarioOrigen
        final long idTablero = 1002L;           //

        Usuario usuarioOrigen = usuarioService.findUsuarioPorId(idAdministrador);
        Usuario usuarioDestino = usuarioService.findUsuarioPorId(idParticipante);
        Tablero tablero = tableroService.obtenerTablero(idTablero);

        final Throwable thrown = catchThrowable(() -> mailerService.enviarMail(usuarioOrigen, usuarioDestino, tablero));
        assertEquals(null, thrown);
    }

}
