package Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoException;

import controlador.ConexionMongoDb;

public class MongoBean {

    private static final Logger logger = LoggerFactory.getLogger(ConexionMongoDb.class);
    private ConexionMongoDb conexionMongoDb;

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

    public void destroy() {
        logger.info("Cerrando conexión a MongoDB");
        if (conexionMongoDb != null) {
            conexionMongoDb.closeConnect();
        }
    }

    public ConexionMongoDb getConexionMongoDb() {
        return conexionMongoDb;
    }

    public void setConexionMongoDb(ConexionMongoDb conexionMongoDb) {
        this.conexionMongoDb = conexionMongoDb;
    }
}