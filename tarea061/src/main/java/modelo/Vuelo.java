package modelo;

import org.bson.Document;

public class Vuelo {

    private String identificador;
    private String aeropuertoorigen;
    private String aeropuertodestino;
    private String tipovuelo;
    private String fechavuelo;
    private double descuento;

    public Vuelo(Document document) {
    }

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
