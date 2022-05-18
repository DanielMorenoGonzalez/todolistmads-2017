package services;

import common.DBSetup;
import models.Usuario;
import models.VistaCalendario;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Environment;
import play.db.jpa.JPAApi;
import play.inject.Injector;
import play.inject.guice.GuiceApplicationBuilder;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import static org.junit.Assert.*;


public class UsuarioServiceTest {
    private static final String LOOKUP_DB_NAME = "DBTest";
    private static Injector injector;

    // Se ejecuta sólo una vez, al principio de todos los tests
    @BeforeClass
    static public void initApplication() {
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

    private UsuarioService newUsuarioService() {
        return injector.instanceOf(UsuarioService.class);
    }

    //Test 5: crearNuevoUsuarioCorrectoTest
    @Test
    public void crearNuevoUsuarioCorrectoTest() {
        UsuarioService usuarioService = newUsuarioService();
        Usuario usuario = usuarioService.creaUsuario("luciaruiz", "lucia.ruiz@gmail.com", "123456");
        assertNotNull(usuario.getId());
        assertEquals("luciaruiz", usuario.getLogin());
        assertEquals("lucia.ruiz@gmail.com", usuario.getEmail());
        assertEquals("123456", usuario.getPassword());
    }

    //Test 6:crearNuevoUsuarioLoginRepetidoLanzaExcepcion
    @Test(expected = UsuarioServiceException.class)
    public void crearNuevoUsuarioLoginRepetidoLanzaExcepcion() {
        UsuarioService usuarioService = newUsuarioService();
        // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
        usuarioService.creaUsuario("juangutierrez", "juan.gutierrez@gmail.com", "123456");
    }

    //Test 7: findUsuarioPorLogin
    @Test
    public void findUsuarioPorLogin() {
        UsuarioService usuarioService = newUsuarioService();
        // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
        Usuario usuario = usuarioService.findUsuarioPorLogin("juangutierrez");
        assertNotNull(usuario);
        assertEquals((Long) 1000L, usuario.getId());
    }

    //Test 8:loginUsuarioExistenteTest
    @Test
    public void loginUsuarioExistenteTest() {
        UsuarioService usuarioService = newUsuarioService();
        // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
        Usuario usuario = usuarioService.login("juangutierrez", "123456789");
        assertEquals((Long) 1000L, usuario.getId());
    }

    //Test 9: loginUsuarioNoExistenteTest
    @Test
    public void loginUsuarioNoExistenteTest() {
        UsuarioService usuarioService = newUsuarioService();
        // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
        Usuario usuario = usuarioService.login("juan", "123456789");
        assertNull(usuario);
    }

    //Test 10: findUsuarioPorId
    @Test
    public void findUsuarioPorId() {
        UsuarioService usuarioService = newUsuarioService();
        // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
        Usuario usuario = usuarioService.findUsuarioPorId(1000L);
        assertNotNull(usuario);
        assertEquals("juangutierrez", usuario.getLogin());
    }

    @Test
    public void actualizarPerfil() throws ParseException {
        UsuarioService usuarioService = newUsuarioService();
        Long idUsuario = 1000L;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaNacimiento = sdf.parse("26-05-1994");
        usuarioService.actualizarPerfil(idUsuario, "danielmoreno", "dany@gmail.com", "12345", "Daniel", "Moreno González", fechaNacimiento, VistaCalendario.Mes);
        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);

        assertNotNull(usuario);
        assertEquals("danielmoreno", usuario.getLogin());
        assertEquals("dany@gmail.com", usuario.getEmail());
        assertEquals("12345", usuario.getPassword());
        assertEquals("Daniel", usuario.getNombre());
        assertEquals("Moreno González", usuario.getApellidos());
        assertEquals(VistaCalendario.Mes, usuario.getVistaCalendario());
        assertTrue(usuario.getFechaNacimiento().compareTo(sdf.parse("26-05-1994")) == 0);
    }

    @Test
    public void actualizarPerfilConMismoLoginUsuarioLogeado() throws ParseException {
        UsuarioService usuarioService = newUsuarioService();
        Long idUsuario = 1000L;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaNacimiento = sdf.parse("14-12-1900");
        usuarioService.actualizarPerfil(idUsuario, "juangutierrez", "danymoreno@gmail.com", "12345", "Dani", "Moreno", fechaNacimiento, VistaCalendario.Dia);
        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);

