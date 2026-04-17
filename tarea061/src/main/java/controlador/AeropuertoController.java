package controlador;

import java.io.IOException;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Bean.MongoBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * Yo soy un controller sencillo de prueba relacionado con la conexión y la parte de aeropuerto.
 *
 * Mi función principal, en la versión actual, no es mostrar un listado completo,
 * sino comprobar y enseñar si la conexión con MongoDB está disponible.
 *
 * Me sirve como punto de apoyo para verificar que la parte web
 * puede hablar correctamente con la base de datos.
 */
@WebServlet(name = "AeropuertoController", urlPatterns = { "/viewAllVuelos" })
public class AeropuertoController extends HttpServlet {
    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = LoggerFactory.getLogger(AeropuertoController.class);

    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;
    /**
     * Aquí yo preparo la conexión con MongoDB al arrancar el servlet.
     *
     * De esta manera, cuando llegue una petición GET,
     * ya puedo comprobar si la conexión existe y responder en consecuencia.
     *
     * @throws ServletException si ocurre un error durante la inicialización.
     */
    @Override
    public void init() throws ServletException {
        logger.info("Inicializando AeropuertoController");
        mongoBean = new MongoBean();
        this.conexionMongoDb = mongoBean.getConexionMongoDb();
    }
    /**
     * Aquí yo muestro una respuesta HTML muy simple para indicar
     * si la conexión con MongoDB se ha establecido correctamente.
     *
     * No hago todavía una consulta compleja,
     * pero sí sirvo para comprobar que la parte web y la conexión
     * están funcionando juntas.
     *
     * @param request contiene la petición enviada por el navegador.
     * @param response me permite devolver una página HTML sencilla con el resultado.
     * @throws ServletException si ocurre un error interno del servlet.
     * @throws IOException si ocurre un error de entrada o salida.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try {
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>PersonasController</title></head>");
            out.println("<body>");
            if (this.conexionMongoDb != null) {
                out.println("<h1>Conexion a MongoDB establecida correctamente</h1>");
            } else {
                out.println("<h1>Error al establecer conexion a MongoDB</h1>");
            }
            out.println("</body></html>");
        } catch (Exception e) {
            logger.error("Error al procesar la solicitud en PersonasController: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la solicitud");
        }
    }

}
