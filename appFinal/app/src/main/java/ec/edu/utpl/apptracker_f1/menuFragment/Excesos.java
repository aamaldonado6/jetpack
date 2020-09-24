package ec.edu.utpl.apptracker_f1.menuFragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import ec.edu.utpl.apptracker_f1.R;
import ec.edu.utpl.apptracker_f1.manejadorBdd.GlobalClass;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Excesos.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Excesos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Excesos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    View view;
    LinearLayout iContenedor;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    String datosBundle;
    GlobalClass globalClass;
    int hora,minuto,segundo,mSegundo,mes,dia,anio;
    String horaAc="",fechaAc="";
    ArrayList<String> nombreArrayList;

    public Excesos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Excesos.
     */
    // TODO: Rename and change types and number of parameters
    public static Excesos newInstance(String param1, String param2) {
        Excesos fragment = new Excesos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            datosBundle =getArguments().getString("idDisposi");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_excesos, container, false);
        iContenedor = view.findViewById(R.id.lcontenedor);

        nombreArrayList = new ArrayList<String>();
        listaExcesos();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void listaExcesos() {
        globalClass = ((GlobalClass) getActivity().getApplicationContext());
        if(globalClass.getNombreTransporte() != null){
            getDate();
            //String myIMEI = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            //"bus/"+globalClass.getNombreTransporte()+"/autobus/"+globalClass.getNumVehiculo()+"/excesos/" +fechaAc+"/"+hora
            ref.child("bus/"+globalClass.getNombreTransporte()+"/autobus/"+globalClass.getNumVehiculo()+"/excesos/" +fechaAc+"/"+hora).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   int contador =0;
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        try {
                            String idR =snapshot.getKey();

                            Reporte rCoord = snapshot.getValue(Reporte.class);

                            double rVel = rCoord.getVelocidad();
                            boolean rReport = rCoord.isReportar();

                            if(rReport == false){

                            nombreArrayList.add(contador, idR);

                            Switch switchb = new Switch(getContext());



                            //Personalizando botones
                            switchb.setText("\nExceso de velocidad Nro: "+(contador+1)+"\nVelocidad: "+rVel+" Km/h\n");
                            switchb.setTextColor(Color.rgb(255, 255, 255));
                            switchb.setBackgroundColor(Color.rgb(64,89,120));
                            switchb.setId(contador);

                            switchb.setChecked(rReport);

                            contador = contador+1;
                            //Enviar un margin a los botones
                            LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                                    /*width*/ ViewGroup.LayoutParams.MATCH_PARENT,
                                    /*height*/ ViewGroup.LayoutParams.WRAP_CONTENT

                            );
                            parametros.setMargins(5, 15, 5, 5);
                            //boton.setLayoutParams(parametros);
                            switchb.setLayoutParams(parametros);
                            switchb.setGravity(1);

                            //implementar el evento
                            switchb.setOnClickListener(misEventosButton);

                            iContenedor.addView(switchb);}

                        }catch(DatabaseException e){
                            Log.e("error!!",""+dataSnapshot.getKey());
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

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
    private View.OnClickListener misEventosButton = new View.OnClickListener() {
        public void onClick(View v) {
            globalClass = ((GlobalClass) getActivity().getApplicationContext());
            //aca castemos la variable v (View) para que este se convierta en un boton
            Button objBoton = (Button) v;
            //System.out.println(nombreArrayList.get(objBoton.getId()));
                Toast.makeText(getContext(),
                        "Se reportó el exceso de velocidad Nro "+objBoton.getText(),Toast.LENGTH_SHORT).show();

            Map<String,Object> datos = new HashMap<>();
            datos.put("reportar",true);
            try {
                ref.child("bus/"+globalClass.getNombreTransporte()+"/autobus/"+globalClass.getNumVehiculo()+"/excesos/" +fechaAc+"/"+hora+"/"+nombreArrayList.get(objBoton.getId())).updateChildren(datos);
            }catch (Exception e){
                System.out.println("erre");
            }

        }
    };


    public static class Reporte {
        private double latitud;
        private double longitud;
        private double velocidad;
        private String fecha;
        private String hora;
        private String idusuario;
        private boolean reportar;

        public Reporte() {
            // ...
        }

        public boolean isReportar() {
            return reportar;
        }

        public void setReportar(boolean reportar) {
            this.reportar = reportar;
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

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getHora() {
            return hora;
        }

        public void setHora(String hora) {
            this.hora = hora;
        }

        public String getIdusuario() {
            return idusuario;
        }

        public void setIdusuario(String idusuario) {
            this.idusuario = idusuario;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
