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


@WebServlet(name = "VueloController", urlPatterns = { "/viewallvuelos" })
public class VueloController extends HttpServlet {
    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = LoggerFactory.getLogger(VueloController.class);

    // @Inject
    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;

    @Override
    public void init() throws ServletException {
        logger.info("Inicializando VueloController");
        mongoBean = new MongoBean();
        this.conexionMongoDb = mongoBean.getConexionMongoDb();

        if (this.conexionMongoDb == null || this.conexionMongoDb.getDatosbase() == null) {
            throw new ServletException("No se pudo establecer la conexión con MongoDB");
        }
    }

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("POST /personas funcionando");
    }

}
