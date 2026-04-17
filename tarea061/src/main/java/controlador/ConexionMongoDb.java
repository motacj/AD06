package controlador;


import java.util.Properties;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import utiles.ConfigFile;
/**
 * Yo soy la clase que se encarga de abrir, mantener y cerrar la conexión con MongoDB.
 *
 * Dicho de forma muy sencilla, yo soy la puerta de entrada a la base de datos.
 *
 * Primero leo la configuración,
 * después construyo la conexión,
 * y finalmente entrego a otras clases el acceso a la base de datos.
 *
 * Si yo fallo, los controllers y los DAO no pueden hacer su trabajo.
 */
public class ConexionMongoDb {

    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = LoggerFactory.getLogger(ConexionMongoDb.class);

    // La URI de conexion a MongoDB, que se construye a partir de los parametros
    // proporcionados en el constructor. Esta URI se utiliza para establecer la
    // conexion con la base de datos.
    private ConnectionString connectionString;

    // El cliente de MongoDB que se utilizara para establecer la conexion con la
    // base de datos. Inicialmente es null, lo que indica que no se ha establecido
    // una conexion.
    private MongoClient client = null;
    // Propiedades de configuracion del pool de conexiones
    private int minPoolSize;
    private int maxPoolSize;
    private int maxWaitTime;
    private int maxlifeTime;
    private int maxIdleTime;
    private String appName;

    /**
     * Aquí yo leo el archivo de configuración y construyo la URI de conexión.
     *
     * También cargo los parámetros del pool de conexiones,
     * porque así la conexión queda preparada con los valores definidos
     * en el archivo de propiedades.
     *
     * Si algo falla al leer la configuración,
     * dejo constancia del error para evitar que otras clases trabajen con datos incompletos.
     */
    public ConexionMongoDb() {

        // Inicializamos la variable properties como null, lo que indica que aun no se
        // han leido las propiedades de configuracion desde un archivo. Este codigo
        // parece ser un fragmento de un constructor que se utiliza para establecer la
        // conexion a MongoDB, pero la variable properties no se utiliza en el resto del
        // codigo proporcionado.
        Properties properties = null;

        try {
            ConfigFile configFile = new ConfigFile("mongodb.properties");

            properties = configFile.readPropertiesFiles();

            // Propiedades de configuracion del pool de conexiones que lo leemos del archivo de configuracion
            this.minPoolSize = Integer.parseInt((String) properties.get("minPoolSize"));
            this.maxPoolSize = Integer.parseInt((String) properties.get("maxPoolSize"));
            this.maxWaitTime = Integer.parseInt((String) properties.get("maxWaitTime"));
            this.maxlifeTime = Integer.parseInt((String) properties.get("maxlifeTime"));
            this.maxIdleTime = Integer.parseInt((String) properties.get("maxIdleTime"));
            this.appName = (String) properties.get("appName");
        } catch (Exception e) {
            logger.error("Error al leer el archivo de configuracion: " + e.getMessage());
            this.connectionString = null;
            return;
        }
        String host = (String) properties.get("host");
        int port = Integer.parseInt((String) properties.get("port"));
        String database = (String) properties.get("database");
        String username = (String) properties.get("username");
        String password = (String) properties.get("password");

        // Esta es la URI de conexion a MongoDB, que incluye el nombre de usuario, la
        // contraseña, el host, el puerto y el nombre de la base de datos.
        this.connectionString = new ConnectionString(
                "mongodb://" + username + ":" + password + "@" + host + ":" + port + "/" + database);

    }

