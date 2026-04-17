package Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoException;

import controlador.ConexionMongoDb;
/**
 * Yo soy una clase de apoyo que encapsula la conexión con MongoDB.
 *
 * Mi papel es muy sencillo:
 * crear la conexión al iniciar,
 * dejarla disponible para otras clases
 * y cerrarla cuando ya no haga falta.
 *
 * Gracias a mí, los controllers no tienen que preocuparse
 * de construir toda la conexión paso a paso cada vez.
 */
public class MongoBean {

    private static final Logger logger = LoggerFactory.getLogger(ConexionMongoDb.class);
    private ConexionMongoDb conexionMongoDb;
    /**
     * Aquí yo creo internamente la conexión con MongoDB e intento dejarla activa.
     *
     * Si la conexión se establece bien, la dejo guardada para que otros componentes la usen.
     * Si falla, registro el error y dejo claro que la conexión no está disponible.
     */
    public MongoBean() {
        this.conexionMongoDb = new ConexionMongoDb();

        try {
            conexionMongoDb.createConnect();
            logger.info("Conexión a MongoDB inicializada correctamente");
        } catch (MongoException e) {
            logger.error("Error al inicializar la conexión a MongoDB: " + e.getMessage(), e);
            this.conexionMongoDb = null;
        }
    }
    /**
     * Aquí yo cierro la conexión con MongoDB cuando el bean ya no se necesita.
     *
     * Mi objetivo es dejar la conexión cerrada de forma ordenada
     * y evitar que queden recursos abiertos innecesariamente.
     */
    public void destroy() {
        logger.info("Cerrando conexión a MongoDB");
        if (conexionMongoDb != null) {
            conexionMongoDb.closeConnect();
        }
    }
    /**
     * Aquí yo devuelvo la conexión MongoDB que tengo guardada.
     *
     * Este método permite que otras clases me pidan la conexión
     * en lugar de crearla ellas directamente.
     *
     * @return la conexión MongoDB almacenada en este bean.
     */
    public ConexionMongoDb getConexionMongoDb() {
        return conexionMongoDb;
    }
    /**
     * Aquí yo permito sustituir o asignar la conexión MongoDB de este bean.
     *
     * Este método resulta útil si en algún momento quiero cambiar
     * la conexión que estoy gestionando.
     *
     * @param conexionMongoDb nueva conexión que quiero dejar guardada.
     */
    public void setConexionMongoDb(ConexionMongoDb conexionMongoDb) {
        this.conexionMongoDb = conexionMongoDb;
    }
}