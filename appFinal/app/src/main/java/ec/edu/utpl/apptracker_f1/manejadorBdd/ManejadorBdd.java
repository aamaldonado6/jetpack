package ec.edu.utpl.apptracker_f1.manejadorBdd;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import androidx.preference.PreferenceManager;
import ec.edu.utpl.apptracker_f1.MainActivity;
import ec.edu.utpl.apptracker_f1.menuFragment.InicioMenu;

public class ManejadorBdd {
    //declaracion
    private static ManejadorBdd instancia;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    int hora,minuto,segundo,mSegundo,mes,dia,anio;
    String horaAc="",fechaAc="";

    MainActivity mainActivity;
    public MainActivity getMainActivity(){
        return mainActivity;
    }
    CountDownTimer countDownTimer;
    CountDownTimer countDownTimerMov;
    CountDownTimer countDownTimerMovNormal;
    CountDownTimer countDownTimerMovExceso;


    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }
    //evitar instanciar con new
    private ManejadorBdd(){
    }

    //obtener la instancia unica
    public static ManejadorBdd getInstance(){
        if(instancia == null){
            instancia = new ManejadorBdd();
        }
        return instancia;
    }

    public void guardarReporte(boolean r1, boolean r2, boolean r3, boolean r4, boolean r5, String coment, GlobalClass gClass) {
        getDate();
        if(gClass.getNombreTransporte() != null){
            String idUs = MainActivity.bundle.getString("idUsuario",null);
            Map<String,Object> datos = new HashMap<>();
            datos.put("comentario",coment);
            datos.put("rebasa",r1);
            datos.put("recoge",r2);
            datos.put("estacionado",r3);
            datos.put("gasolina",r4);
            datos.put("bueno",r5);
            try {
                mDatabase.child("bus/"+gClass.getNombreTransporte()+"/autobus/"+gClass.getNumVehiculo()+"/reporte/"+idUs+"/"+fechaAc).setValue(datos);
            }catch (Exception e){
            }
        }


    }

    public void datosRecorrido(GlobalClass gClass){
        //obener la fecha
        getDate();
        DecimalFormat formato1 = new DecimalFormat("#.##");
        String velocidad = formato1.format(gClass.getVelocidad());
        double numberVeloc = gClass.getVelocidad();
        //codigo para saber que usuario esta ocupando la app
        String codUs = MainActivity.bundle.getString("codUsuario","nonUs");

        //crear valores para guardar en la bdd
        //obtener el id del usuario
        String idUs = MainActivity.bundle.getString("idUsuario",null);
        Map<String,Object> datos = new HashMap<>();
        datos.put("latitud",gClass.getLatitud());
        datos.put("longitud",gClass.getLongitud());
        datos.put("idusuario",idUs);
        datos.put("velocidad",numberVeloc);
        datos.put("hora",horaAc);
        datos.put("fecha",fechaAc);

        String nombreTrans =gClass.getNombreTransporte();
        String numVehiculo =gClass.getNumVehiculo();
        String tipodeUsuario=gClass.getTipoUs();
        String ruta =gClass.getRuta();

        String tipUs = "none";
        if(tipodeUsuario != null){
            tipUs=tipodeUsuario;
        }
        // if(numberVeloc<5){
        if(20<5){
            //envia la velocidad a la pantalla del usuario
            mainActivity.txtVelocidad.setText("....");
        }else{

            mainActivity.txtVelocidad.setText(velocidad);
            if( numberVeloc < 90 ){
                if(tipUs.equals("chofer") || tipUs.equals("pasajero")){
                    contadorVelocidadNormal(nombreTrans,numVehiculo,datos,idUs,tipUs);
                }else {
                    mDatabase.child("nonus/usuario/"+fechaAc+"/"+hora+"/"+idUs).setValue(datos);
                }

            }else{
                if (numberVeloc >= 90){
                    if(tipUs.equals("chofer")){
                        contadorExcesoVelocidad(nombreTrans,numVehiculo,datos,idUs,tipUs);
                    }else if(!tipUs.equals("pasajero")){
                        mDatabase.child("nonus/usuario/"+fechaAc+"/"+hora+"/"+idUs).setValue(datos);
                    }
                }
            }
        }

    }

    public void contadorVelocidadNormal(final String nombreTrans, final String numVehiculo, final Map<String, Object> datos, final String idUs, final String tipUs){
        if(tipUs.equals("chofer")){
            //guarda la posicion actual del transporte en un solo campo
            mDatabase.child("bus/"+nombreTrans+"/autobus/"+numVehiculo+"/datos_gps_actual").setValue(datos);
        }
        //guarda la posicion actual del transporte en muchos campos cada 10 segundos
        if(countDownTimerMovNormal == null){
            //establecer el limite de tiempo
            countDownTimerMovNormal= new CountDownTimer(10000, 1000) {
                //interacciones en cada intervalo
                public void onTick(long millisUntilFinished) {
                    //Log.e("seconds remaining: " ,""+horaAc);
                }
                //finalizacion del contador
                public void onFinish() {
                    //hacer la escritura si el usuario es el chofer o el pasajero
                    if(tipUs.equals("chofer")){
                        mDatabase.child("bus/"+nombreTrans+"/autobus/"+numVehiculo+"/datos_gps_bus/"+fechaAc+ "/" + hora).push().setValue(datos);
                    }else if(tipUs.equals("pasajero")){
                        mDatabase.child("bus/"+nombreTrans+"/autobus/"+numVehiculo+"/pasajero/"+fechaAc+ "/" + hora+"/"+idUs).push().setValue(datos);
                    }
                    //volver nula la variable para que otra vez pueda entrar al ciclo
                    countDownTimerMovNormal=null;
                }
            }.start();
        }


    }
    public void contadorExcesoVelocidad(final String nombreTrans, final String numVehiculo, final Map<String, Object> datos, final String idUs, final String tipUs){
        //guarda la posicion actual del transporte en muchos campos cada 10 segundos
        if(countDownTimerMovExceso == null){
            //establecer el limite de tiempo
            countDownTimerMovExceso= new CountDownTimer(6000, 1000) {
                //interacciones en cada intervalo
                public void onTick(long millisUntilFinished) {
                    //Log.e("seconds remaining: " ,""+horaAc);
                }
                //finalizacion del contador
                public void onFinish() {
                    datos.put("contador",0);
                    //hacer la escritura del exceso de velocidad
                    mDatabase.child("bus/"+nombreTrans+"/autobus/"+numVehiculo+"/excesos/" +fechaAc+"/"+hora).push().setValue(datos);
                    //volver nula la variable para que otra vez pueda entrar al ciclo
                    countDownTimerMovExceso=null;
                }
            }.start();
        }
    }

    public void datosMovimiento(final GlobalClass gClass){
        String tipodeUsuario = gClass.getTipoUs();
        String tipUs = "none";
        final String idUs = MainActivity.bundle.getString("idUsuario",null);


        final Map<String,Object> datos = new HashMap<>();
        getDate();
        if(tipodeUsuario != null){
            tipUs=tipodeUsuario;

            datos.put("latitud",gClass.getLatitud());
            datos.put("longitud",gClass.getLongitud());
            datos.put("idusuario",gClass.getIdDispositivo());
            datos.put("numerove",gClass.getNumVehiculo());
            datos.put("hora",minuto);
            datos.put("fecha",fechaAc);
        }else {

            datos.put("latitud",gClass.getLatitud());
            datos.put("longitud",gClass.getLongitud());
            datos.put("velocidad",gClass.getVelocidad());
            datos.put("hora",horaAc);
            datos.put("fecha",fechaAc);
        }

        if(countDownTimerMov == null){
            final String finalTipUs = tipUs;
            countDownTimerMov= new CountDownTimer(6000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //Log.e("seconds remaining: " ,""+horaAc);
                }

                public void onFinish() {
                    if(finalTipUs.equals("none") ){
                        mDatabase.child("ayudanone/"+fechaAc+"/"+hora+"/"+gClass.getIdDispositivo()).push().setValue(datos);
                    }else{
                        mDatabase.child("ayuda/"+hora+"/"+gClass.getNombreTransporte()+"/"+gClass.getNumVehiculo()+"/"+gClass.getIdDispositivo()).setValue(datos);
                    }


                    countDownTimerMov=null;
                }
            }.start();
        }
    }

    private void getDate(){
        Calendar calendario = new GregorianCalendar();
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
