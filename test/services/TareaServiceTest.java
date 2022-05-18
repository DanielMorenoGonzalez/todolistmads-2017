package services;

import common.DBSetup;
import models.Tablero;
import models.Tarea;
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
import static org.junit.Assert.*;

public class TareaServiceTest {
    private static final String LOOKUP_DB_NAME = "DBTest";
    private static final Date FECHA_LIMITE_EJEMPLO = new GregorianCalendar(2018, Calendar.JANUARY, 1, 12, 30).getTime();
    private static final Date FECHA_INICIO_EJEMPLO = new GregorianCalendar(2017, Calendar.DECEMBER, 31, 23, 30).getTime();
    private static final String USUARIO_NO_EXISTENTE_ERROR = "Usuario no existente";
    private static final String TABLERO_NO_EXISTENTE_ERROR = "Tablero no existente";
    private static final String TAREA_NO_EXISTENTE_ERROR = "Tarea no existente";
    private static final String TAREA_YA_TERMINADA_ERROR = "Tarea ya terminada";
    private static final String TAREA_YA_RESTAURADA_ERROR = "Tarea ya restaurada";
    private static final String RANGO_FECHAS_ERROR = "Fecha límite anterior a la de inicio";
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

    private TareaService newTareaService() {
        return injector.instanceOf(TareaService.class);
    }

    private UsuarioService newUsuarioService() {
        return injector.instanceOf(UsuarioService.class);
    }

    private TableroService newTableroService() {
        return injector.instanceOf(TableroService.class);
    }

    // Test #19: allTareasUsuarioEstanOrdenadas
    @Test
    public void allTareasUsuarioEstanOrdenadas() {
        TareaService tareaService = newTareaService();
        List<Tarea> tareas = tareaService.allTareasUsuario(1000L);
        assertEquals("Renovar DNI", tareas.get(0).getTitulo());
        assertEquals("Práctica 1 MADS", tareas.get(1).getTitulo());
    }

    // Test #20: exceptionSiUsuarioNoExisteRecuperandoSusTareas
    @Test(expected = TareaServiceException.class)
    public void crearNuevoUsuarioLoginRepetidoLanzaExcepcion() {
        TareaService tareaService = newTareaService();
        tareaService.allTareasUsuario(1001L);
    }

    // Test #21: nuevaTareaUsuario
    @Test
    public void nuevaTareaUsuario() {
        TareaService tareaService = newTareaService();
        long idUsuario = 1000L;
        final String titulo = "Pagar el alquiler";
        final String cuerpo = "";
        final Tarea tarea = tareaService.nuevaTarea(idUsuario, titulo, cuerpo, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);
        assertEquals(3, tareaService.allTareasUsuario(1000L).size());
        assertEquals(titulo, tarea.getTitulo());
        assertEquals(cuerpo, tarea.getCuerpo());
        assertEquals(FECHA_LIMITE_EJEMPLO, tarea.getFechaLimite());
        assertEquals(FECHA_INICIO_EJEMPLO, tarea.getFechaInicio());
    }

    // Test #22: modificación de tareas
    @Test
    public void modificacionTarea() {
        final TareaService tareaService = newTareaService();
        final long idTarea = 1000L;
        final String nuevoTitulo = "Pagar el alquiler";
        final String nuevoCuerpo = "Hay que pagarlo antes del 10";
        tareaService.modificaTarea(idTarea, nuevoTitulo, nuevoCuerpo, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);
        Tarea tarea = tareaService.obtenerTarea(idTarea);
        assertEquals(nuevoTitulo, tarea.getTitulo());
        assertEquals(nuevoCuerpo, tarea.getCuerpo());
        assertEquals(FECHA_INICIO_EJEMPLO, tarea.getFechaInicio());
        assertEquals(FECHA_LIMITE_EJEMPLO, tarea.getFechaLimite());
    }

    // Test #23: borrado tarea
    @Test
    public void borradoTarea() {
        TareaService tareaService = newTareaService();
        long idTarea = 1000L;
        tareaService.borraTarea(idTarea);
        assertNull(tareaService.obtenerTarea(idTarea));
    }

    @Test
    public void obtenerTareasPendientesDataset() {
        final TareaService tareaService = newTareaService();

        final List<Tarea> tareasPendientes = tareaService.tareasPendientesUsuario(1000L);

        assertThat(tareasPendientes).hasSize(1);
        assertThat(tareasPendientes).extractingResultOf("getTitulo").containsExactly("Práctica 1 MADS");
    }

