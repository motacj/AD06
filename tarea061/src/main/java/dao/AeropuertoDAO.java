package dao;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import modelo.Aeropuerto;
/**
 * Yo soy el DAO encargado de hablar con la colección de aeropuertos en MongoDB.
 *
 * No muestro pantallas ni recibo botones del usuario.
 * Mi trabajo es más interno:
 * recibir datos desde otras capas,
 * convertirlos al formato adecuado
 * y consultar o guardar información en la base de datos.
 */
public class AeropuertoDAO {

    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AeropuertoDAO.class);

    private final MongoCollection<Document> collection;
    /**
     * Aquí yo recibo la base de datos ya abierta y dejo preparada la colección de aeropuertos.
     *
     * @param database base de datos MongoDB que voy a utilizar.
     */
    public AeropuertoDAO(MongoDatabase database) {
        this.collection = ((MongoDatabase) database).getCollection("aeropuerto");
    }

    /**
     * Aquí yo convierto un objeto Aeropuerto de Java en un Document de MongoDB.
     *
     * Dicho de forma sencilla, hago de traductor entre el modelo de la aplicación
     * y el formato que entiende la base de datos.
     *
     * @param aeropuerto objeto Aeropuerto que quiero transformar.
     * @return documento listo para guardar en MongoDB.
     */
    private Document convertirAeropuertoADocument(Aeropuerto aeropuerto) {
        Document document = new Document();
        document.append("codaeropuerto", aeropuerto.getCodaeropuerto())
                .append("nombre", aeropuerto.getNombre())
                .append("ciudad", aeropuerto.getCiudad())
                .append("pais", aeropuerto.getPais())
                .append("tasa", aeropuerto.getTasa());
        return document;
    }

    /**
     * Aquí yo convierto un objeto Aeropuerto de Java en un Document de MongoDB.
     *
     * Dicho de forma sencilla, hago de traductor entre el modelo de la aplicación
     * y el formato que entiende la base de datos.
     *
     * @param aeropuerto objeto Aeropuerto que quiero transformar.
     * @return documento listo para guardar en MongoDB.
     */
    private Aeropuerto convertirDocumentAAeropuerto(Document document) {
        Aeropuerto aeropuerto = new Aeropuerto();
        aeropuerto.setCodaeropuerto(document.getString("codaeropuerto"));
        aeropuerto.setNombre(document.getString("nombre"));
        aeropuerto.setCiudad(document.getString("ciudad"));
        aeropuerto.setPais(document.getString("pais"));
        aeropuerto.setTasa(document.getInteger("tasa"));
        return aeropuerto;
    }

    /**
     * Metodo para insertar un nuevo aeropuerto en la coleccion de MongoDB. Este
     * metodo toma un objeto Aeropuerto como parametro, lo convierte a un documento
     * de MongoDB utilizando el metodo
     * 
     * @param aeropuerto
     * @return
     */
    public boolean insertarAeropuerto(Aeropuerto aeropuerto) {
        try {
            Document document = convertirAeropuertoADocument(aeropuerto);
            this.collection.insertOne(document);
            logger.info("Aeropuerto insertado correctamente: {}", aeropuerto.getCodaeropuerto());
            return true;
        } catch (Exception e) {
            logger.error("Error al insertar el aeropuerto: {}", e.getMessage());
            return false;
        }
    }
    /**
     * Aquí yo busco un aeropuerto por su nombre.
     *
     * Si lo encuentro, convierto el documento en un objeto Aeropuerto
     * y lo devuelvo a la capa que me lo pidió.
     *
     * @param nombre nombre del aeropuerto que quiero buscar.
     * @return el aeropuerto encontrado, o null si no existe o si ocurre un error.
     */
    public Aeropuerto buscarAeropuerto(String nombre) {
        try {
            Document query = new Document("nombre", nombre);
            Document result = this.collection.find(query).first();
            if (result != null) {
                Aeropuerto aeropuerto = convertirDocumentAAeropuerto(result);
                logger.info("Aeropuerto encontrado: {}", aeropuerto.getCodaeropuerto());
                return aeropuerto;
            } else {
                logger.warn("No se encontro el aeropuerto con nombre: {}", nombre);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error al buscar el aeropuerto: {}", e.getMessage());
            return null;
        }
    }
}
