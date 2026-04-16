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
import modelo.Vuelo;

public class VueloDAO {

    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = (Logger) LoggerFactory.getLogger(VueloDAO.class);

    private final MongoCollection<Document> collection;
    private final MongoCollection<Document> contadoresCollection;

    public VueloDAO(MongoDatabase database) {
        this.collection = database.getCollection("vuelo");
        this.contadoresCollection = database.getCollection("contadores");
    }

    // Metoddo para convertir un objeto Vuelo a un documento de MongoDB. Este
    // metodo toma un objeto Vuelo como parametro y crea un nuevo documento de
    // MongoDB utilizando los atributos del
    private Document convertirVueloADocument(Vuelo vuelo) {
        Document document = new Document();
        document.append("identificador", vuelo.getIdentificador())
                .append("aeropuertoorigen", vuelo.getAeropuertoorigen())
                .append("aeropuertodestino", vuelo.getAeropuertodestino())
                .append("tipovuelo", vuelo.getTipovuelo())
                .append("fechavuelo", vuelo.getFechavuelo())
                .append("descuento", vuelo.getDescuento());
        return document;
    }

    // Metodo para convertir un documento de MongoDB a un objeto Vuelo. Este
    // metodo toma un documento de MongoDB como parametro y crea un nuevo objeto
    // Vuelo utilizando los valores del
    private Vuelo convertirDocumentAVuelo(Document document) {
        Vuelo vuelo = new Vuelo(document);
        vuelo.setIdentificador(document.getString("identificador"));
        vuelo.setAeropuertoorigen(document.getString("aeropuertoorigen"));
        vuelo.setAeropuertodestino(document.getString("aeropuertodestino"));
        vuelo.setTipovuelo(document.getString("tipovuelo"));
        vuelo.setFechavuelo(document.getString("fechavuelo"));
        vuelo.setDescuento((document.get("descuento", Number.class).doubleValue()));
        return vuelo;
    }

    // Metodo que devuelva en una lista los datos de todos los vuelos

    public List<Vuelo> obtenerTodosLosVuelos() {
        List<Vuelo> vuelos = new ArrayList<>();
        try {
            for (Document document : collection.find()) {
                Vuelo vuelo = convertirDocumentAVuelo(document);
                vuelos.add(vuelo);
            }

        } catch (MongoException e) {
            logger.error("Error al obtener los vuelos: " + e.getMessage());
        }
        return vuelos;

    }

    public List<Document> obtenerVuelosConAeropuerto() {

        List<Document> resultados = new ArrayList<>();

        try {
            List<Document> pipeline = Arrays.asList(new Document("$lookup",
                    new Document("from", "aeropuerto")
                            .append("localField", "aeropuertoorigen")
                            .append("foreignField", "codaeropuerto")
                            .append("as", "vueloorigen")),
                    new Document("$unwind",
                            new Document("path", "$vueloorigen")
                                    .append("preserveNullAndEmptyArrays", true)),
                    new Document("$lookup",
                            new Document("from", "aeropuerto")
                                    .append("localField", "aeropuertodestino")
                                    .append("foreignField", "codaeropuerto")
                                    .append("as", "vuelodestino")),
                    new Document("$unwind",
                            new Document("path", "$vuelodestino")
                                    .append("preserveNullAndEmptyArrays", true)),
                    new Document("$lookup",
                            new Document("from", "pasaje")
                                    .append("localField", "identificador")
                                    .append("foreignField", "identificador")
                                    .append("as", "numpasajeros")),
                    new Document("$project",
                            new Document("_id", 0L)
                                    .append("identificador", 1L)
                                    .append("aeropuertoorigen", 1L)
                                    .append("origen", "$vueloorigen.nombre")
                                    .append("pais_origen", "$vueloorigen.pais")
                                    .append("aeropuertodestino", 1L)
                                    .append("destino", "$vuelodestino.nombre")
                                    .append("pais_destino", "$vuelodestino.pais")
                                    .append("tipovuelo", 1L)
                                    .append("fechavuelo", 1L)
                                    .append("num_pasajeros",
                                            new Document("$size", "$numpasajeros"))));

            for (Document doc : collection.aggregate(pipeline)) {
                resultados.add(doc);
            }

        } catch (MongoException e) {
            logger.error("Error al obtener los vuelos con aeropuerto: " + e.getMessage());
        }
        return resultados;
    }

