package modelo;

public class Aeropuerto {

    private String codaeropuerto;
    private String nombre;
    private String ciudad;
    private String pais;
    private int tasa;
    public char[] getCodaeropuerto;

        public Aeropuerto() {
    }

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