    @Test
    public void obtenerTareasPendientesUsuarioInexistente() {
        final TareaService tareaService = newTareaService();

        final Throwable thrown = catchThrowable(() -> tareaService.tareasPendientesUsuario(0L));

        assertThatTareaServiceExceptionHasExpectedMessage(thrown, USUARIO_NO_EXISTENTE_ERROR);
    }

    @Test
    public void obtenerTareasTerminadasDataset() {
        final TareaService tareaService = newTareaService();

        final List<Tarea> tareasTerminadas = tareaService.tareasTerminadasUsuario(1000L);

        assertThat(tareasTerminadas).hasSize(1);
        assertThat(tareasTerminadas).extractingResultOf("getTitulo").containsExactly("Renovar DNI");
    }

    @Test
    public void obtenerTareasTerminadasUsuarioInexistente() {
        final TareaService tareaService = newTareaService();

        final Throwable thrown = catchThrowable(() -> tareaService.tareasTerminadasUsuario(0L));

        assertThatTareaServiceExceptionHasExpectedMessage(thrown, USUARIO_NO_EXISTENTE_ERROR);
    }

    @Test
    public void terminarTareaTieneQueTerminarLaTarea() {
        final TareaService tareaService = newTareaService();

        final Tarea tarea = tareaService.terminarTarea(1001L);

        assertThat(tarea).hasFieldOrPropertyWithValue("terminada", true);
    }

    @Test
    public void terminarTareaTerminadaDaError() {
        final TareaService tareaService = newTareaService();

        final Throwable throwable = catchThrowable(() -> tareaService.terminarTarea(1000L));

        assertThatTareaServiceExceptionHasExpectedMessage(throwable, TAREA_YA_TERMINADA_ERROR);
    }

    @Test
    public void terminarTareaInexistenteDaError() {
        final TareaService tareaService = newTareaService();

        final Throwable throwable = catchThrowable(() -> tareaService.terminarTarea(0L));

        assertThatTareaServiceExceptionHasExpectedMessage(throwable, TAREA_NO_EXISTENTE_ERROR);
    }

    @Test(expected = TareaServiceException.class)
    public void nuevaTareaTituloNuloLanzaExcepcion() {
        TareaService tareaService = newTareaService();
        long idUsuario = 1000L;
        String tituloTarea = "";
        String cuerpoTarea = "Este es el cuerpo de la tarea";
        tareaService.nuevaTarea(idUsuario, tituloTarea, cuerpoTarea, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);
    }

    @Test
    public void nuevaTareaPermiteTituloRepetido() {
        TareaService tareaService = newTareaService();
        UsuarioService usuarioService = newUsuarioService();
        Usuario usuario = usuarioService.findUsuarioPorId(1000L);
        String tituloTarea = "Práctica 1 MADS";
        String cuerpoTarea = "# El título está repetido";
        Tarea tarea = tareaService.nuevaTarea(usuario.getId(), tituloTarea, cuerpoTarea, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);

        assertNotNull(tarea);
        assertEquals(2, usuario.getTareas().size());
    }

    @Test
    public void modificacionTareaTituloNuloAsignaTituloAnterior() {
        final TareaService tareaService = newTareaService();
        final long idTarea = 1000L;
        final String tituloTareaModificado = "";
        final String cuerpoModificado = "Tengo que renovar mi DNI antes del jueves";
        tareaService.modificaTarea(idTarea, tituloTareaModificado, cuerpoModificado, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);
        Tarea tarea = tareaService.obtenerTarea(idTarea);
        assertEquals("Renovar DNI", tarea.getTitulo());
        assertEquals(cuerpoModificado, tarea.getCuerpo());
        assertEquals(FECHA_INICIO_EJEMPLO, tarea.getFechaInicio());
        assertEquals(FECHA_LIMITE_EJEMPLO, tarea.getFechaLimite());
    }

    @Test
    public void tareasPendientesEnTableroEstanOrdenadas() {
        TareaService tareaService = newTareaService();
        TableroService tableroService = newTableroService();
        Tablero tablero = tableroService.findTableroByIdOrThrow(1000L);
        tablero = tableroService.addTareaATablero(1000L, 1001L);
        tablero = tableroService.addTareaATablero(1000L, 1002L);

        List<Tarea> tareas = tareaService.tareasPendientesEnTablero(1000L);
        assertEquals("Práctica 1 MADS", tareas.get(0).getTitulo());
        assertEquals("Práctica 2 MADS", tareas.get(1).getTitulo());
    }

