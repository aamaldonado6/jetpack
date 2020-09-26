package ec.edu.utpl.apptracker_f1.manejadorBdd;

import android.app.Application;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class GlobalClass extends Application {
    private double latitud;
    private double longitud;
    private double velocidad;
    private String nombreTransporte;
    private String numVehiculo;
    private String idDispositivo;
    private String tipoUs;
    //definir la fecha
    Calendar calendario = new GregorianCalendar();
    int hora,minuto,segundo,mSegundo,mes,dia,anio;
    String horaAc="",fechaAc="";



    private DatabaseReference databaseReference;


    public GlobalClass() {
   }

    public GlobalClass(double latitud, double longitud, double velocidad, String nombreTransporte, String numVehiculo, String idDispositivo, String tipoUs) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.velocidad = velocidad;
        this.nombreTransporte = nombreTransporte;
        this.numVehiculo = numVehiculo;
        this.idDispositivo = idDispositivo;
        this.tipoUs = tipoUs;
    }

    public String getTipoUs() {
        return tipoUs;
    }

    public void setTipoUs(String tipoUs) {
        this.tipoUs = tipoUs;
    }

    public String getNombreTransporte() {
        return nombreTransporte;
    }

    public String getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(String idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public void setNombreTransporte(String nombreTransporte) {
        this.nombreTransporte = nombreTransporte;
    }

    public String getNumVehiculo() {
        return numVehiculo;
    }

    public void setNumVehiculo(String numVehiculo) {
        this.numVehiculo = numVehiculo;
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

    public double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(double velocidad) {

        this.velocidad = velocidad;
    }
    public void enviarDatos(){
        //obtener la fecha
        getDate();
        //crear referencia
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("usersGTR").child("gg").setValue("holaaa");

        //mapear los elementos y enviarlos
        if (this.numVehiculo != null){

            Map<String,Object> latlang = new HashMap<>();
            latlang.put("latitud", this.latitud);
            latlang.put("longitud", this.longitud);
            latlang.put("velocidad", this.velocidad);
            latlang.put("fecha", this.fechaAc);
            latlang.put("hora", this.horaAc);
            latlang.put("cooperativa", this.nombreTransporte);
            latlang.put("numeroBus", this.numVehiculo);

            if(velocidad > 0 && velocidad <= 99 ){
                if(tipoUs == "chofer"){
                    Toast.makeText(null, "chofer", Toast.LENGTH_LONG).show();
                    //databaseReference.child("crowd/cooloja/"+this.numVehiculo+"/gps_bus").setValue(latlang);
                }else{
                    if(tipoUs == "pasajero"){
                        Toast.makeText(null, "chofer", Toast.LENGTH_LONG).show();
                    }
                }
                //databaseReference.child("crowd/cooloja/"+this.numVehiculo+"/usuario/"+this.fechaAc+"/"+this.hora+"/"+this.idDispositivo).push().setValue(latlang);

            }else{
                if (velocidad >= 100){
                    databaseReference.child("crowd/cooloja/"+this.numVehiculo+"/exceso_velocidad/"+this.fechaAc+"/"+this.idDispositivo).push().setValue(latlang);

                }
            }
        }
    }
    private void getDate(){
        //parametros para la fecha
        this.hora = calendario.get(Calendar.HOUR_OF_DAY);
        this.minuto = calendario.get(Calendar.MINUTE);
        this.segundo = calendario.get(Calendar.SECOND);
        this.mSegundo = calendario.get(Calendar.MILLISECOND);
        this.dia = calendario.get(Calendar.DAY_OF_MONTH);
        this.mes = calendario.get(Calendar.MONTH);
        this.mes = mes+1;
        this.anio = calendario.get(Calendar.YEAR);
        this.horaAc=hora+":"+minuto+":"+segundo;
        this.fechaAc=dia+"-"+mes+"-"+anio;
    }

}
