package controlador;

import java.io.IOException;
import java.io.PrintWriter;

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
 * Yo soy el controller que busca un vuelo concreto por su identificador.
 *
 * Entro en acción cuando el usuario selecciona o escribe un identificador
 * y pulsa el botón para buscar.
 *
 * Mi misión es recibir ese dato en el request, consultar el DAO
 * y devolver una pantalla HTML con el vuelo encontrado.
 *
 * Por eso, yo represento muy bien este recorrido:
 * JSP o formulario -> botón buscar -> Controller -> request -> DAO -> HTML de resultado.
 */
@WebServlet(name = "FindVueloController", urlPatterns = { "/findvuelo" })
public class FindVueloController extends HttpServlet {
    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = LoggerFactory.getLogger(FindVueloController.class);

    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;
    /**
     * Aquí yo preparo la conexión con MongoDB antes de buscar vuelos.
     *
     * Sin esa conexión no podría consultar la información necesaria
     * para enseñar el vuelo solicitado por el usuario.
     *
     * @throws ServletException si no consigo dejar preparada la conexión.
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
     * Aquí yo proceso la búsqueda de un vuelo por identificador.
     *
     * Paso a paso:
     * 1. Leo del request el identificador que ha enviado el usuario.
     * 2. Se lo paso al DAO para buscar el vuelo junto con sus datos de aeropuerto.
     * 3. Si lo encuentro, genero una tabla HTML con la información.
     * 4. Si no lo encuentro, muestro un mensaje de que no existe.
     *
     * De manera sencilla:
     * el usuario pide un vuelo,
     * yo recojo la petición,
     * el DAO busca,
     * y después yo construyo la página de resultado.
     *
     * @param request contiene el identificador del vuelo que el usuario quiere consultar.
     * @param response me permite devolver la página HTML con el resultado de la búsqueda.
     * @throws ServletException si ocurre un error interno del servlet.
     * @throws IOException si ocurre un error al escribir la respuesta.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            VueloDAO vueloDAO = new VueloDAO(conexionMongoDb.getDatosbase());

            String identificador = request.getParameter("identificador");
            Document vuelo = vueloDAO.obtenerVueloConAeropuertoPorIdentificador(identificador);

            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Vuelo encontrado</title>");
            out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/viewallvuelo.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='contenedor'>");

            out.println("<button type=\"button\" onclick=\"location.href='" + request.getContextPath() + "/index'\">Volver</button>");

            out.println("<h2 class='titulo'>Datos del vuelo</h2>");
            out.println("<table class='tabla-vuelos'>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>Identificador</th>");
            out.println("<th>Aeropuerto Origen</th>");
            out.println("<th>Ciudad Origen</th>");
            out.println("<th>Aeropuerto Destino</th>");
            out.println("<th>Ciudad Destino</th>");
            out.println("<th>País Destino</th>");
            out.println("<th>Tipo de Vuelo</th>");
            out.println("<th>Fecha de Salida</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");

            if (vuelo != null) {
                out.println("<tr>");
                out.println("<td>" + vuelo.getString("identificador") + "</td>");
                out.println("<td>" + vuelo.getString("aeropuertoorigen") + "</td>");
                out.println("<td>" + vuelo.getString("origen") + "</td>");
                out.println("<td>" + vuelo.getString("aeropuertodestino") + "</td>");
                out.println("<td>" + vuelo.getString("destino") + "</td>");
                out.println("<td>" + vuelo.getString("pais_destino") + "</td>");
                out.println("<td>" + vuelo.getString("tipovuelo") + "</td>");
                out.println("<td>" + vuelo.getString("fechavuelo") + "</td>");
                out.println("</tr>");
            } else {
                out.println("<tr>");
                out.println("<td colspan='5'>No se encontró ningún vuelo con ese identificador</td>");
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
    /**
     * Aquí yo dejo preparada una respuesta básica para peticiones POST.
     *
     * En la versión actual, la lógica principal de búsqueda del vuelo
     * está en el método doGet, porque la consulta se hace como navegación normal.
     *
     * Este método queda como apoyo o punto de ampliación futura.
     *
     * @param request contiene la petición enviada por el navegador.
     * @param response permite devolver una respuesta simple.
     * @throws ServletException si ocurre un error interno del servlet.
     * @throws IOException si ocurre un error de entrada o salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("POST /personas funcionando");
    }

}



