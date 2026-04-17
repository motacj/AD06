package controlador;

import java.io.IOException;
import java.util.List;

import org.bson.Document;

import Bean.MongoBean;
import dao.VueloDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Yo soy el controller que prepara la pantalla principal de la aplicación.
 *
 * Mi trabajo es recibir la petición inicial del navegador, pedir a la capa DAO
 * la información de los vuelos y dejarla guardada dentro del request para que
 * la JSP la pueda usar después.
 *
 * Dicho de forma muy sencilla:
 * primero entro yo,
 * después preparo datos en el request,
 * luego se abre index.jsp,
 * y desde esa pantalla el usuario pulsa los botones que lanzan nuevas
 * peticiones.
 *
 * Por eso, yo soy el punto de arranque del flujo:
 * Controller -> request -> JSP -> pulsar botones -> nuevo Controller.
 */
@WebServlet("/index")
public class IndexController extends HttpServlet {

    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;

    /**
     * Aquí yo inicializo la conexión con MongoDB antes de atender ninguna petición.
     *
     * Lo hago una sola vez al arrancar el servlet, para dejar preparada la base de
     * datos
     * y poder usarla más tarde en mis métodos.
     *
     * Si la conexión no existe, detengo el proceso porque sin base de datos
     * no puedo preparar correctamente la pantalla principal.
     *
     * @throws ServletException si no consigo dejar preparada la conexión con
     *                          MongoDB.
     */
    @Override
    public void init() throws ServletException {
        mongoBean = new MongoBean();
        this.conexionMongoDb = mongoBean.getConexionMongoDb();

        if (this.conexionMongoDb == null || this.conexionMongoDb.getDatosbase() == null) {
            throw new ServletException("No se pudo establecer la conexión con MongoDB");
        }
    }

    /**
     * Aquí yo recibo la petición GET que abre la pantalla principal.
     *
     * Paso a paso, lo que hago es esto:
     * 1. Pido al DAO la lista de vuelos.
     * 2. Guardo esa lista dentro del request.
     * 3. Reenvío la petición a index.jsp.
     * 4. La JSP muestra los datos y los botones al usuario.
     *
     * Gracias a este método, la JSP no tiene que ir sola a la base de datos,
     * porque yo ya le entrego en el request lo que necesita para pintar la
     * pantalla.
     *
     * @param request  contiene la petición que llega desde el navegador.
     * @param response permite devolver la respuesta al navegador.
     * @throws ServletException si ocurre un error interno del servlet.
     * @throws IOException      si ocurre un error de entrada o salida.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        VueloDAO vueloDAO = new VueloDAO(conexionMongoDb.getDatosbase());
        List<Document> listaVuelos = vueloDAO.obtenerVuelosConAeropuerto();

        request.setAttribute("listaVuelos", listaVuelos);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
