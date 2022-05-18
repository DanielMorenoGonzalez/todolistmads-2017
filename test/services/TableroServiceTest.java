package services;

import common.DBSetup;
import models.Tablero;
import models.Tarea;
import models.Usuario;
import models.Etiqueta;
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

public class TableroServiceTest {
    static private final String USUARIO_NO_EXISTENTE = "Usuario no existente";
    static private final String TABLERO_NO_EXISTENTE = "Tablero no existente";
    static private final String TAREA_NO_EXISTENTE = "Tarea no existente";
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

    private TareaService newTareaService() {
        return injector.instanceOf(TareaService.class);
    }

    private EtiquetaService newEtiquetaService() {
        return injector.instanceOf(EtiquetaService.class);
    }

    @Test
    public void crearTablero_NombreNormal_UsuarioExistente_TableroOK() {
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idUsuario = 1000L;
        final String nombreTablero = "Tablero de prueba";

        Tablero tablero = tableroService.crearTablero(idUsuario, nombreTablero, false); // false como que no será privado

        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
        assertThat(tablero.getNombre()).as("Nombre del tablero").isEqualTo(nombreTablero);
        assertThat(tablero.getAdministrador()).as("Administrador del tablero").isEqualTo(usuario);
    }

    @Test
    public void crearTablero_NombreNormal_UsuarioInexistente_TableroNoCreado() {
        final TableroService tableroService = newTableroService();
        final long idUsuarioInexistente = 1001L;
        final String nombreTablero = "Tablero de prueba";

        final Throwable thrown = catchThrowable(() -> tableroService.crearTablero(idUsuarioInexistente, nombreTablero, false));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, USUARIO_NO_EXISTENTE);
    }

    @Test
    public void allTablerosAdministradosUsuario_UsuarioConTableros_ListaTablerosOK() {
        final TableroService tableroService = newTableroService();
        final long idUsuario = 1000L;

        final List<Tablero> tablerosAdministrados = tableroService.allTablerosAdministradosUsuario(idUsuario);

        // Comprobar que los tableros del usuario, aplicándoles el método "getNombre", contengan exactamente esos dos
        // nombres
        assertThat(tablerosAdministrados).extractingResultOf("getNombre")
                .containsExactly("Tablero test 1", "Tablero test 2");
    }

    @Test
    public void allTablerosAdministradosUsuario_UsuarioSinTableros_ListaVacia() {
        final TableroService tableroService = newTableroService();
        final long idUsuario = 1002L;

        final List<Tablero> tablerosAdministrados = tableroService.allTablerosAdministradosUsuario(idUsuario);

        assertThat(tablerosAdministrados).isEmpty();
    }

    @Test
    public void allTablerosAdministradosUsuario_UsuarioInexistente_excepcionUsuarioNoEncontrado() {
        final TableroService tableroService = newTableroService();
        final long idUsuario = 1001L;

        final Throwable thrown = catchThrowable(() -> tableroService.allTablerosAdministradosUsuario(idUsuario));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, USUARIO_NO_EXISTENTE);
    }

    @Test
    public void apuntarUsuarioATablero_UsuarioNoApuntado_TableroSinApuntados_UsuarioApuntadoYTableroConUsuario() {
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idUsuarioNoApuntado = 1002L;
        final long idTableroSinApuntados = 1001L;

        final Tablero tablero = tableroService.apuntarUsuarioATablero(idUsuarioNoApuntado, idTableroSinApuntados);
        //Ojito, hay que cogerlo después, que la relación no se actualiza sola
        final Usuario usuario = usuarioService.findUsuarioPorId(idUsuarioNoApuntado);

        assertThat(tablero.getParticipantes()).as("Usuario participante en el tablero").containsExactly(usuario);
        assertThat(usuario.getTableros()).as("Tableros en los que participa el usuario").containsExactly(tablero);
    }

    @Test
    public void apuntarUsuarioATablero_UsuarioConParticipacionEnTablero_TableroConUsuarioApuntado_ExcepcionUsuarioYaApuntado() {
        final TableroService tableroService = newTableroService();
        final long idUsuarioConParticipacionEnTablero = 1003L;
        final long idTableroConUsuarioApuntado = 1000L;

        final Throwable thrown = catchThrowable(() ->
                tableroService.apuntarUsuarioATablero(idUsuarioConParticipacionEnTablero, idTableroConUsuarioApuntado));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, USUARIO_YA_APUNTADO);
    }

    @Test
    public void apuntarUsuarioATablero_UsuarioInexistente_TableroSinApuntados_ExcepcionUsuarioInexistente() {
        final TableroService tableroService = newTableroService();
        final long idUsuarioInexistente = 1001L;
        final long idTableroSinApuntados = 1001L;

        final Throwable thrown = catchThrowable(() ->
                tableroService.apuntarUsuarioATablero(idUsuarioInexistente, idTableroSinApuntados));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, USUARIO_NO_EXISTENTE);

    }

    @Test
    public void apuntarUsuarioATablero_UsuarioNoApuntado_TableroInexistente_ExcepcionTableroInexistente() {
        final TableroService tableroService = newTableroService();
        final long idUsuarioInexistente = 1002L;
        final long idTableroSinApuntados = 999L;

        final Throwable thrown = catchThrowable(() ->
                tableroService.apuntarUsuarioATablero(idUsuarioInexistente, idTableroSinApuntados));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, TABLERO_NO_EXISTENTE);
    }

    @Test
    public void allTablerosParticipaUsuario_UsuarioParticipandoEnTablero_TableroEnElQueParticipa() {
        final TableroService tableroService = newTableroService();
        final long idUsuarioConParticipacionEnTablero = 1003L;

        final List<Tablero> tablerosParticipa = tableroService.allTablerosParticipaUsuario(idUsuarioConParticipacionEnTablero);

        assertThat(tablerosParticipa).extractingResultOf("getNombre").containsExactly("Tablero test 1");
    }

    @Test
    public void allTablerosParticipaUsuario_UsuarioSinTableros_ListaVacia() {
        final TableroService tableroService = newTableroService();
        final long idUsuario = 1002L;

        final List<Tablero> tablerosParticipa = tableroService.allTablerosParticipaUsuario(idUsuario);

        assertThat(tablerosParticipa).isEmpty();
    }

    @Test
    public void allTablerosParticipaUsuario_UsuarioInexistente_excepcionUsuarioNoExistente() {
        final TableroService tableroService = newTableroService();
        final long idUsuario = 1001L;

        final Throwable thrown = catchThrowable(() -> tableroService.allTablerosParticipaUsuario(idUsuario));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, USUARIO_NO_EXISTENTE);
    }

    @Test
    public void restoTablerosUsuario_usuarioSinTableros_ListaTableros() {
        final TableroService tableroService = newTableroService();
        final long idUsuarioSinTableros = 1002L;

        final List<Tablero> tableros = tableroService.restoTablerosUsuario(idUsuarioSinTableros, false);

        assertThat(tableros).as("Nombres de los tableros restantes").extractingResultOf("getNombre")
                .containsExactly("Tablero test 1", "Tablero test 2");
    }

    @Test
    public void restoTablerosUsuario_usuarioInexistente_excepcionUsuarioNoExistente() {
        final TableroService tableroService = newTableroService();
        final long idUsuarioInexistente = 1001L;

        final Throwable thrown = catchThrowable(() -> tableroService.restoTablerosUsuario(idUsuarioInexistente, false));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, USUARIO_NO_EXISTENTE);
    }

    @Test
    public void restoTablerosUsuario_usuarioAdministradorDeTodosLosTableros_ListaVacia() {
        final TableroService tableroService = newTableroService();
        final long idUsuarioAdministradorDeTodosLosTableros = 1000L;

        final List<Tablero> tableros = tableroService.restoTablerosUsuario(idUsuarioAdministradorDeTodosLosTableros, false);

        assertThat(tableros).isEmpty();
    }

    @Test
    public void restoTablerosUsuario_usuarioParticipanteEnTablero_TableroRestante() {
        final TableroService tableroService = newTableroService();
        final long idUsuarioParticipanteEnTablero = 1003L;

        final List<Tablero> tableros = tableroService.restoTablerosUsuario(idUsuarioParticipanteEnTablero, false);

        assertThat(tableros).extractingResultOf("getNombre").containsExactly("Tablero test 2");
    }

    @Test
    public void tableroExisteEnAdministradosPorUsuario_tableroNoRepetido() {
        final TableroService tableroService = newTableroService();
        final long idUsuario = 1000L;

        final boolean existe = tableroService.tableroExisteEnAdministradosPorUsuario(idUsuario, "Tablero prog mads");

        assertThat(existe).isEqualTo(false);
    }

    @Test
    public void tableroExisteEnAdministradosPorUsuario_tableroRepetido() {
        final TableroService tableroService = newTableroService();
        final long idUsuario = 1000L;

        final boolean existe = tableroService.tableroExisteEnAdministradosPorUsuario(idUsuario, "Tablero test 1");

        assertThat(existe).isEqualTo(true);
    }

    @Test
    public void nuevoTablero_deUsuarioAdministrador_yaExistente_lanzaExcepcion() {
        final TableroService tableroService = newTableroService();
        final long idUsuario = 1000L;

        final Throwable thrown = catchThrowable(() -> tableroService.crearTablero(idUsuario, "Tablero test 1",false));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, TABLERO_YA_EXISTENTE);
    }

    @Test
    public void findTableroPorNombre() {
        final TableroService tableroService = newTableroService();
        final Tablero tablero = tableroService.findTableroPorNombre("Tablero test 1");

        assertThat(tablero.getId()).as("ID del tablero").isEqualTo((Long)1000L);
    }

    @Test
    public void nuevoTablero_nombreTableroNulo_lanzaExcepcion() {
        final TableroService tableroService = newTableroService();
        final long idUsuario = 1002L;
        final String nombreTablero = "";

        final Throwable thrown = catchThrowable(() -> tableroService.crearTablero(idUsuario, nombreTablero,false));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, NOMBRE_TABLERO_NULO);
    }

    @Test
    public void testAddTareaATableroEsCorrecto() {
        final TareaService tareaService = newTareaService();
        final TableroService tableroService = newTableroService();
        Long idTablero = 1000L;
        Long idUsuario = 1000L;
        Tablero tablero = tableroService.findTableroByIdOrThrow(idTablero);
        String tituloTarea1 = "Práctica 1 FC";
        String tituloTarea2 = "Práctica 1 EC";
        String tituloTarea3 = "Práctica 1 P1";
        String cuerpoTarea = "# Asignatura de primero";
        Tarea tarea1 = tareaService.nuevaTarea(idUsuario, tituloTarea1, cuerpoTarea, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);
        tablero = tableroService.addTareaATablero(idTablero, tarea1.getId());
        tarea1 = tareaService.obtenerTarea(tarea1.getId());
        Tarea tarea2 = tareaService.nuevaTarea(idUsuario, tituloTarea2, cuerpoTarea, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);
        tablero = tableroService.addTareaATablero(idTablero, tarea2.getId());
        tarea2 = tareaService.obtenerTarea(tarea2.getId());
        Tarea tarea3 = tareaService.nuevaTarea(idUsuario, tituloTarea3, cuerpoTarea, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);
        tablero = tableroService.addTareaATablero(idTablero, tarea3.getId());
        tarea3 = tareaService.obtenerTarea(tarea3.getId());

        assertEquals(3, tablero.getTareas().size());
        assertTrue(tablero.getTareas().contains(tarea1));
        assertTrue(tablero.getTareas().contains(tarea2));
        assertTrue(tablero.getTareas().contains(tarea3));
        assertEquals("Tablero test 1", tarea1.getTablero().getNombre());
        assertEquals("Tablero test 1", tarea2.getTablero().getNombre());
        assertEquals("Tablero test 1", tarea3.getTablero().getNombre());
    }

    @Test
    public void testAddTareaATablero_TableroNoExiste() {
        final TareaService tareaService = newTareaService();
        final TableroService tableroService = newTableroService();
        Long idTablero = 1009L;
        Long idUsuario = 1000L;
        String tituloTarea1 = "Práctica 1 FC";
        String cuerpoTarea = "# Asignatura de primero";
        Tarea tarea1 = tareaService.nuevaTarea(idUsuario, tituloTarea1, cuerpoTarea, FECHA_INICIO_EJEMPLO, FECHA_LIMITE_EJEMPLO);

        final Throwable thrown = catchThrowable(() -> tableroService.addTareaATablero(idTablero, tarea1.getId()));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, TABLERO_NO_EXISTENTE);
    }

    @Test
    public void testAddTareaATablero_TareaNoExiste() {
        final TareaService tareaService = newTareaService();
        final TableroService tableroService = newTableroService();
        Long idTablero = 1000L;
        Long idTarea = 1010L;
        final Throwable thrown = catchThrowable(() -> tableroService.addTareaATablero(idTablero, idTarea));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, TAREA_NO_EXISTENTE);
    }

    private void assertThatTableroServiceExceptionHasExpectedMessage(Throwable thrown, String expectedMessage) {
        assertThat(thrown).isInstanceOf(TableroServiceExcepion.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void testAddTableroPrivado(){
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idUsuario = 1004L; // lisa
        final String nombreTablero = "Tablero de prueba privado";

        Tablero tablero = tableroService.crearTablero(idUsuario, nombreTablero,true);


        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
        assertThat(tablero.getNombre()).as("Nombre del tablero").isEqualTo(nombreTablero);
        assertThat(tablero.getAdministrador()).as("Administrador del tablero").isEqualTo(usuario);
        assertThat(tablero.isPrivado()).isEqualTo(true);
    }

    @Test
    public void testAddTablero_PorDefectoPublico(){
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idUsuario = 1004L; // lisa
        final String nombreTablero = "Tablero de prueba privado";

        Tablero tablero = tableroService.crearTablero(idUsuario, nombreTablero,false);

        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
        assertThat(tablero.getNombre()).as("Nombre del tablero").isEqualTo(nombreTablero);
        assertThat(tablero.getAdministrador()).as("Administrador del tablero").isEqualTo(usuario);
        assertThat(tablero.isPrivado()).isEqualTo(false);
    }

    @Test
    public void testAddParticipante_PorAdmin_TableroPrivado(){
        // tablero 1002 - administrador 1005
        // participante 1004 - lisa
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idParticipante = 1003L;  // juangutierrez3
        final long idUsuario = 1005L;       // pepe
        final long idTablero = 1002L;

        Tablero tablero = tableroService.addParticipanteTablero(idTablero, idParticipante, idUsuario);
        Usuario administrador = usuarioService.findUsuarioPorId(idUsuario);
        Usuario participante = usuarioService.findUsuarioPorId(idParticipante);

        assertThat(tablero.isPrivado()).isEqualTo(true); // No tiene por qué ser privado el tablero para que el administrador añada usuarios pero en este caso lo es
        assertThat(tablero.getNombre()).as("Nombre del tablero").isEqualTo("Tablero test privado");
        assertThat(tablero.getAdministrador()).as("Administrador del tablero").isEqualTo(administrador);

        final ArrayList<Usuario> tablerosParticipantes = new ArrayList<>(tablero.getParticipantes());
        assertThat(tablerosParticipantes.get(0)).as("Participantes del tablero").isEqualTo(participante);
        assertEquals(2, tablero.getParticipantes().size()); // Sólo tienen que estar lisa y el nuevo usuario


    }

    @Test
    public void testAddParticipanteYaExistente_PorAdmin_TableroPrivado(){
        // tablero 1002 - administrador 1005
        // participante 1004 - lisa
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idParticipante = 1004L;  // lisa
        final long idUsuario = 1005L;       // pepe
        final long idTablero = 1002L;

        // Tiene que devolver null
        Tablero t = tableroService.addParticipanteTablero(idTablero, idParticipante, idUsuario);

        assertThat(t).as("Tablero").isEqualTo(null);
    }

    @Test
    public void testRemoveParticipante_PorAdmin_Tablero(){
        // tablero 1002 - administrador 1005
        // participante 1004 - lisa
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idParticipante = 1004L;  // lisa
        final long idUsuario = 1005L;       // pepe
        final long idTablero = 1002L; // ya tiene un participante - lisa

        Tablero t = tableroService.obtenerTablero(idTablero);
        assertEquals(1, t.getParticipantes().size()); // Sólo tiene que estar lisa
        Tablero tablero = tableroService.removeParticipanteTablero(idTablero, idParticipante, idUsuario);
        Usuario administrador = usuarioService.findUsuarioPorId(idUsuario);
        Usuario participante = usuarioService.findUsuarioPorId(idParticipante);

        assertThat(tablero.isPrivado()).isEqualTo(true); // No tiene por qué ser privado el tablero para que el administrador añada usuarios pero en este caso lo es
        assertThat(tablero.getNombre()).as("Nombre del tablero").isEqualTo("Tablero test privado");
        assertThat(tablero.getAdministrador()).as("Administrador del tablero").isEqualTo(administrador);

        assertEquals(0, tablero.getParticipantes().size()); // Tiene que estar vacío
    }

    @Test
    public void testRemoveParticipanteNoExistente_PorAdmin_Tablero(){
        // tablero 1002 - administrador 1005
        // participante 1004 - lisa
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idParticipante = 1000L;  // juangutierrez // Intentamos eliminar al usuario juangutierrez que no está en el tablero
        final long idUsuario = 1005L;       // pepe
        final long idTablero = 1002L;

        // Tiene que devolver null
        Tablero t = tableroService.removeParticipanteTablero(idTablero, idParticipante, idUsuario);

        assertThat(t).as("Tablero").isEqualTo(null);
    }

    @Test
    public void desapuntarUsuarioDeTablero_UsuarioApuntado_TableroConUnUsuario_UsuarioDesapuntadoYTableroVacio() {
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idUsuarioApuntado = 1004L; // lisa
        final long idTableroConUnUsuarioApuntado = 1002L;

        Usuario usuario = usuarioService.findUsuarioPorId(idUsuarioApuntado);
        assertEquals(1, usuario.getTableros().size()); // Tiene que estar apuntado a un tablero
        final Tablero tablero = tableroService.desapuntarUsuarioDeTablero(idUsuarioApuntado, idTableroConUnUsuarioApuntado);
        usuario = usuarioService.findUsuarioPorId(idUsuarioApuntado);
        assertEquals(0, usuario.getTableros().size()); // Tiene que estar vacío
        assertEquals(0, tablero.getParticipantes().size()); // Tiene que estar vacío

    }

    @Test
    public void desapuntarUsuarioDeTablero_UsuarioNoApuntado_TableroConUnUsuario_ExcepcionUsuarioInexistente() {
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idUsuarioNoExistente = 1006L;
        final long idTableroConUnUsuarioApuntado = 1002L;

        final Throwable thrown = catchThrowable(() ->
                tableroService.desapuntarUsuarioDeTablero(idUsuarioNoExistente, idTableroConUnUsuarioApuntado));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, USUARIO_NO_EXISTENTE);
    }

    @Test
    public void desapuntarUsuarioDeTablero_UsuarioNoApuntado_TableroConUnUsuario_ExcepcionUsuarioNoApuntado() {
        final TableroService tableroService = newTableroService();
        final UsuarioService usuarioService = newUsuarioService();
        final long idUsuarioNoApuntado = 1005L; // pepe
        final long idTableroConUnUsuarioApuntado = 1002L;

        final Throwable thrown = catchThrowable(() ->
                tableroService.desapuntarUsuarioDeTablero(idUsuarioNoApuntado, idTableroConUnUsuarioApuntado));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, USUARIO_NO_APUNTADO);
    }

    @Test
    public void testListarParticipantesTablero() {
        final UsuarioService usuarioService = newUsuarioService();
        final TableroService tableroService = newTableroService();
        Usuario usuario1 = usuarioService.findUsuarioPorId(999L);
        Usuario usuario2 = usuarioService.findUsuarioPorId(1004L);
        Usuario usuario3 = usuarioService.findUsuarioPorId(1000L);
        Tablero tablero = tableroService.obtenerTablero(1001L);
        tableroService.apuntarUsuarioATablero(usuario1.getId(), tablero.getId());
        tableroService.apuntarUsuarioATablero(usuario2.getId(), tablero.getId());
        tableroService.apuntarUsuarioATablero(usuario3.getId(), tablero.getId());
        List<Usuario> participantes = tableroService.listarParticipantesTablero(tablero.getId());
        assertEquals("juangutierrez", participantes.get(0).getLogin());
        assertEquals("lisa", participantes.get(1).getLogin());
        assertEquals("pove", participantes.get(2).getLogin());
    }

    @Test
    public void testListarParticipantesTableroNoExisteLanzaExcepcion() {
        final UsuarioService usuarioService = newUsuarioService();
        final TableroService tableroService = newTableroService();

        final Throwable thrown = catchThrowable(() ->
                tableroService.listarParticipantesTablero(0L));

        assertThatTableroServiceExceptionHasExpectedMessage(thrown, TABLERO_NO_EXISTENTE);
    }

    @Test
    public void testAddEtiquetaATablero() {
        EtiquetaService etiquetaService = newEtiquetaService();
        TableroService tableroService = newTableroService();
        long idUsuario = 1004L;
        long idTablero = 1002L;
        Tablero tablero = tableroService.obtenerTablero(idTablero);
        final String titulo = "Emails a enviar";
        final String color = "#e99695";

        Etiqueta etiqueta = etiquetaService.nuevaEtiqueta(idUsuario, titulo, color, tablero);
        tablero = tableroService.addEtiquetaATablero(idTablero, etiqueta.getId());
        etiqueta = etiquetaService.obtenerEtiqueta(etiqueta.getId());

        Etiqueta etiqueta2 = etiquetaService.nuevaEtiqueta(idUsuario, "Deportes guays", "#e99695", tablero);
        tablero = tableroService.addEtiquetaATablero(idTablero, etiqueta2.getId());
        etiqueta2 = etiquetaService.obtenerEtiqueta(etiqueta2.getId());

        assertEquals(2, tablero.getEtiquetas().size());
        assertTrue(tablero.getEtiquetas().contains(etiqueta));
        assertTrue(tablero.getEtiquetas().contains(etiqueta2));
        assertEquals("Tablero test privado", etiqueta.getTablero().getNombre());
        assertEquals("Tablero test privado", etiqueta2.getTablero().getNombre());
    }

}
