<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestión de vuelos</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/formulario.css">
</head>
<body>
    <div class="contenedor">
        <h1>Gestión de vuelos</h1>

        <form action="#" method="get">
            <ul>
                <li>
                    <button type="button" onclick="location.href='viewallvuelos'">
                        Visualizar todos los vuelos
                    </button>
                </li>

                <li>
                    <div class="fila-flex">
                        <label for="vuelo">Seleccionar un vuelo por identificador</label>
                        <select name="vuelo" id="vuelo">
                            <option value="V001">V001</option>
                            <option value="V002">V002</option>
                            <option value="V003">V003</option>
                            <option value="V004">V004</option>
                        </select>
                        <input type="submit" value="Buscar">
                    </div>
                </li>

                <li>
                    <a href="http://localhost:8081/tarea061/aeropuerto">Modificar / borrar los pasajes</a>
                </li>

                <li>
                    <a href="#">Insertar nuevos pasajes</a>
                </li>
            </ul>
        </form>
    </div>
</body>
</html>