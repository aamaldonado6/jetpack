package ec.edu.utpl.apptracker_f1.manejadorUbicacion;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ec.edu.utpl.apptracker_f1.MainActivity;
import ec.edu.utpl.apptracker_f1.manejadorBdd.ManejadorBdd;

public class GpsUbicacion implements LocationListener {
    MainActivity mainActivity;
    ManejadorBdd manejadorBdd;
    Calendar calendario = new GregorianCalendar();
    int hora,minuto,segundo,mSegundo,mes,dia,anio;
    String horaAc="",fechaAc="";
    public MainActivity getMainActivity(){
        return mainActivity;
    }
    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(null,"Esta app necesita todos los permisos solicitados para poder funcionar (Localizacion, Cámara)",Toast.LENGTH_SHORT).show();

        //obtener la fecha
        getDate();
        //codigo guardar en la respectiva raiz de la base de datos
        String codigoBdd=null;
        //obtener la velocidad en km/h ,altitud y longitud
        double speed=((location.getSpeed()*3600)/1000);
        double sp = speed;
        double la = location.getLatitude();
        double lo = location.getLongitude();
        mainActivity.txtVelocidad.setText(String.format("%.2f",sp));
        System.out.println(la);
        System.out.println(speed);
        /*if (speed < 6.5){
            codigoBdd="DatosGPS";
            mainActivity.txtVelocidad.setText("0.00");
            manejadorBdd.guardarDatos(String.format("%.4f",sp),la,lo,horaAc,fechaAc,codigoBdd);
        }else {
            codigoBdd="excesoVelocidad";
            mainActivity.txtVelocidad.setText(String.format("%.2f",sp));
            if (speed >= 100){
                manejadorBdd.guardarDatos(String.format("%.4f",sp),la,lo,horaAc,fechaAc,codigoBdd);}}*/
    }
    private void getDate(){
        //parametros para la fecha
        hora = calendario.get(Calendar.HOUR_OF_DAY);
        minuto = calendario.get(Calendar.MINUTE);
        segundo = calendario.get(Calendar.SECOND);
        mSegundo = calendario.get(Calendar.MILLISECOND);
        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        mes = mes+1;
        anio = calendario.get(Calendar.YEAR);
        horaAc=hora+":"+minuto+":"+segundo+":"+mSegundo;
        fechaAc=dia+"-"+mes+"-"+anio;
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
