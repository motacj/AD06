package modelo;

public class Pasaje {

    private int idpasaje;
    private int pasajerocod;
    private String identificador;
    private int numasiento;
    private String clase;
    private double pvp;



    public Pasaje() {
    }

    public Pasaje(int pasajerocod, String identificador, int numasiento, String clase, double pvp) {
        this.pasajerocod = pasajerocod;
        this.identificador = identificador;
        this.numasiento = numasiento;
        this.clase = clase;
        this.pvp = pvp;
    }
  

    public Pasaje(int idpasaje, int pasajerocod, String identificador, int numasiento, String clase, double pvp) {
        this.idpasaje = idpasaje;
        this.pasajerocod = pasajerocod;
        this.identificador = identificador;
        this.numasiento = numasiento;
        this.clase = clase;
        this.pvp = pvp;
    }

    public int getIdpasaje() {
        return idpasaje;
    }

    public void setIdpasaje(int idpasaje) {
        this.idpasaje = idpasaje;
    }

    public int getPasajerocod() {
        return pasajerocod;
    }

    public void setPasajerocod(int pasajerocod) {
        this.pasajerocod = pasajerocod;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public int getNumasiento() {
        return numasiento;
    }

    public void setNumasiento(int numasiento) {
        this.numasiento = numasiento;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public double getPvp() {
        return pvp;
    }

    public void setPvp(double pvp) {
        this.pvp = pvp;
    }
}
