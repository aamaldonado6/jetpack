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
    CountDownTimer countDownTimerMovExceso;
    CountDownTimer countDownTimerMovNormal;


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
        final double numberVeloc = gClass.getVelocidad();
        //codigo para saber que usuario esta ocupando la app
        String codUs = MainActivity.bundle.getString("codUsuario","nonUs");

        //crear valores para guardar en la bdd
        //obtener el id del usuario
        final String idUs = MainActivity.bundle.getString("idUsuario",null);
        final Map<String,Object> datos = new HashMap<>();
        datos.put("latitud",gClass.getLatitud());
        datos.put("longitud",gClass.getLongitud());
        datos.put("idusuario",idUs);
        datos.put("velocidad",numberVeloc);
        datos.put("hora",horaAc);
        datos.put("fecha",fechaAc);

        final String nombreTrans =gClass.getNombreTransporte();
        final String numVehiculo =gClass.getNumVehiculo();
        String tipodeUsuario=gClass.getTipoUs();
        String ruta =gClass.getRuta();

        String tipUs = "none";
        if(tipodeUsuario != null){
            tipUs=tipodeUsuario;
        }
        //////////

        if(numberVeloc<5){
            //if(20<5){
            //envia la velocidad a la pantalla del usuario
            mainActivity.txtVelocidad.setText("....");
        }else{
            mainActivity.txtVelocidad.setText(velocidad);
            //countDown

            if( numberVeloc < 90 ){
                //count down
                if(countDownTimerMovNormal == null){
                    Toast.makeText(null,"Entro a normal",Toast.LENGTH_SHORT).show();
                    final String finalTipUs = tipUs;
                    countDownTimerMovNormal= new CountDownTimer(10000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            //Log.e("seconds remaining: " ,""+horaAc);
                        }

                        public void onFinish() {
                            if(finalTipUs.equals("chofer") ){

                                mDatabase.child("bus/"+nombreTrans+"/autobus/"+numVehiculo+"/datos_gps_actual").setValue(datos);
                                mDatabase.child("bus/"+nombreTrans+"/autobus/"+numVehiculo+"/datos_gps_bus/"+fechaAc+ "/" + hora).push().setValue(datos);
                            }else{
                                if(finalTipUs.equals("pasajero")){
                                    mDatabase.child("bus/"+nombreTrans+"/autobus/"+numVehiculo+"/pasajero/"+fechaAc+ "/" + hora+"/"+idUs).push().setValue(datos);
                                }else {
                                    mDatabase.child("nonus/usuario/"+fechaAc+"/"+hora+"/"+idUs).setValue(datos);
                                }
                            }


                            countDownTimerMovNormal=null;
                        }
                    }.start();
                }

            }else{
                if(countDownTimerMovExceso == null){
                    Toast.makeText(null,"Entro a exceso",Toast.LENGTH_SHORT).show();
                    final String finalTipUs = tipUs;
                    final String finalTipUs1 = tipUs;
                    countDownTimerMovExceso= new CountDownTimer(10000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            //Log.e("seconds remaining: " ,""+horaAc);
                        }

                        public void onFinish() {
                            if (numberVeloc >= 90){
                                if(finalTipUs1.equals("chofer")){
                                    datos.put("reportar",false);
                                    countDownTimer(idUs,datos,nombreTrans,numVehiculo);

                                }else{
                                    if(finalTipUs1.equals("pasajero")){
                                        //mDatabase.child("bus/"+nombreTrans+"/autobus/"+numVehiculo+"/"+idUs+"/" +fechaAc+ "/" + hora).setValue(datos);
                                    }else {
                                        // mDatabase.child("nonus/"+fechaAc+"/"+hora+"/"+idUs).push().setValue(datos);
                                    }
                                }
                            }
                            countDownTimerMovExceso=null;
                        }
                    }.start();
                }

            }
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

    private void countDownTimer(final String idUs, final Map<String, Object> datos, final String nombreTrans, final String numVehiculo){
        if(countDownTimer == null){
            countDownTimer= new CountDownTimer(15000, 1000) {

                public void onTick(long millisUntilFinished) {
                    // Log.e("seconds remaining: " ,""+millisUntilFinished / 1000);
                }

                public void onFinish() {
                    mDatabase.child("bus/"+nombreTrans+"/autobus/"+numVehiculo+"/excesos/" +fechaAc+"/"+hora).push().setValue(datos);

                    countDownTimer=null;
                }
            }.start();
        }

    }


}
