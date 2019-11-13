package ec.edu.utpl.apptracker_f1.manejador;

import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import ec.edu.utpl.apptracker_f1.MainActivity;
import ec.edu.utpl.apptracker_f1.R;
import ec.edu.utpl.apptracker_f1.menuFragment.Info;
import ec.edu.utpl.apptracker_f1.menuFragment.InicioMenu;

public class GpsUbicacion implements LocationListener {
    MainActivity mainActivity;
    private DatabaseReference mDatabase;
    Calendar calendario = new GregorianCalendar();
    int hora,minuto,segundo,mSegundo,mes,dia,anio;
    InicioMenu inicioMenu = new InicioMenu();
    public MainActivity getMainActivity(){
        return mainActivity;
    }
    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void onLocationChanged(Location location) {
        //parametros para la hora
        hora = calendario.get(Calendar.HOUR_OF_DAY);
        minuto = calendario.get(Calendar.MINUTE);
        segundo = calendario.get(Calendar.SECOND);
        mSegundo = calendario.get(Calendar.MILLISECOND);
        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        mes = mes+1;
        anio = calendario.get(Calendar.YEAR);
        String horaAc=hora+":"+minuto+":"+segundo+":"+mSegundo;
        String fechaAc=dia+"-"+mes+"-"+anio;

        //obtener id del dispositivo
        String myIMEI = Settings.Secure.getString(mainActivity.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        //obtener la velocidad en km/h ,altitud y longitud
        double speed=((location.getSpeed()*3600)/1000);
        double sp = speed;
        double la = location.getLatitude();
        double lo = location.getLongitude();
        mainActivity.latitudM=la;
        mainActivity.longitudM=lo;
        mainActivity.velocidadM=sp;
        System.out.println(sp+"ssssssssssssssssssssssssssssss");
        System.out.println(mainActivity.velocidadM+"k,");



        Bundle bundle = new Bundle();
        bundle.putString("amount", "v");

        //this.mainActivity.gpsLoc(location);


    }
    View view;
    private void guardarDatosExceso(String myIMEI,String sp,double la,double lo,String hora,String fecha) {

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
    private void guardarDatos(String myIMEI,String sp,double la,double lo,String hora,String fecha) {

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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
