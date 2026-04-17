package modelo;
/**
 * Yo soy el modelo que representa un aeropuerto dentro de la aplicación.
 *
 * Mi función es agrupar los datos principales de un aeropuerto
 * en un único objeto fácil de manejar.
 */
public class Aeropuerto {

    private String codaeropuerto;
    private String nombre;
    private String ciudad;
    private String pais;
    private int tasa;
    public char[] getCodaeropuerto;
    /**
     * Aquí yo creo un aeropuerto vacío.
     *
     * Este constructor me sirve cuando quiero crear el objeto primero
     * y rellenarlo después con setters.
     */
    public Aeropuerto() {
    }
    /**
     * Aquí yo creo un aeropuerto completo con todos sus datos principales.
     *
     * @param codaeropuerto código del aeropuerto.
     * @param nombre nombre del aeropuerto.
     * @param ciudad ciudad del aeropuerto.
     * @param pais país del aeropuerto.
     * @param tasa tasa asociada al aeropuerto.
     */
    public Aeropuerto(String codaeropuerto, String nombre, String ciudad, String pais, int tasa) {
        this.codaeropuerto = codaeropuerto;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.pais = pais;
        this.tasa = tasa;
    }

    // Getters and setters
    public String getCodaeropuerto() {
        return codaeropuerto;
    }

    public void setCodaeropuerto(String codaeropuerto) {
        this.codaeropuerto = codaeropuerto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public int getTasa() {
        return tasa;
    }

    public void setTasa(int tasa) {
        this.tasa = tasa;
    }

}
