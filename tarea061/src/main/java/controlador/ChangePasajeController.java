package controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Bean.MongoBean;
import dao.PasajeDAO;
import dao.PasajeroDAO;
import dao.VueloDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Pasaje;
import modelo.Vuelo;
/**
 * Yo soy el controller encargado de modificar un pasaje que ya existe.
 *
 * Mi trabajo empieza cuando el usuario está viendo el listado de pasajes
 * y pulsa el botón de modificar.
 *
 * En ese momento, la petición llega a mí con el id del pasaje.
 * Yo busco ese registro, preparo la pantalla con los datos actuales
 * y le devuelvo al usuario un formulario ya rellenado.
 *
 * Después, cuando el usuario cambia los datos y pulsa el botón de guardar,
 * vuelvo a intervenir para recoger lo escrito en el request
 * y mandarlo al DAO para actualizar la base de datos.
 *
 * Dicho de una manera muy simple, yo sigo este recorrido:
 * listado HTML -> botón modificar -> Controller -> request -> formulario HTML
 * -> botón guardar -> Controller -> DAO -> actualización final.
 */
@WebServlet(name = "ChangePasajeController", urlPatterns = { "/changePasaje" })
public class ChangePasajeController extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasajeController.class);

    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;
    /**
     * Aquí yo dejo preparada la conexión con MongoDB antes de atender peticiones.
     *
     * Lo hago al arrancar el servlet para no tener que crear la conexión cada vez
     * que un usuario entra a modificar un pasaje.
     *
     * Si la conexión no existe, no puedo seguir, porque todo mi trabajo depende
     * de poder leer y actualizar datos en la base de datos.
     *
     * @throws ServletException si no consigo dejar lista la conexión con MongoDB.
     */
    @Override
    public void init() throws ServletException {
        logger.info("Inicializando ChangePasajeController");
        mongoBean = new MongoBean();
        this.conexionMongoDb = mongoBean.getConexionMongoDb();

        if (this.conexionMongoDb == null || this.conexionMongoDb.getDatosbase() == null) {
            throw new ServletException("No se pudo establecer la conexión con MongoDB");
        }
    }
    /**
     * Aquí yo muestro el formulario de modificación de un pasaje.
     *
     * Este método se ejecuta cuando el usuario pulsa el botón "Modificar"
     * desde el listado de pasajes.
     *
     * Paso a paso, lo que hago es:
     * 1. Leo del request el id del pasaje que se quiere editar.
     * 2. Busco ese pasaje en la base de datos.
     * 3. Pido también los códigos de pasajeros y los vuelos disponibles.
     * 4. Construyo el HTML del formulario con los datos actuales ya cargados.
     * 5. Devuelvo esa pantalla al navegador.
     *
     * Yo aquí todavía no guardo cambios.
     * En esta fase solo preparo la pantalla para que el usuario pueda editar.
     *
     * @param request contiene la petición enviada desde el navegador, incluido el id del pasaje.
     * @param response me permite devolver la página HTML del formulario de modificación.
     * @throws ServletException si ocurre un error interno del servlet.
     * @throws IOException si ocurre un error al escribir la respuesta.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            String idPasajeParam = request.getParameter("idpasaje");

            if (idPasajeParam == null || idPasajeParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/viewallpasajes");
                return;
            }

            int idpasaje = Integer.parseInt(idPasajeParam);

            PasajeDAO pasajeDAO = new PasajeDAO(conexionMongoDb.getDatosbase());
            PasajeroDAO pasajeroDAO = new PasajeroDAO(conexionMongoDb.getDatosbase());
            VueloDAO vueloDAO = new VueloDAO(conexionMongoDb.getDatosbase());

            Document pasaje = pasajeDAO.obtenerPasajePorId(idpasaje);
            List<Integer> codigosPasajero = pasajeroDAO.obtenerCodigosPasajero();
            List<Vuelo> vuelos = vueloDAO.obtenerTodosLosVuelos();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Modificar pasaje</title>");
            out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/viewallvuelo.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='contenedor'>");

            out.println("<button type=\"button\" onclick=\"location.href='" + request.getContextPath()
                    + "/viewallpasajes'\">Volver</button>");

            out.println("<h2 class='titulo'>Modificar pasaje</h2>");

            if (pasaje != null) {
                out.println("<form action='" + request.getContextPath() + "/changePasaje' method='post'>");

                // idpasaje no se modifica
                out.println("<input type='hidden' name='idpasaje' value='" + pasaje.getInteger("idpasaje") + "'>");

                out.println("<table class='tabla-vuelos'>");
                out.println("<tbody>");

                // ID PASAJE SOLO VISUAL
                out.println("<tr>");
                out.println("<td><label>ID Pasaje</label></td>");
                out.println("<td>" + pasaje.getInteger("idpasaje") + "</td>");
                out.println("</tr>");

                // SELECT CODIGO PASAJERO
                out.println("<tr>");
                out.println("<td><label for='pasajerocod'>Código pasajero</label></td>");
                out.println("<td>");
                out.println("<select name='pasajerocod' id='pasajerocod' required>");

                Integer pasajeroActual = pasaje.getInteger("pasajerocod");
                for (Integer codigo : codigosPasajero) {
                    if (codigo != null && codigo.equals(pasajeroActual)) {
                        out.println("<option value='" + codigo + "' selected>" + codigo + "</option>");
                    } else {
                        out.println("<option value='" + codigo + "'>" + codigo + "</option>");
                    }
                }

                out.println("</select>");
                out.println("</td>");
                out.println("</tr>");

                // SELECT IDENTIFICADOR VUELO
                out.println("<tr>");
                out.println("<td><label for='identificador'>Identificador vuelo</label></td>");
                out.println("<td>");
                out.println("<select name='identificador' id='identificador' required>");

                String identificadorActual = pasaje.getString("identificador");
                for (Vuelo vuelo : vuelos) {
                    String identificador = vuelo.getIdentificador();

                    if (identificador != null && identificador.equals(identificadorActual)) {
                        out.println("<option value='" + identificador + "' selected>" + identificador + "</option>");
                    } else {
                        out.println("<option value='" + identificador + "'>" + identificador + "</option>");
                    }
                }

                out.println("</select>");
                out.println("</td>");
                out.println("</tr>");

                // NUMERO ASIENTO
                out.println("<tr>");
                out.println("<td><label for='numasiento'>Número de asiento</label></td>");
                out.println("<td><input type='number' name='numasiento' id='numasiento' value='"
                        + pasaje.getInteger("numasiento") + "' required></td>");
                out.println("</tr>");

                // CLASE
                // CLASE
                out.println("<tr>");
                out.println("<td><label for='clase'>Clase</label></td>");
                out.println("<td>");
                out.println("<select name='clase' id='clase' required>");

                String claseActual = pasaje.getString("clase");

                if ("TURISTA".equalsIgnoreCase(claseActual)) {
                    out.println("<option value='TURISTA' selected>TURISTA</option>");
                } else {
                    out.println("<option value='TURISTA'>TURISTA</option>");
                }

                if ("PRIMERA".equalsIgnoreCase(claseActual)) {
                    out.println("<option value='PRIMERA' selected>PRIMERA</option>");
                } else {
                    out.println("<option value='PRIMERA'>PRIMERA</option>");
                }

                if ("BUSINESS".equalsIgnoreCase(claseActual)) {
                    out.println("<option value='BUSINESS' selected>BUSINESS</option>");
                } else {
                    out.println("<option value='BUSINESS'>BUSINESS</option>");
                }

                out.println("</select>");
                out.println("</td>");
                out.println("</tr>");

                // PVP
                out.println("<tr>");
                out.println("<td><label for='pvp'>PVP</label></td>");
                out.println("<td><input type='number' step='0.01' name='pvp' id='pvp' value='"
                        + pasaje.get("pvp") + "' required></td>");
                out.println("</tr>");

                out.println("<tr>");
                out.println("<td colspan='2' style='text-align:center;'>");
                out.println("<button type='submit' style='margin-right:10px;'>Guardar cambios</button>");
                out.println("<button type='button' onclick=\"location.href='" + request.getContextPath()
                        + "/viewallpasajes'\">Cancelar</button>");
                out.println("</td>");
                out.println("</tr>");

                out.println("</tbody>");
                out.println("</table>");
                out.println("</form>");

            } else {
                out.println("<p>No se encontró ningún pasaje para modificar.</p>");
            }

            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {
            logger.error("Error al procesar la solicitud en ChangePasajeController", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la solicitud");
        }
    }
    /**
     * Aquí yo proceso el formulario de modificación cuando el usuario pulsa "Guardar cambios".
     *
     * En este momento ya no estoy mostrando la pantalla,
     * sino recogiendo lo que el usuario ha escrito.
     *
     * Lo que hago es:
     * 1. Leo del request todos los campos del formulario.
     * 2. Convierto a número los valores que han llegado como texto.
     * 3. Creo un objeto Pasaje con los nuevos datos.
     * 4. Llamo al DAO para actualizar el registro en MongoDB.
     * 5. Redirijo al listado final de pasajes.
     *
     * Dicho de forma sencilla:
     * formulario HTML -> botón guardar -> Controller -> request -> DAO -> actualización.
     *
     * @param request contiene los nuevos datos enviados desde el formulario.
     * @param response permite redirigir al listado o devolver un error si algo falla.
     * @throws ServletException si ocurre un error interno del servlet.
     * @throws IOException si ocurre un error de entrada o salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int idpasaje = Integer.parseInt(request.getParameter("idpasaje"));
            int pasajerocod = Integer.parseInt(request.getParameter("pasajerocod"));
            String identificador = request.getParameter("identificador");
            int numasiento = Integer.parseInt(request.getParameter("numasiento"));
            String clase = request.getParameter("clase");
            double pvp = Double.parseDouble(request.getParameter("pvp"));

            Pasaje pasaje = new Pasaje();
            pasaje.setIdpasaje(idpasaje);
            pasaje.setPasajerocod(pasajerocod);
            pasaje.setIdentificador(identificador);
            pasaje.setNumasiento(numasiento);
            pasaje.setClase(clase);
            pasaje.setPvp(pvp);

            PasajeDAO pasajeDAO = new PasajeDAO(conexionMongoDb.getDatosbase());
            pasajeDAO.actualizarPasaje(pasaje);

            response.sendRedirect(request.getContextPath() + "/viewallpasajes");

        } catch (Exception e) {
            logger.error("Error al actualizar el pasaje", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar el pasaje");
        }
    }
}