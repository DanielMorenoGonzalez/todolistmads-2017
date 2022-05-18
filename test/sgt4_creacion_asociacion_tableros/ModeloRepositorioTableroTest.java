package sgt4_creacion_asociacion_tableros;

import common.DBSetup;
import models.*;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Environment;
import play.db.Database;
import play.db.jpa.JPAApi;
import play.inject.Injector;
import play.inject.guice.GuiceApplicationBuilder;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ModeloRepositorioTableroTest {
    static private Injector injector;
    static private Database db;

    @BeforeClass
    static public void initApplication() {
        GuiceApplicationBuilder guiceApplicationBuilder =
                new GuiceApplicationBuilder().in(Environment.simple());
        injector = guiceApplicationBuilder.injector();
        // Necesario para inicializar JPA
        injector.instanceOf(JPAApi.class);
        db = injector.instanceOf(Database.class);
    }

    @Test
    public void testCrearTablero() {
        Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
        Tablero tablero = new Tablero(usuario, "Tablero 1",false);

        assertEquals("juangutierrez", tablero.getAdministrador().getLogin());
        assertEquals("juangutierrez@gmail.com", tablero.getAdministrador().getEmail());
        assertEquals("Tablero 1", tablero.getNombre());
    }

    @Test
    public void testObtenerTableroRepository() {
        TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
        assertNotNull(tableroRepository);
    }

    @Test
    public void testCrearTablaTableroEnBD() throws Exception {
        Connection connection = db.getConnection();
        DatabaseMetaData meta = connection.getMetaData();
        // En la BD H2 el nombre de las tablas se define con mayúscula y en
        // MySQL con minúscula
        ResultSet resH2 = meta.getTables(null, null, "TABLERO", new String[]{"TABLE"});
        ResultSet resMySQL = meta.getTables(null, null, "Tablero", new String[]{"TABLE"});
        boolean existeTabla = resH2.next() || resMySQL.next();
        assertTrue(existeTabla);
    }

    @Test
    public void testAddTableroInsertsDatabase() {
        UsuarioRepository usuarioRepository = injector.instanceOf(UsuarioRepository.class);
        TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
        Usuario administrador = new Usuario("juangutierrez", "juangutierrez@gmail.com");
        administrador = usuarioRepository.add(administrador);
        Tablero tablero = new Tablero(administrador, "Tablero 1",false);
        tablero = tableroRepository.add(tablero);
        assertNotNull(tablero.getId());
        assertEquals("Tablero 1", getNombreFromTableroDB(tablero.getId()));
    }

    private String getNombreFromTableroDB(Long tableroId) {
        String nombre = db.withConnection(connection -> {
            String selectStatement = "SELECT Nombre FROM Tablero WHERE ID = ? ";
            PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
            prepStmt.setLong(1, tableroId);
            ResultSet rs = prepStmt.executeQuery();
            rs.next();
            return rs.getString("Nombre");
        });
        return nombre;
    }

    @Test
    public void testUsuarioAdministraVariosTableros() {
        UsuarioRepository usuarioRepository = injector.instanceOf(UsuarioRepository.class);
        TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
        Usuario administrador = new Usuario("juangutierrez", "juangutierrez@gmail.com");
        administrador = usuarioRepository.add(administrador);
        Tablero tablero1 = new Tablero(administrador, "Tablero 1",false);
        tableroRepository.add(tablero1);
        Tablero tablero2 = new Tablero(administrador, "Tablero 2",false);
        tableroRepository.add(tablero2);
        // Recuperamos el administrador del repository
        administrador = usuarioRepository.findById(administrador.getId());
        // Y comprobamos si tiene los tableros
        assertEquals(2, administrador.getAdministrados().size());
    }

    @Test
    public void testUsuarioParticipaEnVariosTableros() throws Exception {
        initDataSet();
        UsuarioRepository usuarioRepository = injector.instanceOf(UsuarioRepository.class);
        TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
        Usuario admin = usuarioRepository.findById(1000L);
        Usuario usuario = usuarioRepository.findById(1002L);
        Set<Tablero> tableros = admin.getAdministrados();
        // Tras cargar los datos del dataset el usuario2 no tiene ningún
        // tablero asociado y el usuario 1 tiene 2 tableros administrados
        assertEquals(0, usuario.getTableros().size());
        assertEquals(2, tableros.size());
        for (Tablero tablero : tableros) {
            // Actualizamos la relación en memoria, añadiendo el usuario
            // al tablero
            tablero.getParticipantes().add(usuario);
            // Actualizamos la base de datos llamando al repository
            tableroRepository.update(tablero);
        }
        // Comprobamos que se ha actualizado la relación en la BD y
        // el usuario pertenece a los dos tableros a los que le hemos añadido
        usuario = usuarioRepository.findById(1002L);
        Set<Tablero> tablerosUsuario = usuario.getTableros();
        assertEquals(2, tablerosUsuario.size());
        for (Tablero tablero : tableros) {
            assertTrue(tablerosUsuario.contains(tablero));
        }
    }

    @Test
    public void testTableroTieneVariosUsuarios() throws Exception {
        initDataSet();
        UsuarioRepository usuarioRepository = injector.instanceOf(UsuarioRepository.class);
        TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
        // Obtenemos datos del dataset
        Tablero tablero = tableroRepository.findById(1000L);
        Usuario usuario1 = usuarioRepository.findById(1000L);
        Usuario usuario2 = usuarioRepository.findById(1002L);
        Usuario usuario3 = usuarioRepository.findById(1003L);
        assertEquals(1, tablero.getParticipantes().size());
        assertEquals(0, usuario1.getTableros().size());
        // Añadimos los 3 usuarios al tablero
        tablero.getParticipantes().add(usuario1);
        tablero.getParticipantes().add(usuario2);
        tablero.getParticipantes().add(usuario3);
        tableroRepository.update(tablero);
        // Comprobamos que los datos se han actualizado
        tablero = tableroRepository.findById(1000L);
        usuario1 = usuarioRepository.findById(1000L);
        assertEquals(3, tablero.getParticipantes().size());
        assertEquals(1, usuario1.getTableros().size());
        assertTrue(tablero.getParticipantes().contains(usuario1));
        assertTrue(usuario1.getTableros().contains(tablero));
    }

    @Test
    public void testFindByNameTablero() throws Exception {
        initDataSet();
        TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
        Tablero tablero = tableroRepository.findByName("Tablero test 1");
        assertNotNull(tablero);
        assertEquals((Long)1000L, tablero.getId());
    }

    @Test
    public void testTableroTieneVariasTareas() throws Exception {
        initDataSet();
        UsuarioRepository usuarioRepository = injector.instanceOf(UsuarioRepository.class);
        TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
        TareaRepository tareaRepository = injector.instanceOf(TareaRepository.class);

        Tablero tablero = tableroRepository.findById(1000L);
        Usuario usuario = usuarioRepository.findById(1000L);

        // Creamos una nueva tarea
        Tarea tarea1 = new Tarea(usuario, "Tarea 1", "Preparar receta", null, null);
        tarea1 = tareaRepository.add(tarea1);
        // Asignamos la tarea al tablero "Tablero test 1"
        tarea1.setTablero(tablero);

        // Obtenemos otra tarea para añadírsela al mismo tablero
        Tarea tarea2 = tareaRepository.findById(1002L);
        tarea2.setTablero(tablero);
        assertEquals("Tablero test 1", tarea1.getTablero().getNombre());
        assertEquals("Tablero test 1", tarea2.getTablero().getNombre());

        // Añadimos las tarea al conjunto de tareas del tablero
        Set<Tarea> tareas = tablero.getTareas();
        tareas.add(tarea1);
        tareas.add(tarea2);
        tablero.setTareas(tareas);
        assertEquals(2, tablero.getTareas().size());
        assertTrue(tablero.getTareas().contains(tarea1));
        assertTrue(tablero.getTareas().contains(tarea2));
    }

    @Test
    public void testTableroTieneVariasEtiquetas() throws Exception {
        initDataSet();
        UsuarioRepository usuarioRepository = injector.instanceOf(UsuarioRepository.class);
        TableroRepository tableroRepository = injector.instanceOf(TableroRepository.class);
        EtiquetaRepository etiquetaRepository = injector.instanceOf(EtiquetaRepository.class);

        Tablero tablero = tableroRepository.findById(1000L);
        Usuario usuario = usuarioRepository.findById(1000L);

        // Creamos una nueva etiqueta
        Etiqueta etiqueta1 = new Etiqueta(usuario, "Deportes de riesgo", "#0e8a16");
        etiqueta1 = etiquetaRepository.add(etiqueta1);
        // Asignamos la etiqueta al tablero "Tablero test 1"
        etiqueta1.setTablero(tablero);

        // Creamos una otra etiqueta
        Etiqueta etiqueta2 = new Etiqueta(usuario, "Deportes de riesgo 2", "#0e8a16");
        etiqueta2 = etiquetaRepository.add(etiqueta2);
        // Asignamos la etiqueta al tablero "Tablero test 1"
        etiqueta2.setTablero(tablero);

        assertEquals("Tablero test 1", etiqueta1.getTablero().getNombre());
        assertEquals("Tablero test 1", etiqueta2.getTablero().getNombre());

        // Añadimos las etiquetas al conjunto de etiquetas del tablero
        Set<Etiqueta> etiquetas = tablero.getEtiquetas();
        etiquetas.add(etiqueta1);
        etiquetas.add(etiqueta2);
        tablero.setEtiquetas(etiquetas);
        assertEquals(2, tablero.getEtiquetas().size());
        assertTrue(tablero.getEtiquetas().contains(etiqueta1));
        assertTrue(tablero.getEtiquetas().contains(etiqueta2));
    }

    private void initDataSet() throws Exception {
        DBSetup.initData("test/resources/usuarios_dataset.xml", "DBTest");
    }
}
