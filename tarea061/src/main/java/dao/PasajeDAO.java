package dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import modelo.Pasaje;

public class PasajeDAO {

    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = (Logger) LoggerFactory.getLogger(PasajeDAO.class);

    private final MongoCollection<Document> collection;
    private final MongoCollection<Document> contadoresCollection;

    public PasajeDAO(MongoDatabase database) {
        this.collection = database.getCollection("pasaje");
        this.contadoresCollection = database.getCollection("contadores");
    }

    // Metoddo para convertir un objeto Pasaje a un documento de MongoDB. Este
    // metodo toma un objeto Pasaje como parametro y crea un nuevo documento de
    // MongoDB utilizando los atributos del
    private Document convertirPasajeADocument(Pasaje pasaje) {
        Document document = new Document();
        document.append("idpasaje", pasaje.getIdpasaje())
                .append("pasajerocod", pasaje.getPasajerocod())
                .append("identificador", pasaje.getIdentificador())
                .append("numasiento", pasaje.getNumasiento())
                .append("clase", pasaje.getClase())
                .append("pvp", pasaje.getPvp());
        return document;
    }

    // Metodo para convertir un documento de MongoDB a un objeto Pasaje. Este
    // metodo toma un documento de MongoDB como parametro y crea un nuevo objeto
    // Pasaje utilizando los valores del
    private Pasaje convertirDocumentAPasaje(Document document) {
        Pasaje pasaje = new Pasaje();
        pasaje.setIdpasaje(document.getInteger("idpasaje"));
        pasaje.setPasajerocod(document.getInteger("pasajerocod"));
        pasaje.setIdentificador(document.getString("identificador"));
        pasaje.setNumasiento(document.getInteger("numasiento"));
        pasaje.setClase(document.getString("clase"));
        pasaje.setPvp(document.getDouble("pvp"));
        return pasaje;
    }

    private int siguienteIdPasaje() {
        Document filtro = new Document("_id", "pasaje");
        Document actualizacion = new Document("$inc", new Document("secuencia", 1));

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
                .returnDocument(ReturnDocument.AFTER)
                .upsert(true);

        Document resultado = contadoresCollection.findOneAndUpdate(filtro, actualizacion, options);

        if (resultado == null || resultado.getInteger("secuencia") == null) {
            throw new RuntimeException("No se pudo generar el siguiente idpasaje.");
        }

        return resultado.getInteger("secuencia");
    }

    public boolean buscarVueloPasajero(int pasajerocod, String identificador) {
        try {
            Document query = new Document("pasajerocod", pasajerocod).append("identificador", identificador);
            Document result = this.collection.find(query).first();
            if (result != null) {
                Pasaje pasaje = convertirDocumentAPasaje(result);
                logger.info("Pasaje encontrado: {}. El numero de asiento es: {} en el vuelo {}", pasaje.getIdpasaje(),
                        pasaje.getNumasiento(), pasaje.getIdentificador());
                return true;
            } else {
                logger.warn("No se encontro el pasaje para el vuelo: {}", identificador);
                return false;
            }
        } catch (MongoException e) {
            logger.error("Error al buscar el pasaje: {}", e.getMessage());
            return false;
        }
    }

    public void insertarPasaje(Pasaje pasaje) {
        try {
            int nuevoId = siguienteIdPasaje();
            pasaje.setIdpasaje(nuevoId);
            Document document = convertirPasajeADocument(pasaje);
            this.collection.insertOne(document);
            logger.info("Pasaje insertado correctamente: {}", pasaje.getIdpasaje());
        } catch (MongoException e) {
            logger.error("Error al insertar el pasaje: {}", e.getMessage());
        }
    }

    public void borrarPasaje(int idpasaje) {
        try {
            Document query = new Document("idpasaje", idpasaje);
            Document result = this.collection.find(query).first();

            if (result == null) {
                logger.error("No existe el pasaje con id: {}", idpasaje);
            } else {
                this.collection.deleteOne(query);
                logger.info("Pasaje con id {} borrado correctamente.", idpasaje);
            }

        } catch (MongoException e) {
            logger.error("Error al borrar el pasaje: {}", e.getMessage());
        }

    }

