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

/**
 * Aquí yo recupero todos los vuelos guardados en la base de datos.
 *
 * En el proceso de insertar un pasaje, este método se usa para rellenar
 * el desplegable de identificadores de vuelo dentro del formulario.
 *
 * Mi papel en el flujo es sencillo:
 * el controller me pide los vuelos,
 * yo los convierto en objetos manejables,
 * y se los devuelvo para que luego se construya el HTML del formulario.
 *
 * @return una lista con todos los vuelos disponibles para poder seleccionar
 *         uno.
 */
public class VueloDAO {

        // Logger para registrar mensajes de informacion, advertencia y error
        // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
        // el registro de mensajes.
        private static final Logger logger = (Logger) LoggerFactory.getLogger(VueloDAO.class);

        private final MongoCollection<Document> collection;
        private final MongoCollection<Document> contadoresCollection;
        /**
         * Aquí yo preparo las colecciones que necesito para trabajar con vuelos.
         *
         * En concreto, dejo lista la colección principal de vuelos
         * y también la colección de contadores.
         *
         * @param database base de datos MongoDB que voy a utilizar.
         */
        public VueloDAO(MongoDatabase database) {
                this.collection = database.getCollection("vuelo");
                this.contadoresCollection = database.getCollection("contadores");
        }

        /**
         * Aquí yo convierto un objeto Vuelo de Java en un Document de MongoDB.
         *
         * Es decir, traduzco el modelo interno de la aplicación
         * al formato que necesita la base de datos para guardar información.
         *
         * @param vuelo objeto Vuelo que quiero transformar.
         * @return documento listo para ser almacenado o usado en MongoDB.
         */
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

        /**
         * Aquí yo convierto un documento leído de MongoDB en un objeto Vuelo.
         *
         * Gracias a este método, los resultados de la base de datos
         * vuelven a convertirse en objetos más fáciles de manejar dentro del programa.
         *
         * @param document documento recuperado desde MongoDB.
         * @return objeto Vuelo equivalente al documento recibido.
         */
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

        /**
         * Aquí yo recupero todos los vuelos almacenados en la base de datos.
         *
         * Para cada documento que encuentro en la colección de vuelos,
         * lo convierto a un objeto Vuelo y lo añado a una lista.
         *
         * Al final, devuelvo esa lista con todos los vuelos disponibles.
         *
         * @return lista con todos los objetos Vuelo encontrados en la base de datos.
         */

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
        /**
         * Aquí yo obtengo los vuelos junto con datos relacionados de aeropuerto y número de pasajeros.
         *
         * Este método es más rico que una consulta simple,
         * porque une información de varias colecciones
         * para construir un resultado más completo.
         *
         * Luego ese resultado suele acabar en una tabla HTML
         * que el controller genera para enseñar al usuario.
         *
         * @return lista de documentos con información ampliada de cada vuelo.
         */
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
                                                                                        new Document("$size",
                                                                                                        "$numpasajeros"))));

                        for (Document doc : collection.aggregate(pipeline)) {
                                resultados.add(doc);
                        }

                } catch (MongoException e) {
                        logger.error("Error al obtener los vuelos con aeropuerto: " + e.getMessage());
                }
                return resultados;
        }
        /**
         * Aquí yo consulto un vuelo concreto y saco sus datos ampliados de origen, destino y pasajeros.
         *
         * Aunque el nombre del método pueda sonar a aeropuerto,
         * en la práctica estoy filtrando por identificador de vuelo
         * y generando un resultado detallado.
         *
         * @param Identificador identificador del vuelo que quiero consultar.
         */
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
                                                                                        new Document("$size",
                                                                                                        "$numpasajeros"))));

                        for (Document doc : collection.aggregate(pipeline)) {
                                resultados.add(doc);
                        }

                        for (Document resultado : resultados) {
                                logger.info(
                                                "Un solo Vuelo: " + resultado.getString("identificador") + ", Origen: "
                                                                + resultado.getString("origen")
                                                                + ", Pais Origen: " + resultado.getString("pais_origen")
                                                                + ", Destino: "
                                                                + resultado.getString("destino")
                                                                + ", Pais Destino: "
                                                                + resultado.getString("pais_destino")
                                                                + ", Numero de Pasajeros: "
                                                                + resultado.getInteger("num_pasajeros"));
                        }

                } catch (MongoException e) {
                        logger.error("Error al obtener los vuelos con aeropuerto: " + e.getMessage());
                }
        }
        /**
         * Aquí yo devuelvo un único vuelo enriquecido con la información de sus aeropuertos.
         *
         * Este método es muy útil cuando el usuario busca un vuelo concreto
         * y después el controller necesita mostrar una pantalla HTML con todos sus datos.
         *
         * @param identificador identificador del vuelo que quiero buscar.
         * @return documento con la información completa del vuelo, o null si no se encuentra.
         */
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
                                                                        .append("num_pasajeros", new Document("$size",
                                                                                        "$numpasajeros"))));

                        return collection.aggregate(pipeline).first();

                } catch (MongoException e) {
                        logger.error("Error al obtener el vuelo con aeropuerto: " + e.getMessage());
                        return null;
                }
        }
}
