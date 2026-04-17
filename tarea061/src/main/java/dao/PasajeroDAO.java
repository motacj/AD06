package dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class PasajeroDAO {

    // Logger para registrar mensajes de información, advertencia y error
    // relacionados con la conexión a MongoDB.
    private static final Logger logger = LoggerFactory.getLogger(PasajeroDAO.class);

    private final MongoCollection<Document> collection;

    public PasajeroDAO(MongoDatabase database) {
        this.collection = database.getCollection("pasajero");
    }

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
}