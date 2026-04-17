package controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Bean.MongoBean;
import dao.VueloDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Yo soy el controller que muestra el listado general de vuelos.
 *
 * Mi misión es consultar todos los vuelos disponibles,
 * pedir también sus datos relacionados de aeropuerto
 * y construir la pantalla HTML que el usuario ve en el navegador.
 *
 * Esta pantalla suele ser una de las más importantes,
 * porque desde ella el usuario entiende qué vuelos existen
 * y desde ahí puede seguir navegando por la aplicación.
 */
@WebServlet(name = "VueloController", urlPatterns = { "/viewallvuelos" })
public class VueloController extends HttpServlet {
    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = LoggerFactory.getLogger(VueloController.class);

    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;
    /**
     * Aquí yo preparo la conexión con MongoDB antes de listar los vuelos.
     *
     * Necesito tener esa conexión lista para poder pedir al DAO
     * todos los datos que después convertiré en una tabla HTML.
     *
     * @throws ServletException si no consigo disponer de una conexión válida.
     */
    @Override
    public void init() throws ServletException {
        logger.info("Inicializando VueloController");
        mongoBean = new MongoBean();
        this.conexionMongoDb = mongoBean.getConexionMongoDb();

        if (this.conexionMongoDb == null || this.conexionMongoDb.getDatosbase() == null) {
            throw new ServletException("No se pudo establecer la conexión con MongoDB");
        }
    }
    /**
     * Aquí yo construyo la pantalla del listado de vuelos.
     *
     * Lo hago así:
     * 1. Pido al DAO la lista de vuelos junto con sus datos relacionados.
     * 2. Recorro esos resultados.
     * 3. Genero el HTML de la tabla.
     * 4. Devuelvo la página al navegador.
     *
     * Yo actúo como puente entre la base de datos y la pantalla.
     * El DAO me da los datos y yo los convierto en una vista comprensible.
     *
     * @param request contiene la petición que llega desde el navegador.
     * @param response me permite escribir la página HTML del listado de vuelos.
     * @throws ServletException si ocurre un error interno del servlet.
     * @throws IOException si ocurre un error de entrada o salida.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            VueloDAO vueloDAO = new VueloDAO(conexionMongoDb.getDatosbase());
            List<Document> vuelos = vueloDAO.obtenerVuelosConAeropuerto();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Listado de Vuelos</title>");
            out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/viewallvuelo.css'>");
            out.println("</head>");
            out.println("<div class='contenedor'>");
            out.println("<body>");            
            out.println("<button type=\"button\" onclick=\"location.href='http://localhost:8081/tarea061/index'\">Visualizar todos los vuelos</button>");
            
            out.println("<h2 class='titulo'>Listado de Vuelos</h2>");
            out.println("<table class='tabla-vuelos'>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>Identificador</th>");
            out.println("<th>Origen Vuelo</th>");
            out.println("<th>Destino Vuelo</th>");
            out.println("<th>Tipo</th>");
            out.println("<th>Fecha de Salida</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");

            for (Document vuelo : vuelos) {
                out.println("<tr>");
                out.println("<td>" + vuelo.getString("identificador") + "</td>");
                out.println("<td>" + vuelo.getString("origen") + "</td>");
                out.println("<td>" + vuelo.getString("destino") + "</td>");
                out.println("<td>" + vuelo.getString("tipovuelo") + "</td>");
                out.println("<td>" + vuelo.getString("fechavuelo") + "</td>");
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