        assertNotNull(usuario);
        assertEquals("juangutierrez", usuario.getLogin());
        assertEquals("danymoreno@gmail.com", usuario.getEmail());
        assertEquals("12345", usuario.getPassword());
        assertEquals("Dani", usuario.getNombre());
        assertEquals("Moreno", usuario.getApellidos());
        assertEquals(VistaCalendario.Dia, usuario.getVistaCalendario());
        assertTrue(usuario.getFechaNacimiento().compareTo(sdf.parse("14-12-1900")) == 0);
    }

    @Test(expected = UsuarioServiceException.class)
    public void actualizarPerfilLoginNoValidoYaExisteLanzaExcepcion() throws ParseException {
        UsuarioService usuarioService = newUsuarioService();
        Usuario usuario = usuarioService.creaUsuario("danyleon", "danyleon@gmail.com", "12345");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaNacimiento = sdf.parse("22-09-1998");
        usuarioService.actualizarPerfil(usuario.getId(), "juangutierrez", "danyleon@gmail.com", "12345", "Dany", "Moreno", fechaNacimiento, null);
    }

    @Test(expected = UsuarioServiceException.class)
    public void actualizarPerfilLoginNoValidoSuperaLongitudLanzaExcepcion() throws ParseException {
        UsuarioService usuarioService = newUsuarioService();
        Long idUsuario = 1000L;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaNacimiento = sdf.parse("20-11-1994");
        usuarioService.actualizarPerfil(idUsuario, "juanantoniopuiggutierrez", "lolopuig@gmail.com", "123456", "Lolo", "Puig", fechaNacimiento, null);
    }

    @Test(expected = UsuarioServiceException.class)
    public void actualizarPerfilFechaNacimientoNoValidaMenor1900LanzaExcepcion() throws ParseException {
        UsuarioService usuarioService = newUsuarioService();
        Long idUsuario = 1000L;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaNacimiento = sdf.parse("01-10-1898");
        usuarioService.actualizarPerfil(idUsuario, "rafasala", "rafasala@gmail.com", "123456", "Rafa", "Sala", fechaNacimiento, null);
    }

    @Test(expected = UsuarioServiceException.class)
    public void actualizarPerfilFechaNacimientoNoValidaMayorActualLanzaExcepcion() throws ParseException {
        UsuarioService usuarioService = newUsuarioService();
        Long idUsuario = 1000L;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaNacimiento = sdf.parse("01-10-2022");
        usuarioService.actualizarPerfil(idUsuario, "rafasala", "rafasala@gmail.com", "123456", "Rafa", "Sala", fechaNacimiento, null);
    }

    @Test
    public void actualizarPerfilSinCampoNombre() throws ParseException {
        UsuarioService usuarioService = newUsuarioService();
        Long idUsuario = 1000L;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaNacimiento = sdf.parse("01-10-2000");
        usuarioService.actualizarPerfil(idUsuario, "rafasala", "rafasala@gmail.com", "123456", "", "Sala", fechaNacimiento, null);
        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);

        assertNotNull(usuario);
        assertEquals("rafasala", usuario.getLogin());
        assertEquals("rafasala@gmail.com", usuario.getEmail());
        assertEquals("123456", usuario.getPassword());
        assertEquals("", usuario.getNombre());
        assertEquals("Sala", usuario.getApellidos());
        assertEquals(null, usuario.getVistaCalendario());
        assertTrue(usuario.getFechaNacimiento().compareTo(sdf.parse("01-10-2000")) == 0);
    }

    @Test
    public void actualizarPerfilSinCampoLoginAsignaLoginAnterior() throws ParseException {
        UsuarioService usuarioService = newUsuarioService();
        Long idUsuario = 1000L;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaNacimiento = sdf.parse("01-10-2000");
        usuarioService.actualizarPerfil(idUsuario, "", "rafasala@gmail.com", "123456", "Rafa", "Sala", fechaNacimiento, null);
        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);

        assertNotNull(usuario);
        assertEquals("juangutierrez", usuario.getLogin());
        assertEquals("rafasala@gmail.com", usuario.getEmail());
        assertEquals("123456", usuario.getPassword());
        assertEquals("Rafa", usuario.getNombre());
        assertEquals("Sala", usuario.getApellidos());
        assertEquals(null, usuario.getVistaCalendario());
        assertTrue(usuario.getFechaNacimiento().compareTo(sdf.parse("01-10-2000")) == 0);
    }

    @Test
    public void findUsuarioPorEmail() {
        UsuarioService usuarioService = newUsuarioService();
        // En la BD de prueba usuarios_dataset se ha cargado el usuario pove
        Usuario usuario = usuarioService.findUsuarioPorEmail("pove@alu.ua.es");
        assertNotNull(usuario);
        assertEquals("pove", usuario.getLogin());
    }

    @Test
    public void loginUsuarioExistenteTestConEmail() {
        UsuarioService usuarioService = newUsuarioService();
        // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
        Usuario usuario = usuarioService.login("aplicationt1@gmail.com", "123456789");
        assertEquals((Long) 1000L, usuario.getId());
    }
}
