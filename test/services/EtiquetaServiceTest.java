package services;

import common.DBSetup;
import models.Etiqueta;
import models.Usuario;
import models.Tarea;
import models.Tablero;
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
import static org.junit.Assert.*;

public class EtiquetaServiceTest {
    static private final String TAREA_NO_EXISTENTE = "Tarea no existente";
    static private final String ETIQUETA_NO_EXISTENTE = "Etiqueta no existente";
    private static final Date FECHA_LIMITE_EJEMPLO = new GregorianCalendar(2018, Calendar.JANUARY, 1, 12, 30).getTime();
    private static final Date FECHA_INICIO_EJEMPLO = new GregorianCalendar(2017, Calendar.DECEMBER, 31, 23, 30).getTime();
    private static final String LOOKUP_DB_NAME = "DBTest";
    private static Injector injector;

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

    private EtiquetaService newEtiquetaService() {
        return injector.instanceOf(EtiquetaService.class);
    }

    private UsuarioService newUsuarioService() {
        return injector.instanceOf(UsuarioService.class);
    }

    private TareaService newTareaService() {
        return injector.instanceOf(TareaService.class);
    }

    private TableroService newTableroService() {
        return injector.instanceOf(TableroService.class);
    }

    private void assertThatEtiquetaServiceExceptionHasExpectedMessage(Throwable thrown, String expectedMessage) {
        assertThat(thrown).isInstanceOf(EtiquetaServiceException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void allEtiquetasUsuarioEstanOrdenadas() {
        EtiquetaService etiquetaService = newEtiquetaService();
        List<Etiqueta> etiquetas = etiquetaService.allEtiquetasUsuario(1000L);
        assertEquals("Deportes", etiquetas.get(0).getTitulo());
        assertEquals("MADS", etiquetas.get(1).getTitulo());
        assertEquals("Compras de navidad", etiquetas.get(2).getTitulo());
    }

    @Test(expected = EtiquetaServiceException.class)
    public void findAllEtiquetasUsuarioNoExisteLanzaExcepcion() {
        EtiquetaService etiquetaService = newEtiquetaService();
        etiquetaService.allEtiquetasUsuario(1001L);
    }

    @Test
    public void obtenerEtiquetaNoExisteLanzaExcepcion() {
        EtiquetaService etiquetaService = newEtiquetaService();
        Etiqueta etiqueta = etiquetaService.obtenerEtiqueta(0L);
        assertNull(etiqueta);
    }

    @Test
    public void obtenerEtiqueta() {
        EtiquetaService etiquetaService = newEtiquetaService();
        Etiqueta etiqueta = etiquetaService.obtenerEtiqueta(1001L);
        assertNotNull(etiqueta);
        assertEquals("MADS", etiqueta.getTitulo());
        assertEquals("#e99695", etiqueta.getColor());
    }

    @Test
    public void nuevaEtiquetaUsuario() {
        EtiquetaService etiquetaService = newEtiquetaService();
        long idUsuario = 1000L;
        final String titulo = "Emails a enviar";
        final String color = "#e99695";
        final Etiqueta etiqueta = etiquetaService.nuevaEtiqueta(idUsuario, titulo, color, null);

        assertEquals(4, etiquetaService.allEtiquetasUsuario(1000L).size());
        assertEquals(titulo, etiqueta.getTitulo());
        assertEquals(color, etiqueta.getColor());
    }

    @Test(expected = EtiquetaServiceException.class)
    public void nuevaEtiquetaSinTituloUsuarioLanzaExcepcion() {
        EtiquetaService etiquetaService = newEtiquetaService();
        long idUsuario = 1000L;
        final String titulo = "";
        final String color = "#e99695";
        final Etiqueta etiqueta = etiquetaService.nuevaEtiqueta(idUsuario, titulo, color, null);
    }

    @Test(expected = EtiquetaServiceException.class)
    public void nuevaEtiquetaUsuarioNoExisteLanzaExcepcion() {
        EtiquetaService etiquetaService = newEtiquetaService();
        long idUsuario = 0L;
        final String titulo = "Emails a enviar";
        final String color = "#e99695";
        final Etiqueta etiqueta = etiquetaService.nuevaEtiqueta(idUsuario, titulo, color, null);
    }

    @Test(expected = EtiquetaServiceException.class)
    public void nuevaEtiquetaUsuarioTituloYaExisteLanzaExcepcion() {
        EtiquetaService etiquetaService = newEtiquetaService();
        long idUsuario = 1000L;
        final String titulo = "Deportes";
        final String color = "#e99695";
        final Etiqueta etiqueta = etiquetaService.nuevaEtiqueta(idUsuario, titulo, color, null);
    }

    @Test
    public void modificacionEtiqueta() {
        final EtiquetaService etiquetaService = newEtiquetaService();
        final long idEtiqueta = 1000L;
        final String nuevoTitulo = "Deportes extremos";
        final String nuevoColor = "#0e8a16";
        etiquetaService.modificaEtiqueta(idEtiqueta, nuevoTitulo, nuevoColor, null);
        Etiqueta etiqueta = etiquetaService.obtenerEtiqueta(idEtiqueta);
        assertEquals(nuevoTitulo, etiqueta.getTitulo());
        assertEquals(nuevoColor, etiqueta.getColor());
    }

    @Test(expected = EtiquetaServiceException.class)
    public void modificacionEtiquetaSinColorLanzaExcepcion() {
        final EtiquetaService etiquetaService = newEtiquetaService();
        final long idEtiqueta = 1000L;
        final String nuevoTitulo = "Deportes extremos";
        final String nuevoColor = "";
        etiquetaService.modificaEtiqueta(idEtiqueta, nuevoTitulo, nuevoColor, null);
    }

    @Test(expected = EtiquetaServiceException.class)
    public void modificacionEtiquetaSinTituloLanzaExcepcion() {
        final EtiquetaService etiquetaService = newEtiquetaService();
        final long idEtiqueta = 1000L;
        final String nuevoTitulo = "";
        final String nuevoColor = "#0e8a16";
        etiquetaService.modificaEtiqueta(idEtiqueta, nuevoTitulo, nuevoColor, null);
    }

    @Test(expected = EtiquetaServiceException.class)
    public void modificacionEtiquetaNoExisteLanzaExcepcion() {
        final EtiquetaService etiquetaService = newEtiquetaService();
        final long idEtiqueta = 0L;
        final String nuevoTitulo = "Deportes extremos";
        final String nuevoColor = "#0e8a16";
        etiquetaService.modificaEtiqueta(idEtiqueta, nuevoTitulo, nuevoColor, null);
    }

    @Test
    public void modificacionEtiquetaUsuarioMismoTituloMismaEtiqueta() {
        final EtiquetaService etiquetaService = newEtiquetaService();
        final long idEtiqueta = 1000L;
        final String nuevoTitulo = "Deportes";
        final String nuevoColor = "#0e8a16";
        etiquetaService.modificaEtiqueta(idEtiqueta, nuevoTitulo, nuevoColor, null);
        Etiqueta etiqueta = etiquetaService.obtenerEtiqueta(idEtiqueta);
        assertEquals(nuevoTitulo, etiqueta.getTitulo());
        assertEquals(nuevoColor, etiqueta.getColor());
    }

    @Test(expected = EtiquetaServiceException.class)
    public void modificacionEtiquetaUsuarioMismoTituloDistintaEtiqueta() {
        final EtiquetaService etiquetaService = newEtiquetaService();
        final long idEtiqueta = 1001L;
        final String nuevoTitulo = "Deportes";
        final String nuevoColor = "#0e8a16";
        etiquetaService.modificaEtiqueta(idEtiqueta, nuevoTitulo, nuevoColor, null);
    }

    @Test
    public void borradoEtiqueta() {
        EtiquetaService etiquetaService = newEtiquetaService();
        long idEtiqueta = 1000L;
        etiquetaService.borraEtiqueta(idEtiqueta);
        assertNull(etiquetaService.obtenerEtiqueta(idEtiqueta));
    }

    @Test(expected = EtiquetaServiceException.class)
    public void borradoEtiquetaNoExiste() {
        EtiquetaService etiquetaService = newEtiquetaService();
        long idEtiqueta = 0L;
        etiquetaService.borraEtiqueta(idEtiqueta);
    }

    @Test
    public void findEtiquetaPorTituloConUsuario() {
        EtiquetaService etiquetaService = newEtiquetaService();
        final long idUsuario = 1000L;
        final String titulo = "Deportes";
        Etiqueta etiqueta = etiquetaService.findEtiquetaPorTituloConUsuario(titulo, idUsuario);
        assertNotNull(etiqueta);
        assertEquals("#fbca04", etiqueta.getColor());
        assertEquals("juangutierrez", etiqueta.getUsuario().getLogin());

        final long idUsuario2 = 1002L;
        Etiqueta etiqueta2 = etiquetaService.findEtiquetaPorTituloConUsuario(titulo, idUsuario2);
        assertNotNull(etiqueta);
        assertEquals("#0e8a16", etiqueta2.getColor());
        assertEquals("juangutierrez2", etiqueta2.getUsuario().getLogin());
    }

    @Test
    public void testAddTareaAEtiquetaEsCorrecto() {
        TareaService tareaService = newTareaService();
        EtiquetaService etiquetaService = newEtiquetaService();
        Long idEtiqueta = 1000L;
        Long idUsuario = 1000L;
        Etiqueta etiqueta = etiquetaService.obtenerEtiqueta(idEtiqueta);
        String tituloTarea1 = "Práctica 1 FC";
        String tituloTarea2 = "Práctica 1 EC";
        String tituloTarea3 = "Práctica 1 P1";
        String cuerpoTarea = "# Pruebas añadiendo tareas a etiqueta";

        Tarea tarea1 = tareaService.nuevaTarea(idUsuario, tituloTarea1, cuerpoTarea, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);
        etiqueta = etiquetaService.addTareaAEtiqueta(idEtiqueta, tarea1.getId());
        tarea1 = tareaService.obtenerTarea(tarea1.getId());

        Tarea tarea2 = tareaService.nuevaTarea(idUsuario, tituloTarea2, cuerpoTarea, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);
        etiqueta = etiquetaService.addTareaAEtiqueta(idEtiqueta, tarea2.getId());
        tarea2 = tareaService.obtenerTarea(tarea2.getId());

        Tarea tarea3 = tareaService.nuevaTarea(idUsuario, tituloTarea3, cuerpoTarea, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);
        etiqueta = etiquetaService.addTareaAEtiqueta(idEtiqueta, tarea3.getId());
        tarea3 = tareaService.obtenerTarea(tarea3.getId());

        assertEquals(3, etiqueta.getTareas().size());
        assertTrue(etiqueta.getTareas().contains(tarea1));
        assertTrue(etiqueta.getTareas().contains(tarea2));
        assertTrue(etiqueta.getTareas().contains(tarea3));
        assertEquals("Deportes", tarea1.getEtiqueta().getTitulo());
        assertEquals("Deportes", tarea2.getEtiqueta().getTitulo());
        assertEquals("Deportes", tarea3.getEtiqueta().getTitulo());
    }

    @Test
    public void testAddTareaAEtiqueta_EtiquetaNoExiste() {
        final TareaService tareaService = newTareaService();
        final EtiquetaService etiquetaService = newEtiquetaService();
        Long idEtiqueta = 0L;
        Long idUsuario = 1000L;
        String tituloTarea1 = "Práctica 1 FC";
        String cuerpoTarea = "# Pruebas añadiendo tareas a etiqueta";
        Tarea tarea1 = tareaService.nuevaTarea(idUsuario, tituloTarea1, cuerpoTarea, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);

        final Throwable thrown = catchThrowable(() -> etiquetaService.addTareaAEtiqueta(idEtiqueta, tarea1.getId()));
        assertThatEtiquetaServiceExceptionHasExpectedMessage(thrown, ETIQUETA_NO_EXISTENTE);
    }

    @Test
    public void testAddTareaAEtiqueta_TareaNoExiste() {
        final TareaService tareaService = newTareaService();
        final EtiquetaService etiquetaService = newEtiquetaService();
        Long idEtiqueta = 1000L;
        Long idTarea = 0L;

        final Throwable thrown = catchThrowable(() -> etiquetaService.addTareaAEtiqueta(idEtiqueta, idTarea));
        assertThatEtiquetaServiceExceptionHasExpectedMessage(thrown, TAREA_NO_EXISTENTE);
    }

    @Test
    public void allEtiquetasTableroEstanOrdenadas() {
        EtiquetaService etiquetaService = newEtiquetaService();
        TableroService tableroService = newTableroService();
        Tablero tablero = tableroService.addEtiquetaATablero(1002L, 1004L);
        tablero = tableroService.addEtiquetaATablero(1002L, 1005L);
        List<Etiqueta> etiquetas = etiquetaService.allEtiquetasTablero(1002L);
        assertEquals("Etiqueta 1 prueba tablero", etiquetas.get(0).getTitulo());
        assertEquals("Etiqueta 2 prueba tablero", etiquetas.get(1).getTitulo());
    }
}
