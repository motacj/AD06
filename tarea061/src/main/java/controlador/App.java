package controlador;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.client.MongoDatabase;

import dao.PasajeDAO;
import dao.VueloDAO;
import modelo.Pasaje;
/**
 * Yo soy una clase de prueba manual.
 *
 * No actúo como servlet ni como pantalla web.
 * Mi función es servirme como punto de arranque para probar desde consola
 * la conexión con MongoDB y varios métodos DAO.
 *
 * Gracias a mí puedo comprobar, fuera del flujo web,
 * si la lógica de inserción, borrado, actualización y consulta funciona bien.
 */
public class App {
    /**
     * Aquí yo ejecuto pruebas manuales de la aplicación.
     *
     * Dentro de este método hago varias comprobaciones:
     * 1. Creo la conexión con MongoDB.
     * 2. Verifico si conecta correctamente.
     * 3. Muestro información del cluster y de la base de datos.
     * 4. Pruebo operaciones de pasajes como insertar, borrar y actualizar.
     * 5. Pruebo consultas de vuelos y pasajes.
     *
     * Este método me ayuda a validar la lógica DAO
     * antes o aparte de usar la interfaz web.
     *
     * @param args argumentos de entrada del programa.
     */
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        ConexionMongoDb connection = new ConexionMongoDb();

        if (connection.createConnect()) {
            System.out.println("Conexión correcta a MongoDB");

            MongoDatabase database = connection.getDatosbase();

            if (database != null) {
                System.out.println("Base de datos: " + database.getName());
            }

            connection.mostrarDatosCluster();
            connection.mostrarDatosDatabase();

            connection.closeConnect();
        } else {
            System.out.println("No se pudo conectar a MongoDB");
        }

        // Se inicia el proceso del Metodo para insertar pasaje a un vuelo
        PasajeDAO pasajeDAO = new PasajeDAO(connection.getDatosbase());

        Pasaje pasaje = new Pasaje();
        pasaje.setPasajerocod(3);
        pasaje.setIdentificador("V001");
        pasaje.setNumasiento(27);
        pasaje.setClase("BUSINESS");
        pasaje.setPvp(59.0);

        boolean encontrado = pasajeDAO.buscarVueloPasajero(pasaje.getPasajerocod(), pasaje.getIdentificador());

        if (!encontrado) {
            pasajeDAO.insertarPasaje(pasaje);
        } else {
            logger.warn("Ya existe un pasaje para ese pasajero en ese vuelo.");
        }

        // Se inicia el proceso del Metodo para borrar pasaje a un vuelo

        pasajeDAO.borrarPasaje(7);

        // Se inicia el proceso del Metodo para actualziar el pasaje a un vuelo

        Pasaje pasajeToActualziar = new Pasaje(1, 3, "V001", 27, "BUSINESS", 59.0);

        pasajeDAO.actualizarPasaje(pasajeToActualziar);
        

        // lista los vuelos
        VueloDAO vueloDAO = new VueloDAO(connection.getDatosbase());
        vueloDAO.obtenerVuelosConAeropuerto();
        vueloDAO.obtenerVuelosDeUnAeropuerto("V002");
        pasajeDAO.obtenerDastosDelPasaje("V001");

        connection.closeConnect();
    }

}