    /**
     * Aquí yo intento abrir la conexión real con MongoDB.
     *
     * Lo hago usando la URI y la configuración que preparé antes.
     * Además, realizo una prueba de ping para confirmar
     * que la conexión funciona de verdad.
     *
     * Si todo sale bien, guardo el cliente MongoDB dentro de la clase
     * para que luego se pueda reutilizar.
     *
     * @return true si la conexión se creó correctamente; false si ocurrió algún error.
     */
    public boolean createConnect() {

        if (this.connectionString == null) {

            logger.error("La URI de conexion a MongoDB no se ha proporcionado.");

            return false;
        }

        try {
                MongoClient mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyConnectionString(this.connectionString)
                            .applyToConnectionPoolSettings(builder -> builder
                                    .minSize(this.minPoolSize)
                                    .maxSize(this.maxPoolSize)
                                    .maxWaitTime(this.maxWaitTime, java.util.concurrent.TimeUnit.SECONDS)
                                    .maxConnectionLifeTime(this.maxlifeTime, java.util.concurrent.TimeUnit.SECONDS)
                                    .maxConnectionIdleTime(this.maxIdleTime, java.util.concurrent.TimeUnit.SECONDS))
                            .applicationName(this.appName)
                            .build()
                );
            //MongoClient mongoClient = MongoClients.create(this.connectionString);

            MongoDatabase database = mongoClient.getDatabase(this.connectionString.getDatabase());
            // Realizamos una prueba de ping para verificar que la conexion se ha
            // establecido correctamente.

            Bson command = new Document("ping", new BsonInt64(1));

            // Resutado de la prueba de ping, que se ejecuta utilizando el metodo runCommand
            // del objeto database. Si la conexion es exitosa, se imprimira un mensaje
            // indicando que la conexion a MongoDB fue exitosa, junto con el resultado del
            // comando ping. Si ocurre un error durante la conexion, se capturara la
            // excepcion MongoException y se imprimira un mensaje de error.
            Document result = database.runCommand(command);

            this.client = mongoClient;

            logger.info("Conexion a MongoDB exitosa.");

            return true;

        } catch (MongoException e) {

            logger.error("Error al conectar a MongoDB: " + e.getMessage());

            return false;
        }
    }
    /**
     * Aquí yo muestro información general del cluster de MongoDB.
     *
     * Este método me sirve como comprobación técnica:
     * no modifica datos ni construye pantallas,
     * pero me ayuda a confirmar que la conexión está viva
     * y a ver información del entorno al que estoy conectado.
     */
    public void mostrarDatosCluster() {

        // Verificamos si la conexion a MongoDB se ha establecido correctamente antes de
        // intentar obtener informacion del cluster. Si el cliente es null, significa
        // que no se ha establecido una conexion, por lo que se imprime un mensaje
        // indicando que no se ha establecido una conexion a MongoDB y se retorna del
        // metodo.
        if (this.client == null) {

            logger.warn("No se ha establecido una conexion a MongoDB.");

            return;
        }

        // si se ha establecido una conexion, se intenta obtener informacion del cluster
        // utilizando el metodo getClusterDescription del cliente de MongoDB. Si la
        // operacion es exitosa, se imprimira la informacion del cluster. Si ocurre un
        // error durante la obtencion de la informacion del cluster, se capturara la
        // excepcion MongoException y se imprimira un mensaje de error.
        try {

            // Obtenemos la informacion del cluster utilizando el metodo
            // getClusterDescription del cliente de MongoDB y la imprimimos en la consola.
            logger.info("Informacion del cluster MongoDB:" + this.client.getClusterDescription());

        } catch (MongoException e) {

            logger.error("Error al obtener informacion del cluster MongoDB: " + e.getMessage());
        }
    }
    /**
     * Aquí yo muestro información básica de la base de datos conectada.
     *
     * Igual que el método del cluster, este no cambia datos.
     * Su objetivo es ayudarme a comprobar
     * que estoy apuntando a la base de datos correcta.
     */
    public void mostrarDatosDatabase() {

        // Verificamos si la conexion a MongoDB se ha establecido correctamente antes de
        // intentar obtener informacion de la base de datos. Si el cliente es null,
        // significa que no se ha establecido una conexion, por lo que se imprime un
        // mensaje indicando que no se ha establecido una conexion a MongoDB y se
        // retorna del metodo.
        if (this.client == null) {

            logger.warn("No se ha establecido una conexion a MongoDB.");

            return;
        }
        // Si se ha establecido una conexion, se intenta obtener informacion de la base
        // de datos utilizando el metodo getDatabase del cliente de MongoDB, pasando el
        // nombre de la base de datos que se especifico en la URI de conexion. Si la
        // operacion es exitosa, se imprimira el nombre de la base de datos. Si ocurre
        // un error durante la obtencion de la informacion de la base de datos, se
        // capturara la excepcion MongoException y se imprimira un mensaje de error.
        try {

            // Obtenemos el nombre de la base de datos utilizando el metodo getDatabase del
            // cliente de MongoDB y lo imprimimos en la consola.
            logger.info("Informacion de la base de datos MongoDB:"
                    + this.client.getDatabase(this.connectionString.getDatabase()).getName());

        } catch (MongoException e) {

            logger.error("Error al obtener informacion de la base de datos MongoDB: " + e.getMessage());
        }
    }
    /**
     * Aquí yo devuelvo el objeto MongoDatabase que otras clases necesitan para trabajar.
     *
     * Este método es muy importante porque actúa como puente:
     * los controllers y los DAO no crean la conexión por su cuenta,
     * sino que me piden a mí la base de datos ya preparada.
     *
     * @return la base de datos MongoDB activa, o null si no existe conexión.
     */
    public MongoDatabase getDatosbase() {
        if (this.client == null) {

            logger.warn("No se ha establecido una conexion a MongoDB.");

            return null;
        }
        return this.client.getDatabase(this.connectionString.getDatabase());
    }
    /**
     * Aquí yo cierro la conexión con MongoDB cuando ya no se necesita.
     *
     * Esto ayuda a liberar recursos y dejar el sistema cerrado correctamente.
     *
     * Aunque parezca un paso pequeño, es importante
     * porque una buena conexión no solo se abre bien,
     * también se cierra bien.
     */
    public void closeConnect() {

        if (this.client != null) {

            this.client.close();

            logger.info("Conexion a MongoDB cerrada.");

        }
    }
    /**
     * Aquí yo sería el punto para devolver una colección concreta de MongoDB.
     *
     * En la versión actual todavía no está implementado,
     * así que por ahora solo deja claro que este hueco existe
     * para una posible ampliación futura.
     *
     * @param string nombre de la colección solicitada.
     * @return no devuelve nada útil todavía, porque el método no está implementado.
     */
    public MongoCollection<Document> getCollection(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCollection'");
    }
}
