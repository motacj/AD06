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

@WebServlet(name = "ChangePasajeController", urlPatterns = { "/changePasaje" })
public class ChangePasajeController extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasajeController.class);

    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;

    @Override
    public void init() throws ServletException {
        logger.info("Inicializando ChangePasajeController");
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