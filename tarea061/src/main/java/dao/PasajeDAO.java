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

/**
 * Yo soy la clase DAO encargada de hablar con MongoDB para todo lo relacionado
 * con los pasajes.
 *
 * Mi responsabilidad no es mostrar pantallas ni recibir botones,
 * sino trabajar directamente con la base de datos.
 *
 * Dentro del flujo general, yo entro cuando el controller ya ha recibido
 * los datos del formulario y necesita comprobar o guardar información.
 *
 * Dicho de forma fácil:
 * el usuario pulsa un botón,
 * el controller recoge los datos del request,
 * y después me llama a mí para consultar o insertar en la colección "pasaje".
 */
public class PasajeDAO {

    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = (Logger) LoggerFactory.getLogger(PasajeDAO.class);

    private final MongoCollection<Document> collection;
    private final MongoCollection<Document> contadoresCollection;

    /**
     * Aquí yo recibo la base de datos ya abierta y dejo preparada la colección de
     * pasajes.
     *
     * Gracias a esto, los métodos de esta clase pueden trabajar directamente
     * contra MongoDB sin tener que volver a crear la conexión cada vez.
     *
     * @param database base de datos MongoDB ya inicializada por la capa superior.
     */
    public PasajeDAO(MongoDatabase database) {
        this.collection = database.getCollection("pasaje");
        this.contadoresCollection = database.getCollection("contadores");
    }

    /**
     * Aquí yo transformo un objeto Pasaje en un Document de MongoDB.
     *
     * Este paso es necesario porque el controller y el modelo trabajan con objetos
     * Java,
     * pero MongoDB guarda documentos.
     *
     * Por tanto, yo hago de traductor:
     * recibo un Pasaje en formato Java
     * y lo convierto en el formato que la base de datos entiende.
     *
     * @param pasaje objeto Java que contiene los datos del pasaje.
     * @return documento listo para ser insertado o actualizado en MongoDB.
     */
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

    /**
     * Aquí yo genero el siguiente identificador numérico del pasaje.
     *
     * Antes de insertar un registro nuevo, necesito un id único.
     * Para conseguirlo, consulto la colección de contadores y aumento
     * la secuencia correspondiente a "pasaje".
     *
     * Dicho de forma sencilla, este método me da el número nuevo
     * que se va a usar como idpasaje antes de guardar el documento.
     *
     * @return el siguiente id disponible para un nuevo pasaje.
     */
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

    /**
     * Aquí yo inserto en MongoDB un pasaje nuevo.
     *
     * Este es el punto donde termina la parte de inserción real.
     *
     * Mi trabajo consiste en:
     * 1. Pedir un nuevo idpasaje.
     * 2. Asignárselo al objeto Pasaje.
     * 3. Convertir ese objeto a Document.
     * 4. Insertarlo en la colección de MongoDB.
     *
     * Visto desde arriba, este método representa el final del recorrido:
     * botón del formulario -> controller -> request -> validación -> DAO ->
     * inserción definitiva.
     *
     * @param pasaje objeto que contiene los datos del nuevo pasaje a guardar.
     */
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
            } else {

                Document datosActualizar = new Document("pasajerocod", pasaje.getPasajerocod())
                        .append("identificador", pasaje.getIdentificador())
                        .append("numasiento", pasaje.getNumasiento())
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
                Number pvpNumber = resultado.get("pvp", Number.class);
                double pvp = pvpNumber != null ? pvpNumber.doubleValue() : 0.0;
                logger.info(
                        "Pasaje ID: " + resultado.getInteger("idpasaje") + ", Codigo Pasajero: "
                                + resultado.getInteger("pasajerocod")
                                + ", Identificador Vuelo: " + resultado.getString("identificador")
                                + ", Numero Asiento: "
                                + resultado.getInteger("numasiento")
                                + ", Clase: " + resultado.getString("clase") + ", PVP: "
                                + pvp);
            }

            return resultados;

        } catch (MongoException e) {
            logger.error("Error al obtener los pasajes: " + e.getMessage());
            return null;
        }
    }

    public Document obtenerPasajePorId(int idpasaje) {
        try {
            Document query = new Document("idpasaje", idpasaje);
            Document pasaje = collection.find(query).first();

            if (pasaje == null) {
                logger.warn("No existe ningún pasaje con id: {}", idpasaje);
            } else {
                logger.info("Pasaje encontrado con id: {}", idpasaje);
            }

            return pasaje;

        } catch (MongoException e) {
            logger.error("Error al obtener el pasaje por id: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Aquí yo compruebo si un asiento ya está ocupado dentro de un vuelo concreto.
     *
     * Este control se hace antes de insertar, para evitar que dos pasajes
     * terminen usando el mismo asiento en el mismo vuelo.
     *
     * En el flujo general, el controller me pregunta esto justo después
     * de leer los datos del formulario y justo antes de intentar guardar.
     *
     * @param identificador identificador del vuelo que se quiere comprobar.
     * @param numasiento    número de asiento que el usuario quiere reservar.
     * @return true si el asiento ya existe en ese vuelo; false si todavía está
     *         libre.
     */
    public boolean existeAsientoEnVuelo(String identificador, int numasiento) {
        try {
            Document query = new Document("identificador", identificador)
                    .append("numasiento", numasiento);

            Document result = this.collection.find(query).first();

            if (result != null) {
                logger.warn("El asiento {} ya está ocupado en el vuelo {}", numasiento, identificador);
                return true;
            }

            return false;

        } catch (MongoException e) {
            logger.error("Error al comprobar asiento en vuelo: {}", e.getMessage());
            return false;
        }
    }
}
