package models;

import common.DBSetup;
import org.apache.commons.lang3.time.DateUtils;
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
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class EtiquetaTest {
    private static final String LOOKUP_DB_NAME = "DBTest";
    private static final int UN_MINUTO_EN_MS = 60 * 1000;
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

    private EtiquetaRepository newEtiquetaRepository() {
        return injector.instanceOf(EtiquetaRepository.class);
    }

    private TareaRepository newTareaRepository() {
        return injector.instanceOf(TareaRepository.class);
    }

    @Test
    public void testCrearEtiqueta() {
        Usuario usuario = new Usuario("danielmoreno", "danielmoreno@gmail.com");
        Etiqueta etiqueta = new Etiqueta(usuario, "Deportes", "#fbca04");

        assertEquals("danielmoreno", etiqueta.getUsuario().getLogin());
        assertEquals("danielmoreno@gmail.com", etiqueta.getUsuario().getEmail());
        assertEquals("Deportes", etiqueta.getTitulo());
    }

    @Test
    public void testEqualsEtiquetasConId() {
        Usuario usuario = new Usuario("danielmoreno", "danielmoreno@gmail.com");
        Etiqueta etiqueta1 = new Etiqueta(usuario, "Deportes", "#fbca04");
        Etiqueta etiqueta2 = new Etiqueta(usuario, "Recetas", "#fbca04");
        Etiqueta etiqueta3 = new Etiqueta(usuario, "Asignaturas de cuarto", "#fbca04");
        etiqueta1.setId(1L);
        etiqueta2.setId(1L);
        etiqueta3.setId(2L);
        assertEquals(etiqueta1, etiqueta2);
        assertNotEquals(etiqueta1, etiqueta3);
    }

    @Test
    public void testEqualsEtiquetasSinId() {
        Usuario usuario = new Usuario("danielmoreno", "danielmoreno@gmail.com");
        Etiqueta etiqueta1 = new Etiqueta(usuario, "Deportes", "#fbca04");
        Etiqueta etiqueta2 = new Etiqueta(usuario, "Deportes", "#fbca04");
        Etiqueta etiqueta3 = new Etiqueta(usuario, "Asignaturas de cuarto", "#fbca04");

        assertEquals(etiqueta1, etiqueta2);
        assertNotEquals(etiqueta1, etiqueta3);
    }

    @Test
    public void testAddEtiquetaJPARepositoryInsertsEtiquetaDatabase() {
        UsuarioRepository usuarioRepository = newUsuarioRepository();
        EtiquetaRepository etiquetaRepository = newEtiquetaRepository();
        Usuario usuario = new Usuario("danielmoreno", "danielmoreno@gmail.com");
        usuario = usuarioRepository.add(usuario);
        Etiqueta etiqueta = new Etiqueta(usuario, "Deportes", "#fbca04");
        etiqueta = etiquetaRepository.add(etiqueta);
        Logger.info("Número de etiqueta: " + Long.toString(etiqueta.getId()));
        assertNotNull(etiqueta.getId());
        assertEquals("Deportes", getTituloFromEtiquetaDB(etiqueta.getId()));
        assertEquals("#fbca04", getColorFromEtiquetaDB(etiqueta.getId()));
    }

    private String getTituloFromEtiquetaDB(Long etiquetaId) {
        return db.withConnection(connection -> {
            String selectStatement = "SELECT TITULO FROM Etiqueta WHERE ID = ? ";
            PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
            prepStmt.setLong(1, etiquetaId);
            ResultSet rs = prepStmt.executeQuery();
            rs.next();
            return rs.getString("TITULO");
        });
    }

    private String getColorFromEtiquetaDB(Long etiquetaId) {
        return db.withConnection(connection -> {
            String selectStatement = "SELECT COLOR FROM Etiqueta WHERE ID = ? ";
            PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
            prepStmt.setLong(1, etiquetaId);
            ResultSet rs = prepStmt.executeQuery();
            rs.next();
            return rs.getString("COLOR");
        });
    }

    @Test
    public void testFindEtiquetaPorId() {
        EtiquetaRepository repository = newEtiquetaRepository();
        Etiqueta etiqueta = repository.findById(1000L);
        assertEquals("Deportes", etiqueta.getTitulo());
        assertEquals("#fbca04", etiqueta.getColor());
    }

    @Test
    public void testFindAllEtiquetasUsuario() {
        UsuarioRepository repository = newUsuarioRepository();
        Usuario usuario = repository.findById(1000L);
        assertEquals(3, usuario.getEtiquetas().size());
    }

    @Test
    public void constructorPorDefectoDebeTenerFechaActualDeLaEtiqueta() {
        final Date fechaActual = new Date();
        final Etiqueta etiqueta = new Etiqueta();

        assertThat(etiqueta.getFechaCreacion()).isCloseTo(fechaActual, UN_MINUTO_EN_MS);
    }

    @Test
    public void editarEtiquetaRepositoryDebeActualizarColor() {
        final UsuarioRepository usuarioRepository = newUsuarioRepository();
        final EtiquetaRepository etiquetaRepository = newEtiquetaRepository();
        final Usuario usuario = usuarioRepository.findById(1000L);
        final Etiqueta etiqueta = new Etiqueta(usuario, "Tareas del lunes", "#c2e0c6");

        final Etiqueta anyadida = etiquetaRepository.add(etiqueta);
        assertThat(anyadida).hasFieldOrPropertyWithValue("color", "#c2e0c6");

        etiqueta.setColor("#0052cc");
        final Etiqueta update = etiquetaRepository.update(etiqueta);
        assertThat(update).isEqualTo(etiqueta);
    }

    @Test
    public void deleteEtiqueta() {
        EtiquetaRepository etiquetaRepository = newEtiquetaRepository();
        long idEtiqueta = 1000L;
        etiquetaRepository.delete(idEtiqueta);
        assertNull(etiquetaRepository.findById(idEtiqueta));
    }

    @Test
    public void testEtiquetaTieneVariasTareas() throws Exception {
        EtiquetaRepository etiquetaRepository = newEtiquetaRepository();
        UsuarioRepository usuarioRepository = newUsuarioRepository();
        TareaRepository tareaRepository = newTareaRepository();

        Usuario usuario = usuarioRepository.findById(1000L);
        Etiqueta etiqueta = etiquetaRepository.findById(1000L);
        // Creamos una nueva tarea
        Tarea tarea1 = new Tarea(usuario, "Tarea 1 de la etiqueta", "Actividad 1", null, null);
        tarea1 = tareaRepository.add(tarea1);
        // Asignamos la tarea a la etiqueta "Deportes"
        tarea1.setEtiqueta(etiqueta);
        // Creamos otra tarea tarea
        Tarea tarea2 = new Tarea(usuario, "Tarea 2 de la etiqueta", "Actividad 2", null, null);
        tarea2 = tareaRepository.add(tarea2);
        // Asignamos la tarea a la etiqueta "Deportes"
        tarea2.setEtiqueta(etiqueta);

        assertEquals("Deportes", tarea1.getEtiqueta().getTitulo());
        assertEquals("Deportes", tarea2.getEtiqueta().getTitulo());

        // Añadimos las tareas al conjunto de tareas de la etiqueta
        Set<Tarea> tareas = etiqueta.getTareas();
        tareas.add(tarea1);
        tareas.add(tarea2);
        etiqueta.setTareas(tareas);
        assertEquals(2, etiqueta.getTareas().size());
        assertTrue(etiqueta.getTareas().contains(tarea1));
        assertTrue(etiqueta.getTareas().contains(tarea2));
    }
}
