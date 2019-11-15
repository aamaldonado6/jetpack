package ec.edu.utpl.apptracker_f1.manejadorBdd;

import android.provider.Settings;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import ec.edu.utpl.apptracker_f1.MainActivity;

public class ManejadorBdd {

    private DatabaseReference mDatabase;
    MainActivity mainActivity;
    //obtener id del dispositivo
    String myIMEI = Settings.Secure.getString(mainActivity.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    public void guardarDatosExceso(String sp,double la,double lo,String hora,String fecha) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Map<String,Object> datos = new HashMap<>();
        datos.put("latitud",la);
        datos.put("longitud",lo);
        datos.put("velocidad",sp);
        datos.put("hora",hora);
        datos.put("fecha",fecha);
        datos.put("idDisposi",myIMEI);
        datos.put("reportado",0);
        mDatabase.child(myIMEI).child("excesoVelocidad").push().setValue(datos);


    }
    public void guardarDatos(String sp,double la,double lo,String hora,String fecha) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Map<String,Object> datos = new HashMap<>();
        datos.put("latitud",la);
        datos.put("longitud",lo);
        datos.put("velocidad",sp);
        datos.put("hora",hora);
        datos.put("fecha",fecha);
        datos.put("idDisposi",myIMEI);
        mDatabase.child(myIMEI).child("DatosGPS").push().setValue(datos);


    }
    public void guardarD(){
    }
}
