package practica2;

import common.DBSetup;
import models.Usuario;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Environment;
import play.db.jpa.JPAApi;
import play.inject.Injector;
import play.inject.guice.GuiceApplicationBuilder;
import services.TareaService;
import services.TareaServiceException;
import services.UsuarioService;

import static org.junit.Assert.assertNull;

/**
 * Tests que comprueban condiciones que no hemos comprobado en la práctica 1.
 * Created by pavel on 4/10/17.
 */
public class Practica2Test {
    private static final String LOOK_UP_DB_NAME = "DBTest";
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
        DBSetup.initData("test/resources/usuarios_dataset.xml", LOOK_UP_DB_NAME);
    }

    private UsuarioService newUsuarioService() {
        return injector.instanceOf(UsuarioService.class);
    }

    private TareaService newTareaService() {
        return injector.instanceOf(TareaService.class);
    }

    @Test
    public void findUsuarioPorId_idInexistente_null() {
        final UsuarioService usuarioService = newUsuarioService();
        final long idInexistente = 2000L;
        final Usuario usuario = usuarioService.findUsuarioPorId(idInexistente);
        assertNull(usuario);
    }

    @Test
    public void findUsuarioPorLogin_loginInexistente_null() {
        final UsuarioService usuarioService = newUsuarioService();
        final String loginInexistente = "usuario_inventado";
        final Usuario usuario = usuarioService.findUsuarioPorLogin(loginInexistente);
        assertNull(usuario);
    }

    @Test(expected = TareaServiceException.class)
    public void borrarTarea_idInexistente_TareaServiceExcepction() {
        final TareaService tareaService = newTareaService();
        final long idInexistente = 2000L;
        tareaService.borraTarea(idInexistente);
    }

    @Test(expected = TareaServiceException.class)
    public void modificarTarea_idInexistente_TareaServiceException() {
        final TareaService tareaService = newTareaService();
        final long idInexistente = 2000L;
        tareaService.modificaTarea(idInexistente, "Esta tarea no existe", " **Ni** lo intentes", null, null);
    }
}