    public void actualizarPasaje(Pasaje pasaje) {
        try {
            Document query = new Document("idpasaje", pasaje.getIdpasaje());

            Document result = this.collection.find(query).first();

            if (result == null) {
                logger.error("No existe el pasaje con id: {}", pasaje.getIdpasaje());
                return;
            }else {

                Document datosActualizar = new Document(
                    "numasiento", pasaje.getNumasiento())
                    .append("clase", pasaje.getClase())
                    .append("pvp", pasaje.getPvp());


                Document update = new Document("$set", datosActualizar);
                this.collection.updateOne(query, update);
                logger.info("Registro id {} actualizado correctamente.", pasaje.getIdpasaje());
            }
        } catch (MongoException e) {
            logger.error("Error al actualizar el pasaje: {}", e.getMessage());
        }
    }

    public List<Document> obtenerDastosDelPasaje(String Identificador) {

        List<Document> resultados = new ArrayList<>();

        try {
            List<Document> pipeline = Arrays.asList(new Document("$match",
                    new Document("identificador", Identificador)),
                    new Document("$lookup",
                            new Document("from", "vuelo")
                                    .append("localField", "identificador")
                                    .append("foreignField", "identificador")
                                    .append("as", "pasaje_vuelo")),
                    new Document("$unwind",
                            new Document("path", "$pasaje_vuelo")
                                    .append("preserveNullAndEmptyArrays", true)),
                    new Document("$lookup",
                            new Document("from", "pasajero")
                                    .append("localField", "pasajerocod")
                                    .append("foreignField", "pasajerocod")
                                    .append("as", "pasaje_pasajero")),
                    new Document("$unwind",
                            new Document("path", "$pasaje_pasajero")
                                    .append("preserveNullAndEmptyArrays", true)),
                    new Document("$project",
                            new Document("_id", 0L)
                                    .append("idpasaje", 1L)
                                    .append("codigo_pasajero", "$pasaje_pasajero.pasajerocod")
                                    .append("nombre_pasajero", "$pasaje_pasajero.nombre")
                                    .append("pais_pasajero", "$pasaje_pasajero.pais")
                                    .append("numasiento", "$numasiento")
                                    .append("clase", "$clase")
                                    .append("pvp_final",
                                            new Document("$toDouble",
                                                    new Document("$sum",
                                                            Arrays.asList(
                                                                    new Document("$multiply",
                                                                            Arrays.asList("$pvp",
                                                                                    "$pasaje_vuelo.descuento")),
                                                                    "$pvp"))))));

            for (Document doc : collection.aggregate(pipeline)) {
                resultados.add(doc);
            }

            for (Document resultado : resultados) {
                logger.info(
                        "Pasaje ID: " + resultado.getInteger("idpasaje") + ", Codigo Pasajero: "
                                + resultado.getInteger("codigo_pasajero")
                                + ", Nombre Pasajero: " + resultado.getString("nombre_pasajero") + ", Pais Pasajero: "
                                + resultado.getString("pais_pasajero") + ", Numero Asiento: "
                                + resultado.getInteger("numasiento")
                                + ", Clase: " + resultado.getString("clase") + ", PVP Final: "
                                + resultado.getDouble("pvp_final"));
            }

            return resultados;

        } catch (MongoException e) {
            logger.error("Error al obtener los vuelos con aeropuerto: " + e.getMessage());
            return null;
        }


    }

    public List<Document> obtenerTodosLosPasajes() {
        List<Document> resultados = new ArrayList<>();

        try {
            for (Document doc : collection.find()) {
                resultados.add(doc);
            }

            for (Document resultado : resultados) {
                logger.info(
                        "Pasaje ID: " + resultado.getInteger("idpasaje") + ", Codigo Pasajero: "
                                + resultado.getInteger("pasajerocod")
                                + ", Identificador Vuelo: " + resultado.getString("identificador") + ", Numero Asiento: "
                                + resultado.getInteger("numasiento")
                                + ", Clase: " + resultado.getString("clase") + ", PVP: "
                                + resultado.getDouble("pvp"));
            }

            return resultados;

        } catch (MongoException e) {
            logger.error("Error al obtener los pasajes: " + e.getMessage());
            return null;
        }
    }   

}