    @Test
    public void tareasPendientesEnTablero_TableroNoExiste() {
        TareaService tareaService = newTareaService();

        final Throwable throwable = catchThrowable(() -> tareaService.tareasPendientesEnTablero(0L));

        assertThatTareaServiceExceptionHasExpectedMessage(throwable, TABLERO_NO_EXISTENTE_ERROR);
    }

    @Test
    public void tareasTerminadasEnTableroEstanOrdenadas() {
        TareaService tareaService = newTareaService();
        TableroService tableroService = newTableroService();
        Tablero tablero = tableroService.findTableroByIdOrThrow(1000L);
        Tarea tarea1 = tareaService.obtenerTarea(1001L);
        tarea1 = tareaService.terminarTarea(tarea1.getId());
        Tarea tarea2 = tareaService.obtenerTarea(1002L);
        tarea2 = tareaService.terminarTarea(tarea2.getId());
        Tarea tarea3 = tareaService.obtenerTarea(1000L);
        tablero = tableroService.addTareaATablero(1000L, tarea1.getId());
        tablero = tableroService.addTareaATablero(1000L, tarea2.getId());
        tablero = tableroService.addTareaATablero(1000L, tarea3.getId());

        List<Tarea> tareas = tareaService.tareasTerminadasEnTablero(1000L);
        assertEquals("Renovar DNI", tareas.get(0).getTitulo());
        assertEquals("Práctica 1 MADS", tareas.get(1).getTitulo());
        assertEquals("Práctica 2 MADS", tareas.get(2).getTitulo());
    }

    @Test
    public void tareasTerminadasEnTablero_TableroNoExiste() {
        TareaService tareaService = newTareaService();

        final Throwable throwable = catchThrowable(() -> tareaService.tareasTerminadasEnTablero(0L));

        assertThatTareaServiceExceptionHasExpectedMessage(throwable, TABLERO_NO_EXISTENTE_ERROR);
    }

    private void assertThatTareaServiceExceptionHasExpectedMessage(Throwable thrown, String expectedMessage) {
        assertThat(thrown).isInstanceOf(TareaServiceException.class)
            .hasMessage(expectedMessage);
    }


    // Tests para restaurar tarea
    @Test
    public void restaurarTareaTieneQueRestaurarLaTarea() {
        final TareaService tareaService = newTareaService();

        tareaService.terminarTarea(1001L);
        final Tarea tarea = tareaService.restaurarTarea(1001L);

        assertThat(tarea).hasFieldOrPropertyWithValue("terminada", false);

    }

    @Test
    public void restaurarTareaEnPendientesDaError() {
        final TareaService tareaService = newTareaService();

        final Throwable throwable = catchThrowable(() -> tareaService.restaurarTarea(1002L));

        assertThatTareaServiceExceptionHasExpectedMessage(throwable, TAREA_YA_RESTAURADA_ERROR);
    }

    @Test
    public void restaurarTareaInexistenteDaError() {
        final TareaService tareaService = newTareaService();

        final Throwable throwable = catchThrowable(() -> tareaService.restaurarTarea(0L));

        assertThatTareaServiceExceptionHasExpectedMessage(throwable, TAREA_NO_EXISTENTE_ERROR);
    }

    @Test
    public void nuevaTareaFechasInvertidasDaError() {
        TareaService tareaService = newTareaService();
        UsuarioService usuarioService = newUsuarioService();
        Usuario usuario = usuarioService.findUsuarioPorId(1000L);
        String tituloTarea = "Práctica 1 MADS";
        String cuerpoTarea = "# El título está repetido";

        final Throwable thrown = catchThrowable(() ->
            tareaService.nuevaTarea(usuario.getId(), tituloTarea, cuerpoTarea, FECHA_LIMITE_EJEMPLO, FECHA_INICIO_EJEMPLO));

        assertThatTareaServiceExceptionHasExpectedMessage(thrown, RANGO_FECHAS_ERROR);
    }

    @Test
    public void modificarTareaFechasInvertidasDaError() {
        final TareaService tareaService = newTareaService();
        final long idTarea = 1000L;
        final String tituloTareaModificado = "";
        final String cuerpoModificado = "Tengo que renovar mi DNI antes del jueves";

        final Throwable thrown = catchThrowable(() ->
            tareaService.modificaTarea(idTarea, tituloTareaModificado, cuerpoModificado, FECHA_LIMITE_EJEMPLO, FECHA_INICIO_EJEMPLO));

        assertThatTareaServiceExceptionHasExpectedMessage(thrown, RANGO_FECHAS_ERROR);
    }
}
