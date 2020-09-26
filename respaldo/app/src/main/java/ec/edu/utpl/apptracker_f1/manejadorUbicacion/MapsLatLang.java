package ec.edu.utpl.apptracker_f1.manejadorUbicacion;

public class MapsLatLang {
    private double latitud;
    private double longitud;

    public MapsLatLang() {
    }

    public MapsLatLang(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

}
