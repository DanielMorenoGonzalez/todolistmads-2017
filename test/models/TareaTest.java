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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
//import org.powermock.modules.junit4.PowerMockRunner;

//@RunWith(PowerMockRunner.class)
public class TareaTest {
    private static final Date FIN_DE_ANYO = new GregorianCalendar(2017, Calendar.DECEMBER, 31, 13, 0).getTime();
    private static final String LOOKUP_DB_NAME = "DBTest";
    private static final int UN_MINUTO_EN_MS = 60 * 1000;
    private static final long ID_USUARIO_JUAN_DATASET = 1000L;
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

    private TareaRepository newTareaRepository() {
        return injector.instanceOf(TareaRepository.class);
    }

    // Test #11: testCrearTarea
    @Test
    public void testCrearTarea() {
        Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS", "#Que comience el juego!", null, null);

        assertEquals("juangutierrez", tarea.getUsuario().getLogin());
        assertEquals("juangutierrez@gmail.com", tarea.getUsuario().getEmail());
        assertEquals("Práctica 1 de MADS", tarea.getTitulo());
        assertEquals("#Que comience el juego!", tarea.getCuerpo());
    }

    // Test #14: testEqualsTareasConId
    @Test
    public void testEqualsTareasConId() {
        Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
        Tarea tarea1 = new Tarea(usuario, "Práctica 1 de MADS", "#Que comience el juego!", null, null);
        Tarea tarea2 = new Tarea(usuario, "Renovar DNI", "Tengo que renovar mi DNI antes del lunes", null, null);
        Tarea tarea3 = new Tarea(usuario, "Pagar el alquiler", "", null, null);
        tarea1.setId(ID_USUARIO_JUAN_DATASET);
        tarea2.setId(ID_USUARIO_JUAN_DATASET);
        tarea3.setId(2L);
        assertEquals(tarea1, tarea2);
        assertNotEquals(tarea1, tarea3);
    }

    // Test #15
    @Test
    public void testEqualsTareasSinId() {
        Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
        Tarea tarea1 = new Tarea(usuario, "Renovar DNI", "Tengo que renovar mi DNI antes del lunes", null, null);
        Tarea tarea2 = new Tarea(usuario, "Renovar DNI", "Tengo que renovar mi DNI antes del lunes", null, null);
        Tarea tarea3 = new Tarea(usuario, "Pagar el alquiler", "", null, null);
        // También se comprueba que sean las mismas si ambas están terminadask
        tarea1.setTerminada(true);
        tarea2.setTerminada(true);
        assertEquals(tarea1, tarea2);
        assertNotEquals(tarea1, tarea3);
    }

    // Test #16: testAddTareaJPARepositoryInsertsTareaDatabase
    @Test
    public void testAddTareaJPARepositoryInsertsTareaDatabase() {
        UsuarioRepository usuarioRepository = newUsuarioRepository();
        TareaRepository tareaRepository = newTareaRepository();
        Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
        usuario = usuarioRepository.add(usuario);
        Tarea tarea = new Tarea(usuario, "Renovar DNI", "Tengo que renovar mi DNI antes del lunes", null, FIN_DE_ANYO);
        tarea = tareaRepository.add(tarea);
        Logger.info("Número de tarea: " + Long.toString(tarea.getId()));
        assertNotNull(tarea.getId());
        assertEquals("Renovar DNI", getTituloFromTareaDB(tarea.getId()));
        assertEquals("Tengo que renovar mi DNI antes del lunes", getCuerpoFromTareaDB(tarea.getId()));
        assertThat(getFechaLimiteFromTareaBD(tarea.getId())).isEqualTo(FIN_DE_ANYO);
    }

    private String getCuerpoFromTareaDB(Long tareaId) {
        return db.withConnection(connection -> {
            String selectStatement = "SELECT CUERPO FROM Tarea WHERE ID = ? ";
            PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
            prepStmt.setLong(1, tareaId);
            ResultSet rs = prepStmt.executeQuery();
            rs.next();
            return rs.getString("CUERPO");
        });
    }