    public void obtenerVuelosDeUnAeropuerto(String Identificador) {

        List<Document> resultados = new ArrayList<>();

        try {
            List<Document> pipeline = Arrays.asList(new Document("$match",
                    new Document("identificador", Identificador)),
                    new Document("$lookup",
                            new Document("from", "aeropuerto")
                                    .append("localField", "aeropuertoorigen")
                                    .append("foreignField", "codaeropuerto")
                                    .append("as", "vueloorigen")),
                    new Document("$unwind",
                            new Document("path", "$vueloorigen")
                                    .append("preserveNullAndEmptyArrays", true)),
                    new Document("$lookup",
                            new Document("from", "aeropuerto")
                                    .append("localField", "aeropuertodestino")
                                    .append("foreignField", "codaeropuerto")
                                    .append("as", "vuelodestino")),
                    new Document("$unwind",
                            new Document("path", "$vuelodestino")
                                    .append("preserveNullAndEmptyArrays", true)),
                    new Document("$lookup",
                            new Document("from", "pasaje")
                                    .append("localField", "identificador")
                                    .append("foreignField", "identificador")
                                    .append("as", "numpasajeros")),
                    new Document("$project",
                            new Document("_id", 0L)
                                    .append("identificador", 1L)
                                    .append("aeropuertoorigen", 1L)
                                    .append("origen", "$vueloorigen.nombre")
                                    .append("pais_origen", "$vueloorigen.pais")
                                    .append("aeropuertodestino", 1L)
                                    .append("destino", "$vuelodestino.nombre")
                                    .append("pais_destino", "$vuelodestino.pais")
                                    .append("num_pasajeros",
                                            new Document("$size", "$numpasajeros"))));

            for (Document doc : collection.aggregate(pipeline)) {
                resultados.add(doc);
            }

            for (Document resultado : resultados) {
                logger.info(
                        "Un solo Vuelo: " + resultado.getString("identificador") + ", Origen: "
                                + resultado.getString("origen")
                                + ", Pais Origen: " + resultado.getString("pais_origen") + ", Destino: "
                                + resultado.getString("destino")
                                + ", Pais Destino: " + resultado.getString("pais_destino") + ", Numero de Pasajeros: "
                                + resultado.getInteger("num_pasajeros"));
            }

        } catch (MongoException e) {
            logger.error("Error al obtener los vuelos con aeropuerto: " + e.getMessage());
        }
    }

    public Document obtenerVueloConAeropuertoPorIdentificador(String identificador) {
        try {
            List<Document> pipeline = Arrays.asList(
                    new Document("$match",
                            new Document("identificador", identificador)),
                    new Document("$lookup",
                            new Document("from", "aeropuerto")
                                    .append("localField", "aeropuertoorigen")
                                    .append("foreignField", "codaeropuerto")
                                    .append("as", "vueloorigen")),
                    new Document("$unwind",
                            new Document("path", "$vueloorigen")
                                    .append("preserveNullAndEmptyArrays", true)),
                    new Document("$lookup",
                            new Document("from", "aeropuerto")
                                    .append("localField", "aeropuertodestino")
                                    .append("foreignField", "codaeropuerto")
                                    .append("as", "vuelodestino")),
                    new Document("$unwind",
                            new Document("path", "$vuelodestino")
                                    .append("preserveNullAndEmptyArrays", true)),
                    new Document("$lookup",
                            new Document("from", "pasaje")
                                    .append("localField", "identificador")
                                    .append("foreignField", "identificador")
                                    .append("as", "numpasajeros")),
                    new Document("$project",
                            new Document("_id", 0L)
                                    .append("identificador", 1L)
                                    .append("aeropuertoorigen", 1L)
                                    .append("origen", "$vueloorigen.nombre")
                                    .append("pais_origen", "$vueloorigen.pais")
                                    .append("aeropuertodestino", 1L)
                                    .append("destino", "$vuelodestino.nombre")
                                    .append("pais_destino", "$vuelodestino.pais")
                                    .append("tipovuelo", 1L)
                                    .append("fechavuelo", 1L)
                                    .append("num_pasajeros", new Document("$size", "$numpasajeros"))));

            return collection.aggregate(pipeline).first();

        } catch (MongoException e) {
            logger.error("Error al obtener el vuelo con aeropuerto: " + e.getMessage());
            return null;
        }
    }
}
