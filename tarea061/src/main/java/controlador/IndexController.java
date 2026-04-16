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

@WebServlet("/index")
public class IndexController extends HttpServlet {

    private MongoBean mongoBean;
    private ConexionMongoDb conexionMongoDb;

    @Override
    public void init() throws ServletException {
        mongoBean = new MongoBean();
        this.conexionMongoDb = mongoBean.getConexionMongoDb();

        if (this.conexionMongoDb == null || this.conexionMongoDb.getDatosbase() == null) {
            throw new ServletException("No se pudo establecer la conexión con MongoDB");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        VueloDAO vueloDAO = new VueloDAO(conexionMongoDb.getDatosbase());
        List<Document> listaVuelos = vueloDAO.obtenerVuelosConAeropuerto();

        request.setAttribute("listaVuelos", listaVuelos);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