    private String getTituloFromTareaDB(Long tareaId) {
        return db.withConnection(connection -> {
            String selectStatement = "SELECT TITULO FROM Tarea WHERE ID = ? ";
            PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
            prepStmt.setLong(1, tareaId);
            ResultSet rs = prepStmt.executeQuery();
            rs.next();
            return rs.getString("TITULO");
        });
    }

    private Date getFechaLimiteFromTareaBD(Long tareaId) {
        return db.withConnection(connection -> {
            final String selectStatement = "SELECT FECHALIMITE FROM Tarea WHERE ID = ?";
            final PreparedStatement preparedStatement = connection.prepareStatement(selectStatement);
            preparedStatement.setLong(1, tareaId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return new Date(resultSet.getTimestamp("FECHALIMITE").getTime());
        });
    }

    // Test #17 testFindTareaById
    @Test
    public void testFindTareaPorId() {
        TareaRepository repository = newTareaRepository();
        Tarea tarea = repository.findById(ID_USUARIO_JUAN_DATASET);
        assertEquals("Renovar DNI", tarea.getTitulo());
    }

    // Test #18 testFindAllTareasUsuario
    @Test
    public void testFindAllTareasUsuario() {
        UsuarioRepository repository = newUsuarioRepository();
        Usuario usuario = repository.findById(ID_USUARIO_JUAN_DATASET);
        assertEquals(2, usuario.getTareas().size());
    }

    @Test
    public void constructorTareaPorDefectoTieneQueTenerTareaNoTerminada() {
        assertThat(new Tarea()).hasFieldOrPropertyWithValue("terminada", false);
    }

    @Test
    public void constructorTareaConParametrosTieneQueTenerTareaNoTerminada() {
        final UsuarioRepository repository = newUsuarioRepository();
        final Usuario usuario = repository.findById(ID_USUARIO_JUAN_DATASET);
        assertThat(new Tarea(usuario, "Título", "Cuerpo", null, null))
            .hasFieldOrPropertyWithValue("terminada", false);
    }

    @Test
    public void cuandoTerminoUnaTareaTerminadaSaleTrue() {
        final UsuarioRepository repository = newUsuarioRepository();
        final Usuario usuario = repository.findById(ID_USUARIO_JUAN_DATASET);
        final Tarea tarea = new Tarea(usuario, "Título", "Cuerpo", null, null);
        tarea.setTerminada(true);
        assertThat(tarea).hasFieldOrPropertyWithValue("terminada", true);
    }

    @Test
    public void editarTareaRepositoryDebeActualizarElTerminada() {
        final UsuarioRepository usuarioRepository = newUsuarioRepository();
        final TareaRepository tareaRepository = newTareaRepository();
        final Usuario usuario = usuarioRepository.findById(ID_USUARIO_JUAN_DATASET);
        final Tarea tarea = new Tarea(usuario, "Título", "Cuerpo", null, null);

        final Tarea anyadida = tareaRepository.add(tarea);
        assertThat(anyadida).hasFieldOrPropertyWithValue("terminada", false);

        tarea.setTerminada(true);
        final Tarea update = tareaRepository.update(tarea);
        assertThat(update).isEqualTo(tarea);
    }

    @Test
    public void obtenerTareasTerminadasDataset() {
        final UsuarioRepository usuarioRepository = newUsuarioRepository();
        final TareaRepository tareaRepository = newTareaRepository();

        final List<Tarea> tareasTerminadas = tareaRepository.findTareasTerminadas(ID_USUARIO_JUAN_DATASET);

        assertThat(tareasTerminadas).hasSize(1);
        assertThat(tareasTerminadas).extractingResultOf("getTitulo").containsExactly("Renovar DNI");
    }

    @Test
    public void obtenerTareasTerminadasUsuarioInexistente() {
        final UsuarioRepository usuarioRepository = newUsuarioRepository();
        final TareaRepository tareaRepository = newTareaRepository();

        final List<Tarea> tareasTerminadas = tareaRepository.findTareasTerminadas(0L);

        assertThat(tareasTerminadas).isEmpty();
    }

    @Test
    public void obtenerTareasPendientesDataset() {
        final UsuarioRepository usuarioRepository = newUsuarioRepository();
        final TareaRepository tareaRepository = newTareaRepository();

        final List<Tarea> tareasPendientes = tareaRepository.findTareasPendientes(ID_USUARIO_JUAN_DATASET);

        assertThat(tareasPendientes).hasSize(1);
        assertThat(tareasPendientes).extractingResultOf("getTitulo").containsExactly("Práctica 1 MADS");
    }

    @Test
    public void obtenerTareasPendientesUsuarioInexistente() {
        final UsuarioRepository usuarioRepository = newUsuarioRepository();
        final TareaRepository tareaRepository = newTareaRepository();

        final List<Tarea> tareasPendientes = tareaRepository.findTareasPendientes(0L);

        assertThat(tareasPendientes).isEmpty();
    }
    @Test
    public void constructorPorDefectoDebeTenerFechaActual() {
        final Date fechaActual = new Date();
        final Tarea tarea = new Tarea();

        assertThat(tarea.getFechaCreacion()).isCloseTo(fechaActual, UN_MINUTO_EN_MS);
    }

    @Test
    public void constructorConParametrosDebeTenerFechaActual() throws ParseException {
        final Date fechaActual = new Date();
        final Date fechaInicio = DateUtils.parseDate("30/12/2017 13:00", "dd/MM/yyyy HH:mm");
        final Date fechaLimite = DateUtils.parseDate("31/12/2017 13:00", "dd/MM/yyyy HH:mm");
        final Usuario usuario = new Usuario("login", "email@example.com");
        final Tarea tarea = new Tarea(usuario, "titulo", "cuerpo", fechaInicio, fechaLimite);

        assertThat(tarea.getFechaCreacion()).isCloseTo(fechaActual, UN_MINUTO_EN_MS);
        assertThat(tarea.getFechaInicio()).isCloseTo(fechaInicio, UN_MINUTO_EN_MS);
        assertThat(tarea.getFechaLimite()).isCloseTo(fechaLimite, UN_MINUTO_EN_MS);
        assertThat(tarea.getFechaInicio()).isBefore(tarea.getFechaLimite());
    }

    @Test
    public void tareaEqualsConFechasEsIgual() throws ParseException {
        final Usuario usuario = new Usuario("login", "email@example.com");
        final Date fechaInicio = DateUtils.parseDate("30/12/2017 13:00", "dd/MM/yyyy HH:mm");
        final Date fechaLimite = DateUtils.parseDate("31/12/2017 13:00", "dd/MM/yyyy HH:mm");
        final Tarea tarea = new Tarea(usuario, "titulo", "cuerpo", fechaInicio, fechaLimite);
        //Si la ejecución es muy lenta podría darse el caso de que este test fallara (muy raro)
        final Tarea otraTarea = new Tarea(usuario, "titulo", "cuerpo", fechaInicio, fechaLimite);

        assertThat(tarea).isEqualTo(otraTarea);
    }

    /*
    Hemos comentado este método ya que es necesaria una dependencia
    y a la hora de probar cambios en la UA no nos deja descargarla

    @Test
    @PrepareOnlyThisForTest(Tarea.class)
    public void tareaEqualsConDiferenciaEntreFechasDeCreacionNoEsIgual() throws Exception {
        final Usuario usuario = new Usuario("login", "email@example.com");
        final Tarea tarea = new Tarea(usuario, "titulo", "cuerpo", FIN_DE_ANYO);

        PowerMockito.mockStatic(Tarea.class);
        Mockito.when(Tarea.getCurrentDate()).thenReturn(DateUtils.addMinutes(tarea.getFechaCreacion(), 1));
        final Tarea otraTarea = new Tarea(usuario, "titulo", "cuerpo", FIN_DE_ANYO);

        assertThat(tarea).isNotEqualTo(otraTarea);
    }
    */
}
