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
 * Yo soy el controller encargado del alta de pasajes.
 *
 * En mi caso hago dos trabajos distintos:
 * - si me llaman por GET, yo construyo y muestro el formulario de inserción;
 * - si me llaman por POST, yo recojo lo que el usuario ha escrito y mando
 * guardar el pasaje.
 *
 * Mi recorrido mental es este:
 * IndexController -> request -> index.jsp -> botón "Insertar pasaje"
 * -> InsertPasajeController -> DAO -> generación de HTML o guardado final.
 *
 * Lo explico así porque quiero dejar claro que yo soy el puente entre la
 * pantalla
 * que ve el usuario y la lógica que termina guardando datos en MongoDB.
 */
@WebServlet(name = "InsertPasajeController", urlPatterns = { "/insertpasaje" })
public class InsertPasajeController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(InsertPasajeController.class);

    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;

    /**
     * Aquí yo preparo la conexión con MongoDB para poder trabajar con los datos del
     * pasaje.
     *
     * Antes de mostrar formularios o guardar información, necesito asegurarme
     * de que la conexión existe y está lista para usarse.
     *
     * Si esta preparación falla, no continúo, porque el alta del pasaje
     * depende completamente de la base de datos.
     *
     * @throws ServletException si no consigo establecer correctamente la conexión
     *                          con MongoDB.
     */
    @Override
    public void init() throws ServletException {
        logger.info("Inicializando InsertPasajeController");
        mongoBean = new MongoBean();
        this.conexionMongoDb = mongoBean.getConexionMongoDb();

        if (this.conexionMongoDb == null || this.conexionMongoDb.getDatosbase() == null) {
            throw new ServletException("No se pudo establecer la conexión con MongoDB");
        }
    }

    /**
     * Aquí yo muestro el formulario de inserción de pasajes.
     *
     * Cuando el usuario pulsa en index.jsp el botón "Insertar pasaje",
     * la petición llega a este método por GET.
     *
     * Entonces yo hago esto, paso a paso:
     * 1. Consulto los pasajeros para rellenar el desplegable.
     * 2. Consulto los vuelos para rellenar el otro desplegable.
     * 3. Genero el HTML del formulario con out.println(...).
     * 4. Devuelvo ese HTML al navegador para que el usuario lo vea.
     *
     * Es importante entender que en este punto yo no guardo nada todavía.
     * Aquí solo preparo la pantalla y los botones para que el usuario pueda
     * escribir los datos.
     *
     * @param request  contiene la petición enviada desde el navegador.
     * @param response permite escribir la página HTML que voy a devolver.
     * @throws ServletException si ocurre un error interno del servlet.
     * @throws IOException      si ocurre un error al escribir la respuesta.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            PasajeDAO pasajeDAO = new PasajeDAO(conexionMongoDb.getDatosbase());
            PasajeroDAO pasajeroDAO = new PasajeroDAO(conexionMongoDb.getDatosbase());
            VueloDAO vueloDAO = new VueloDAO(conexionMongoDb.getDatosbase());

            List<Document> listadopasajeros = pasajeroDAO.obtenerTodosPasajeros();
            List<Vuelo> listadovuelos = vueloDAO.obtenerTodosLosVuelos();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Insertar pasaje</title>");
            out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/viewallvuelo.css'>");
            out.println("</head>");
            out.println("<body>");

            // Script para mostrar el nombre del pasajero seleccionado en el select
            out.println("<script>");
            out.println("function mostrarNombrePasajero() {");
            out.println("    var select = document.getElementById('pasajerocod');");
            out.println("    var opcionSeleccionada = select.options[select.selectedIndex];");
            out.println("    var nombre = opcionSeleccionada.getAttribute('data-nombre');");
            out.println("    document.getElementById('nombrePasajero').innerText = nombre ? nombre : '';");
            out.println("}");
            out.println("window.onload = mostrarNombrePasajero;");
            out.println("</script>");

            out.println("<div class='contenedor'>");

            out.println("<button type=\"button\" onclick=\"location.href='" + request.getContextPath()
                    + "/viewallpasajes'\">Volver</button>");

            out.println("<h2 class='titulo'>Insertar pasaje</h2>");

            out.println("<form action='" + request.getContextPath() + "/insertpasaje' method='post'>");

            out.println("<table class='tabla-vuelos'>");
            out.println("<tbody>");

            // SELECT CODIGO PASAJERO
            out.println("<tr>");
            out.println("<td><label for='pasajerocod'>Código pasajero</label></td>");
            out.println("<td>");
            out.println("<select name='pasajerocod' id='pasajerocod' onchange='mostrarNombrePasajero()' required>");

            for (Document pasajero : listadopasajeros) {
                Integer codigo = pasajero.getInteger("pasajerocod");
                String nombre = pasajero.getString("nombre");

                if (codigo != null) {
                    out.println("<option value='" + codigo + "' data-nombre='" + nombre + "'>"
                            + codigo + "</option>");
                }
            }

            out.println("</select>");
            out.println("</td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td><label>Nombre pasajero</label></td>");
            out.println("<td><span id='nombrePasajero'></span></td>");
            out.println("</tr>");

            // SELECT IDENTIFICADOR VUELO
            out.println("<tr>");
            out.println("<td><label for='identificador'>Identificador vuelo</label></td>");
            out.println("<td>");
            out.println("<select name='identificador' id='identificador' required>");

            for (Vuelo vuelo : listadovuelos) {
                String identificador = vuelo.getIdentificador();
                if (identificador != null) {
                    out.println("<option value='" + identificador + "'>" + identificador + "</option>");
                }
            }

            out.println("</select>");
            out.println("</td>");
            out.println("</tr>");

            // NUMERO ASIENTO
            out.println("<tr>");
            out.println("<td><label for='numasiento'>Número de asiento</label></td>");
            out.println("<td><input type='number' name='numasiento' id='numasiento' value='0' required></td>");
            out.println("</tr>");

            // CLASE
            out.println("<tr>");
            out.println("<td><label>Clase</label></td>");
            out.println("<td>");
            out.println("<input type='radio' name='clase' id='turista' value='TURISTA' " + " required>");
            out.println("<label for='turista'>TURISTA</label>");
            out.println("&nbsp;&nbsp;");
            out.println("<input type='radio' name='clase' id='primera' value='PRIMERA' " + ">");
            out.println("<label for='primera'>PRIMERA</label>");
            out.println("&nbsp;&nbsp;");
            out.println("<input type='radio' name='clase' id='business' value='BUSINESS' " + ">");
            out.println("<label for='business'>BUSINESS</label>");
            out.println("</td>");
            out.println("</tr>");

            // PVP
            out.println("<tr>");
            out.println("<td><label for='pvp'>PVP</label></td>");
            out.println("<td><input type='number' step='0.01' name='pvp' id='pvp' value='0.00' required></td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td colspan='2' style='text-align:center;'>");
            out.println("<button type='submit' style='margin-right:10px;'>Insertar pasaje</button>");
            out.println("<button type='button' onclick=\"location.href='" + request.getContextPath()
                    + "/viewallpasajes'\">Cancelar</button>");
            out.println("</td>");
            out.println("</tr>");
            out.println("</tbody>");
            out.println("</table>");
            out.println("</form>");

            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {
            logger.error("Error al procesar la solicitud en ChangePasajeController", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la solicitud");
        }
    }

    /**
     * Aquí yo proceso el envío del formulario de inserción.
     *
     * Este método entra en acción cuando el usuario ya ha rellenado el formulario
     * y pulsa el botón "Insertar pasaje".
     *
     * Lo que hago es lo siguiente:
     * 1. Leo del request los valores enviados por el formulario.
     * 2. Convierto a número los campos que llegan como texto.
     * 3. Compruebo en el DAO si el asiento ya está ocupado en ese vuelo.
     * 4. Si está ocupado, genero una página HTML de error para avisar al usuario.
     * 5. Si está libre, creo el objeto Pasaje y se lo paso al DAO para insertarlo.
     * 6. Al final redirijo al listado de pasajes.
     *
     * En otras palabras, aquí ocurre la parte más importante del flujo:
     * pulsar botón -> Controller -> request -> DAO -> guardado o error -> respuesta
     * HTML.
     *
     * @param request  contiene los datos que el usuario ha enviado desde el
     *                 formulario.
     * @param response permite devolver una página de error o redirigir al listado
     *                 final.
     * @throws ServletException si ocurre un error interno del servlet.
     * @throws IOException      si ocurre un error de entrada o salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try {
            int pasajerocod = Integer.parseInt(request.getParameter("pasajerocod"));
            String identificador = request.getParameter("identificador");
            int numasiento = Integer.parseInt(request.getParameter("numasiento"));
            String clase = request.getParameter("clase");
            double pvp = Double.parseDouble(request.getParameter("pvp"));

            PasajeDAO pasajeDAO = new PasajeDAO(conexionMongoDb.getDatosbase());

            // Comprobar si ese asiento ya está ocupado en ese vuelo
            boolean asientoOcupado = pasajeDAO.existeAsientoEnVuelo(identificador, numasiento);

            if (asientoOcupado) {
                try (PrintWriter out = response.getWriter()) {
                    out.println("<!DOCTYPE html>");
                    out.println("<html lang='es'>");
                    out.println("<head>");
                    out.println("<meta charset='UTF-8'>");
                    out.println("<title>Error al insertar pasaje</title>");
                    out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/viewallvuelo.css'>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<div class='contenedor'>");

                    out.println("<h2 class='titulo'>Error al insertar pasaje</h2>");
                    out.println("<p>El asiento <strong>" + numasiento + "</strong> ya está ocupado en el vuelo <strong>"
                            + identificador + "</strong>.</p>");

                    out.println("<div style='margin-top:20px;'>");
                    out.println(
                            "<button type='button' style='margin-right:10px;' onclick='history.back()'>Volver</button>");
                    out.println("<button type='button' onclick=\"location.href='" + request.getContextPath()
                            + "/viewallpasajes'\">Ir al listado</button>");
                    out.println("</div>");

                    out.println("</div>");
                    out.println("</body>");
                    out.println("</html>");
                }
                return;
            }

            // Crear el objeto sin idpasaje
            Pasaje pasaje = new Pasaje(pasajerocod, identificador, numasiento, clase, pvp);

            pasajeDAO.insertarPasaje(pasaje);

            response.sendRedirect(request.getContextPath() + "/viewallpasajes");

        } catch (NumberFormatException e) {
            logger.error("Error al convertir datos numéricos del formulario", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos numéricos no válidos");

        } catch (Exception e) {
            logger.error("Error al insertar el pasaje", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al insertar el pasaje");
        }
    }
}