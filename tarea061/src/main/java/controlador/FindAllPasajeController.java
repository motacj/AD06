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

@WebServlet(name = "FindAllPasajeController", urlPatterns = { "/viewallpasajes" })
public class FindAllPasajeController extends HttpServlet {
    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = LoggerFactory.getLogger(FindAllPasajeController.class);

    // @Inject
    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;

    @Override
    public void init() throws ServletException {
        logger.info("Inicializando FindVueloController");
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

            PasajeDAO pasajeDAO = new PasajeDAO(conexionMongoDb.getDatosbase());
            // Opcion de borrar un pasaje
            String accion = request.getParameter("accion");
            String idPasajeParam = request.getParameter("idpasaje");

            if ("borrar".equalsIgnoreCase(accion) && idPasajeParam != null) {
                try {
                    int idpasaje = Integer.parseInt(idPasajeParam);

                    pasajeDAO.borrarPasaje(idpasaje);

                    response.sendRedirect(request.getContextPath() + "/viewallpasajes");
                    return;

                } catch (NumberFormatException e) {
                    logger.error("El idpasaje no es válido: {}", idPasajeParam, e);
                    response.sendRedirect(request.getContextPath() + "/viewallpasajes");
                    return;
                }
            }

            // Opcion de listar todos los pasajes
            List<Document> pasajes = pasajeDAO.obtenerTodosLosPasajes();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Listado de pasajes</title>");
            out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/viewallvuelo.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='contenedor'>");

            out.println("<button type=\"button\" onclick=\"location.href='" + request.getContextPath()
                    + "/index'\">Volver</button>");

            out.println("<h2 class='titulo'>Datos de los pasajes</h2>");
            out.println("<table class='tabla-vuelos'>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>Id pasaje</th>");
            out.println("<th>Código Pasajero</th>");
            out.println("<th>Identificador del Vuelo</th>");
            out.println("<th>Número de Asiento</th>");
            out.println("<th>Clase</th>");
            out.println("<th>PVP Final</th>");
            out.println("<th colspan=\"2\">Editar</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");

            for (Document pasaje : pasajes) {
                out.println("<tr>");
                out.println("<td>" + pasaje.getInteger("idpasaje") + "</td>");
                out.println("<td>" + pasaje.getInteger("pasajerocod") + "</td>");
                out.println("<td>" + pasaje.getString("identificador") + "</td>");
                out.println("<td>" + pasaje.getInteger("numasiento") + "</td>");
                out.println("<td>" + pasaje.getString("clase") + "</td>");
                out.println("<td>" + pasaje.getDouble("pvp") + "</td>");
                out.println("<td>"
                        + "<button type=\"button\" style=\"margin-right: 8px;\" onclick=\"location.href='"
                        + request.getContextPath()
                        + "/changePasaje?idpasaje="
                        + pasaje.getInteger("idpasaje")
                        + "'\">Modificar</button>"
                        + "</td>");
                out.println("<td>"
                        + "<button type=\"button\" onclick=\"location.href='"
                        + request.getContextPath()
                        + "/viewallpasajes?accion=borrar&idpasaje="
                        + pasaje.getInteger("idpasaje")
                        + "'\">Borrar</button>"
                        + "</td>");
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
