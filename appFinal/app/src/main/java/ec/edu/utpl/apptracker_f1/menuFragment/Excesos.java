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
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ec.edu.utpl.apptracker_f1.R;

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
    DatabaseReference ref;
    LinearLayout lContenedor;

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_excesos, container, false);
        ref = FirebaseDatabase.getInstance().getReference();
        lContenedor = view.findViewById(R.id.lcontenedor);
        listaFirebase(lContenedor);
        FirebaseApp.initializeApp(getContext());
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void listaFirebase(final LinearLayout contenedor) {

        String myIMEI = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        ref.child(myIMEI).child("excesoVelocidad").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int contador =0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    try {
                        Reporte rCoord = snapshot.getValue(Reporte.class);
                        String rVel = rCoord.getVelocidad();
                        int rEx = rCoord.getReportar();
                        contador = contador+1;
                        Switch switchb = new Switch(getContext());
                        //Personalizando botones
                        switchb.setText("Exceso de velocidad Nro: "+contador+"\n\nVelocidad: "+rVel+" Km/h");
                        switchb.setTextColor(Color.rgb(255, 255, 255));
                        switchb.setBackgroundColor(Color.rgb(64,89,120));
                        if (rEx==1 ){
                            switchb.setChecked(true);
                        }

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

                        contenedor.addView(switchb);

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
    private View.OnClickListener misEventosButton = new View.OnClickListener() {
        public void onClick(View v) {
            //aca castemos la variable v (View) para que este se convierta en un boton
            Button objBoton = (Button) v;
            if (objBoton.isEnabled()){

                Toast.makeText(getContext(),
                        "Se reportó el exceso de velocidad Nro "+objBoton.getText(),Toast.LENGTH_SHORT).show();

            }
        }
    };


    public static class Reporte {
        private int reportar;
        private String velocidad;
        public Reporte() {
            // ...
        }

        public String getVelocidad() {
            return velocidad;
        }

        public int getReportar() {
            return reportar;
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
