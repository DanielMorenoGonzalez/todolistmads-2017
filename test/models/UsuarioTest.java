package models;

import common.DBSetup;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Environment;
import play.Logger;
import play.db.Database;
import play.db.jpa.JPAApi;
import play.inject.Injector;
import play.inject.guice.GuiceApplicationBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

public class UsuarioTest {
    private static final String LOOKUP_DB_NAME = "DBTest";
    private static Database db;
    private static Injector injector;

    // Se ejecuta sólo una vez, al principio de todos los tests
    @BeforeClass
    static public void initDatabase() {
        GuiceApplicationBuilder guiceApplicationBuilder =
                new GuiceApplicationBuilder().in(Environment.simple());
        injector = guiceApplicationBuilder.injector();
        db = injector.instanceOf(Database.class);
        // Instanciamos un JPAApi para que inicializar JPA
        injector.instanceOf(JPAApi.class);
    }

    @Before
    public void initData() throws Exception {
        DBSetup.initData("test/resources/usuarios_dataset.xml", LOOKUP_DB_NAME);
    }

    private UsuarioRepository newUsuarioRepository() {
        return injector.instanceOf(UsuarioRepository.class);
    }

    // Test 1: testCrearUsuario
    @Test
    public void testCrearUsuario() throws ParseException {
        // Los parámetros del constructor son los campos obligatorios
        Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
        usuario.setNombre("Juan");
        usuario.setApellidos("Gutierrez");
        usuario.setPassword("123456789");

        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        usuario.setFechaNacimiento(sdf.parse("1997-02-20"));

        assertEquals("juangutierrez", usuario.getLogin());
        assertEquals("juangutierrez@gmail.com", usuario.getEmail());
        assertEquals("Juan", usuario.getNombre());
        assertEquals("Gutierrez", usuario.getApellidos());
        assertEquals("123456789", usuario.getPassword());
        assertTrue(usuario.getFechaNacimiento().compareTo(sdf.parse("1997-02-20")) == 0);
    }

    // Test 2: testAddUsuarioJPARepositoryInsertsUsuarioDatabase
    @Test
    public void testAddUsuarioJPARepositoryInsertsUsuarioDatabase() {
        UsuarioRepository repository = newUsuarioRepository();
        Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
        usuario.setNombre("Juan");
        usuario.setApellidos("Gutierrez");
        usuario.setPassword("123456789");
        usuario = repository.add(usuario);
        Logger.info("Número de usuario: " + Long.toString(usuario.getId()));
        assertNotNull(usuario.getId());
        assertEquals("Juan", getNombreFromUsuarioDB(usuario.getId()));
    }

    private String getNombreFromUsuarioDB(Long usuarioId) {
        return db.withConnection(connection -> {
            String selectStatement = "SELECT NOMBRE FROM Usuario WHERE ID = ? ";
            PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
            prepStmt.setLong(1, usuarioId);
            ResultSet rs = prepStmt.executeQuery();
            rs.next();
            return rs.getString("NOMBRE");
        });
    }

    // Test 3: testFindUsuarioPorId
    @Test
    public void testFindUsuarioPorId() {
        UsuarioRepository repository = newUsuarioRepository();
        Usuario usuario = repository.findById(1000L);
        assertEquals("juangutierrez", usuario.getLogin());
    }

    // Test 4: testFindUsuarioPorLogin
    @Test
    public void testFindUsuarioPorLogin() {
        UsuarioRepository repository = newUsuarioRepository();
        Usuario usuario = repository.findByLogin("juangutierrez");
        assertEquals((Long) 1000L, usuario.getId());
    }

    // Test #12: testEqualsUsuariosConId
    @Test
    public void testEqualsUsuariosConId() {
        Usuario usuario1 = new Usuario("juangutierrez", "juangutierrez@gmail.com");
        Usuario usuario2 = new Usuario("mariafernandez", "mariafernandez@gmail.com");
        Usuario usuario3 = new Usuario("antoniolopez", "antoniolopez@gmail.com");
        usuario1.setId(1L);
        usuario2.setId(1L);
        usuario3.setId(2L);
        assertEquals(usuario1, usuario2);
        assertNotEquals(usuario1, usuario3);
    }

    // Test #13: testEqualsUsuariosSinId
    @Test
    public void testEqualsUsuariosSinId() {
        Usuario usuario1 = new Usuario("mariafernandez", "mariafernandez@gmail.com");
        Usuario usuario2 = new Usuario("mariafernandez", "mariafernandez@gmail.com");
        Usuario usuario3 = new Usuario("antoniolopez", "antoniolopez@gmail.com");
        assertEquals(usuario1, usuario2);
        assertNotEquals(usuario1, usuario3);
    }
}