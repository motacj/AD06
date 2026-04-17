package dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Aquí yo saco de la base de datos todos los pasajeros.
 *
 * En el flujo de inserción, este método me sirve para llenar el desplegable
 * del formulario donde el usuario elige el código del pasajero.
 *
 * Dicho de forma simple:
 * el controller me pide la lista,
 * yo la busco en MongoDB,
 * y se la devuelvo para que después se genere el HTML del select.
 *
 * @return una lista de documentos con todos los pasajeros disponibles.
 */
public class PasajeroDAO {

    // Logger para registrar mensajes de información, advertencia y error
    // relacionados con la conexión a MongoDB.
    private static final Logger logger = LoggerFactory.getLogger(PasajeroDAO.class);

    private final MongoCollection<Document> collection;
    /**
     * Aquí yo recibo la base de datos activa y preparo la colección de pasajeros.
     *
     * @param database base de datos MongoDB sobre la que voy a trabajar.
     */
    public PasajeroDAO(MongoDatabase database) {
        this.collection = database.getCollection("pasajero");
    }
    /**
     * Aquí yo devuelvo solo los códigos de los pasajeros.
     *
     * Este método es muy útil para formularios,
     * porque muchas veces no necesito todos los datos del pasajero,
     * sino únicamente el código para rellenar un select.
     *
     * @return lista con los códigos de pasajero disponibles.
     */
    public List<Integer> obtenerCodigosPasajero() {
        List<Integer> codigos = new ArrayList<>();

        try {
            for (Document doc : collection.find()) {
                Integer codigo = doc.getInteger("pasajerocod");

                if (codigo != null) {
                    codigos.add(codigo);
                }
            }

            logger.info("Se obtuvieron {} códigos de pasajero.", codigos.size());

        } catch (MongoException e) {
            logger.error("Error al obtener códigos de pasajero: {}", e.getMessage());
        }

        return codigos;
    }
    /**
     * Aquí yo recupero todos los pasajeros de la colección.
     *
     * Este método sirve cuando necesito el conjunto completo
     * y no solo un dato parcial como el código.
     *
     * @return lista con todos los pasajeros encontrados.
     */
    public List<Document> obtenerTodosPasajeros() {
        List<Document> pasajeros = new ArrayList<>();

        try {
            for (Document doc : collection.find()) {
                pasajeros.add(doc);
            }

            logger.info("Se obtuvieron {} pasajeros.", pasajeros.size());

        } catch (MongoException e) {
            logger.error("Error al obtener pasajeros: {}", e.getMessage());
        }

        return pasajeros;
    }

}