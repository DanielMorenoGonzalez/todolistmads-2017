package common;

import org.dbunit.JndiDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import java.io.FileInputStream;

public class DBSetup {

    /**
     * Se ejecuta al antes de cada test.
     * Se insertan los datos de prueba en la tabla Usuarios de
     * la BD "DBTest". La BD ya contiene una tabla de usuarios
     * porque la ha creado JPA al tener la propiedad
     * hibernate.hbm2ddl.auto (en META-INF/persistence.xml) el valor update.
     * Los datos de prueba se definen en el fichero
     * test/resources/usuarios_dataset.xml
     *
     * @param datasetPath  la ruta del dataset a cargar
     * @param lookupDbName el nombre de la base de datos a crear
     * @throws Exception si algo va mal al crear la BD (dataset no existe, nombre de db no válido...)
     */
    public static void initData(String datasetPath, String lookupDbName) throws Exception {
        JndiDatabaseTester databaseTester = new JndiDatabaseTester(lookupDbName);
        IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new
                FileInputStream(datasetPath));
        databaseTester.setDataSet(initialDataSet);
        // Definimos como operación SetUp CLEAN_INSERT, que hace un
        // DELETE_ALL de todas las tablase del dataset, seguido por un
        // INSERT. (http://dbunit.sourceforge.net/components.html)
        // Es lo que hace DbUnit por defecto, pero así queda más claro.
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.onSetup();
    }

}