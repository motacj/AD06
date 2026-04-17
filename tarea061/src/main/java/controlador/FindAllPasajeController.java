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
 * Yo soy el controller que muestra el listado completo de pasajes.
 *
 * Mi pantalla sirve para que el usuario vea todos los registros
 * y desde ahí pueda decidir qué quiere hacer con cada uno.
 *
 * En esa tabla aparecen botones que permiten modificar o borrar.
 * Por eso, yo no solo enseño datos, sino que también soy el punto
 * desde el que nace la navegación hacia otras acciones.
 *
 * Mi recorrido mental es este:
 * Controller -> DAO -> generación HTML del listado
 * -> usuario pulsa botones -> nuevo Controller o nueva acción.
 */
@WebServlet(name = "FindAllPasajeController", urlPatterns = { "/viewallpasajes" })
public class FindAllPasajeController extends HttpServlet {
    // Logger para registrar mensajes de informacion, advertencia y error
    // relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para
    // el registro de mensajes.
    private static final Logger logger = LoggerFactory.getLogger(FindAllPasajeController.class);

    // @Inject
    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;
    /**
    * Aquí yo preparo la conexión con MongoDB antes de mostrar el listado de pasajes.
    *
    * Necesito esa conexión porque el listado se construye con datos reales
    * que vienen de la colección de pasajes.
    *
    * @throws ServletException si no consigo disponer de una conexión válida.
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
     * Aquí yo construyo la pantalla que muestra todos los pasajes.
     *
     * Este método hace dos trabajos, según lo que llegue en el request:
     *
     * - Si el usuario viene con una acción de borrado, primero elimino el pasaje indicado.
     * - Si no, consulto todos los pasajes y genero la tabla HTML del listado.
     *
     * Lo explico de forma sencilla:
     * 1. Leo del request si hay una acción y un idpasaje.
     * 2. Si la acción es borrar, llamo al DAO para eliminar el registro.
     * 3. Si no hay borrado, recupero todos los pasajes.
     * 4. Pinto la tabla HTML con los botones de modificar y borrar.
     *
     * Así, esta pantalla funciona como un punto central:
     * muestra datos, deja pulsar botones y lanza nuevas peticiones.
     *
     * @param request contiene la petición del navegador y, si existe, la acción a realizar.
     * @param response me permite devolver el HTML del listado o redirigir después de borrar.
     * @throws ServletException si ocurre un error interno del servlet.
     * @throws IOException si ocurre un error de entrada o salida.
     */
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
    /**
     * Aquí yo respondo a peticiones POST de forma básica.
     *
     * En la versión actual de esta clase, este método no desarrolla la lógica principal,
     * porque el trabajo importante del listado se hace en doGet.
     *
     * Aun así, lo dejo como punto preparado por si más adelante
     * quiero mover aquí alguna acción que venga por POST.
     *
     * @param request contiene la petición enviada por el cliente.
     * @param response permite devolver una respuesta sencilla al navegador.
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
