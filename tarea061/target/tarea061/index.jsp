<%@ page import="java.util.List" %>
<%@ page import="org.bson.Document" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestión de vuelos</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css">
</head>
<body>
    <div class="contenedor">
        <h1>Gestión de vuelos</h1>

        <!-- FORMULARIO 1: visualizar todos los vuelos -->
        <form method="get" action="${pageContext.request.contextPath}/viewallvuelos">
            <input type="submit" value="Visualizar todos los vuelos">
        </form>

        <br>

        <!-- FORMULARIO 2: buscar un vuelo o ver el pasaje de un vuelo concreto -->
        <form method="get">
            <div class="fila-flex">
                <label for="identificador">Seleccionar un vuelo por identificador</label>

                <%
                    List<Document> listaVuelos = (List<Document>) request.getAttribute("listaVuelos");
                %>

                <select name="identificador" id="identificador">
                    <option value="">-- Selecciona un vuelo --</option>
                    <%
                        if (listaVuelos != null) {
                            for (Document vuelo : listaVuelos) {
                    %>
                        <option value="<%= vuelo.getString("identificador") %>">
                            <%= vuelo.getString("identificador") %>
                        </option>
                    <%
                            }
                        }
                    %>
                </select>

                <input type="submit" value="Buscar vuelo" formaction="${pageContext.request.contextPath}/findvuelo">
                <input type="submit" value="Lista del pasaje" formaction="${pageContext.request.contextPath}/findpasaje">
            </div>
        </form>

        <br>

        <!-- FORMULARIO 3: visualizar todos los pasajes -->
        <form method="get" action="${pageContext.request.contextPath}/viewallpasajes">
            <input type="submit" value="Visualizar todos los pasajes">
        </form>

        <br>

        <!-- FORMULARIO 4: insertar pasaje -->
        <form method="get" action="${pageContext.request.contextPath}/insertpasaje">
            <input type="submit" value="Insertar pasaje">
        </form>

    </div>
</body>
</html>