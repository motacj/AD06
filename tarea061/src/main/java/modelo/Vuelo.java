package modelo;

import org.bson.Document;
/**
 * Yo soy el modelo que representa un vuelo dentro de la aplicación.
 *
 * Reúno en un solo objeto la información principal de un vuelo,
 * para que otras capas puedan trabajar con ella de manera ordenada.
 */
public class Vuelo {

    private String identificador;
    private String aeropuertoorigen;
    private String aeropuertodestino;
    private String tipovuelo;
    private String fechavuelo;
    private double descuento;
    /**
     * Aquí yo creo un vuelo a partir de un documento.
     *
     * En la versión actual este constructor está vacío,
     * pero deja preparado el punto donde podría construirse el objeto
     * directamente a partir de un Document de MongoDB.
     *
     * @param document documento desde el que se quiere construir el vuelo.
     */
    public Vuelo(Document document) {
    }
    /**
    * Aquí yo creo un vuelo completo con todos sus datos principales.
    *
    * @param identificador identificador del vuelo.
    * @param aeropuertoorigen código del aeropuerto de origen.
    * @param aeropuertodestino código del aeropuerto de destino.
    * @param tipovuelo tipo de vuelo.
    * @param fechavuelo fecha del vuelo.
    * @param descuento descuento aplicado al vuelo.
    */
    public Vuelo(String identificador, String aeropuertoorigen, String aeropuertodestino, String tipovuelo,
            String fechavuelo, double descuento) {
        this.identificador = identificador;
        this.aeropuertoorigen = aeropuertoorigen;
        this.aeropuertodestino = aeropuertodestino;
        this.tipovuelo = tipovuelo;
        this.fechavuelo = fechavuelo;
        this.descuento = descuento;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getAeropuertoorigen() {
        return aeropuertoorigen;
    }

    public void setAeropuertoorigen(String aeropuertoorigen) {
        this.aeropuertoorigen = aeropuertoorigen;
    }

    public String getAeropuertodestino() {
        return aeropuertodestino;
    }

    public void setAeropuertodestino(String aeropuertodestino) {
        this.aeropuertodestino = aeropuertodestino;
    }

    public String getTipovuelo() {
        return tipovuelo;
    }

    public void setTipovuelo(String tipovuelo) {
        this.tipovuelo = tipovuelo;
    }

    public String getFechavuelo() {
        return fechavuelo;
    }

    public void setFechavuelo(String fechavuelo) {
        this.fechavuelo = fechavuelo;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

}
