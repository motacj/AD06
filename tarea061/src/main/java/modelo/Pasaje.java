package modelo;
/**
 * Yo soy el modelo que representa un pasaje dentro de la aplicación.
 *
 * Mi función es guardar en un solo objeto toda la información
 * que identifica una reserva o asiento dentro de un vuelo.
 *
 * Gracias a mí, los controllers y los DAO pueden intercambiar datos
 * de una manera más ordenada y más fácil de entender.
 */
public class Pasaje {

    private int idpasaje;
    private int pasajerocod;
    private String identificador;
    private int numasiento;
    private String clase;
    private double pvp;


    /**
     * Aquí yo creo un pasaje vacío.
     *
     * Este constructor me sirve cuando primero quiero crear el objeto
     * y después rellenar sus campos poco a poco con setters.
     */
    public Pasaje() {
    }
    /**
     * Aquí yo creo un pasaje nuevo sin id, pensado normalmente para inserciones.
     *
     * El id puede generarse más tarde en el DAO
     * justo antes de guardar el registro en MongoDB.
     *
     * @param pasajerocod código del pasajero.
     * @param identificador identificador del vuelo.
     * @param numasiento número de asiento.
     * @param clase clase del pasaje.
     * @param pvp precio del pasaje.
     */
    public Pasaje(int pasajerocod, String identificador, int numasiento, String clase, double pvp) {
        this.pasajerocod = pasajerocod;
        this.identificador = identificador;
        this.numasiento = numasiento;
        this.clase = clase;
        this.pvp = pvp;
    }
  
    /**
     * Aquí yo creo un pasaje completo con todos sus datos, incluido el id.
     *
     * Este constructor resulta útil cuando ya conozco el identificador del registro,
     * por ejemplo al actualizar o reconstruir un pasaje existente.
     *
     * @param idpasaje identificador del pasaje.
     * @param pasajerocod código del pasajero.
     * @param identificador identificador del vuelo.
     * @param numasiento número de asiento.
     * @param clase clase del pasaje.
     * @param pvp precio del pasaje.
     */
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
