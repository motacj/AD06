package dao;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import modelo.Aeropuerto;

public class AeropuertoDAO {

    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AeropuertoDAO.class);

    private final MongoCollection<Document> collection;

    public AeropuertoDAO(MongoDatabase database) {
        this.collection = ((MongoDatabase) database).getCollection("aeropuerto");
    }

    // Metoddo para convertir un objeto Aeropuerto a un documento de MongoDB. Este
    // metodo toma un objeto Aeropuerto como parametro y crea un nuevo documento de
    // MongoDB utilizando los atributos del
    private Document convertirAeropuertoADocument(Aeropuerto aeropuerto) {
        Document document = new Document();
        document.append("codaeropuerto", aeropuerto.getCodaeropuerto())
                .append("nombre", aeropuerto.getNombre())
                .append("ciudad", aeropuerto.getCiudad())
                .append("pais", aeropuerto.getPais())
                .append("tasa", aeropuerto.getTasa());
        return document;
    }

    // Metodo para convertir un documento de MongoDB a un objeto Aeropuerto. Este
    // metodo toma un documento de MongoDB como parametro y crea un nuevo objeto
    // Aeropuerto utilizando los valores del
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
