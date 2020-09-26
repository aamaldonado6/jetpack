package ec.edu.utpl.apptracker_f1.manejadorBdd;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import ec.edu.utpl.apptracker_f1.MainActivity;

public class ManejadorBdd {
    //declaracion
    private static ManejadorBdd instancia;

    private DatabaseReference mDatabase;

    Calendar calendario = new GregorianCalendar();
    int hora,minuto,segundo,mSegundo,mes,dia,anio;
    String horaAc="",fechaAc="";

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

    /*public void guardarDatosExceso(String sp,double la,double lo,String hora,String fecha) {

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


    }*/
    public void prueba(GlobalClass gClass){

        Map<String,Object> datos = new HashMap<>();
        datos.put("latitud",gClass.getLongitud());
        datos.put("longitud",gClass.getLongitud());
        datos.put("velocidad",gClass.getVelocidad());

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("crowdGTR").push().setValue(datos);

        }catch (Exception e){
            System.out.println("holaaaaaaaaaaaaaaaaaaaaaaa");
            System.out.println(e);
        }




    }

    public void guardarDatos(String sp,double la,double lo,String hora,String fecha,String codigo) {
//obtener la fecha
        getDate();


        Map<String,Object> datos = new HashMap<>();
        datos.put("latitud",la);
        datos.put("longitud",lo);
        datos.put("velocidad",sp);
        datos.put("hora",horaAc);
        datos.put("fecha",fechaAc);
        //datos.put("idDisposi",myIMEI);
        //mDatabase.child(myIMEI).child(codigo).push().setValue(datos);

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
