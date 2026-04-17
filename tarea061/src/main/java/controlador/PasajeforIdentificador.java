package controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Bean.MongoBean;
import dao.PasajeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * Yo soy el controller que muestra los pasajeros de un vuelo concreto.
 *
 * Mi trabajo es recibir el identificador del vuelo que el usuario ha elegido,
 * pedir al DAO los datos relacionados con ese vuelo
 * y devolver una tabla HTML con la información de los pasajeros.
 *
 * Dicho de forma sencilla:
 * pantalla principal o formulario -> botón buscar
 * -> Controller -> request -> DAO -> listado HTML de pasajeros.
 */
@WebServlet(name = "PasajeforIdentificador", urlPatterns = { "/findpasaje" })
public class PasajeforIdentificador extends HttpServlet {
    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = LoggerFactory.getLogger(PasajeforIdentificador.class);

    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;
    /**
     * Aquí yo preparo la conexión con MongoDB antes de buscar los pasajeros de un vuelo.
     *
     * Necesito esa conexión para poder consultar los datos del pasaje
     * y también los datos relacionados del pasajero.
     *
     * @throws ServletException si no consigo dejar lista la conexión.
     */
    @Override
    public void init() throws ServletException {
        logger.info("Inicializando FindVueloController");
        mongoBean = new MongoBean();
        this.conexionMongoDb = mongoBean.getConexionMongoDb();

        if (this.conexionMongoDb == null || this.conexionMongoDb.getDatosbase() == null) {
            throw new ServletException("No se pudo establecer la conexión con MongoDB");
        }
    }
    /**
     * Aquí yo construyo el listado de pasajeros asociados a un identificador de vuelo.
     *
     * Lo que hago es:
     * 1. Leo del request el identificador del vuelo.
     * 2. Llamo al DAO para obtener los datos completos del pasaje y del pasajero.
     * 3. Genero una tabla HTML con la información resultante.
     * 4. Devuelvo esa página al navegador.
     *
     * Yo no modifico nada en la base de datos.
     * Mi misión aquí es solo consultar y mostrar.
     *
     * @param request contiene el identificador del vuelo enviado por el usuario.
     * @param response me permite devolver la página HTML con los pasajeros encontrados.
     * @throws ServletException si ocurre un error interno del servlet.
     * @throws IOException si ocurre un error al escribir la respuesta.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            PasajeDAO pasajeDAO = new PasajeDAO(conexionMongoDb.getDatosbase());
            String identificador = request.getParameter("identificador");
            List<Document> pasajes = pasajeDAO.obtenerDastosDelPasaje(identificador);

            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Listado de pasajeros por identificador</title>");
            out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/viewallvuelo.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='contenedor'>");

            out.println("<button type=\"button\" onclick=\"location.href='" + request.getContextPath() + "/index'\">Volver</button>");

            out.println("<h2 class='titulo'>Datos de los pasajeros</h2>");
            out.println("<table class='tabla-vuelos'>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>Identificador</th>");
            out.println("<th>Código Pasajero</th>");
            out.println("<th>Nombre Pasajero</th>");
            out.println("<th>País Pasajero</th>");
            out.println("<th>Número de Asiento</th>");
            out.println("<th>Clase</th>");
            out.println("<th>PVP Final</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");

            for(Document pasaje: pasajes){
                out.println("<tr>");
                out.println("<td>" + pasaje.getInteger("identificador") + "</td>"); 
                out.println("<td>" + pasaje.getInteger("codigo_pasajero") + "</td>");
                out.println("<td>" + pasaje.getString("nombre_pasajero") + "</td>");
                out.println("<td>" + pasaje.getString("pais_pasajero") + "</td>");
                out.println("<td>" + pasaje.getInteger("numasiento") + "</td>");
                out.println("<td>" + pasaje.getString("clase") + "</td>");
                out.println("<td>" + pasaje.getDouble("pvp_final") + "</td>");
                out.println("</tr>");
            }
            
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {
            logger.error("Error al procesar la solicitud en VueloController", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la solicitud");
        }
        
    }

}



