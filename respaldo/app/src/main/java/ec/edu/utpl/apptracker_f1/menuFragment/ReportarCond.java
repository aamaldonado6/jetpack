package ec.edu.utpl.apptracker_f1.menuFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.navigation.Navigation;
import ec.edu.utpl.apptracker_f1.MainActivity;
import ec.edu.utpl.apptracker_f1.R;
import ec.edu.utpl.apptracker_f1.manejadorBdd.GlobalClass;
import ec.edu.utpl.apptracker_f1.manejadorBdd.ManejadorBdd;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportarCond.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportarCond#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportarCond extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public RadioButton radio11,radio12,radio13,radio14;
    public Button button;
    public EditText textView;
    View view;

    private DatabaseReference mDatabase;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ReportarCond() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportarCond.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportarCond newInstance(String param1, String param2) {
        ReportarCond fragment = new ReportarCond();
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
        view=inflater.inflate(R.layout.fragment_reportar_cond, container, false);
        // Inflate the layout for this fragment
        //crear los componente
        radio11 =view.findViewById(R.id.radio1);
        radio12 =view.findViewById(R.id.radio2);
        radio13 =view.findViewById(R.id.radio3);
        radio14 =view.findViewById(R.id.radio4);
        textView =view.findViewById(R.id.txt_tv);
        button=view.findViewById(R.id.btn_rep);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarD();
            }
        });

        return view;
    }

    public void guardarD(){
        String coment = String.valueOf(textView.getText());
        String r1 = String.valueOf(radio11.isChecked());
        String r2 = String.valueOf(radio12.isChecked());
        String r3 = String.valueOf(radio13.isChecked());
        String r4 = String.valueOf(radio14.isChecked());
        Toast.makeText(getActivity().getApplicationContext(),"REPORTE GUARDADO", Toast.LENGTH_SHORT).show();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Map<String,Object> datos = new HashMap<>();
        datos.put("comentario",coment);
        datos.put("rebasa",r1);
        datos.put("recoge",r2);
        datos.put("estacionado",r3);
        datos.put("gasolina",r4);
        String nomBus =MainActivity.bundle.getString("datoNombre",null);
        String numBus = MainActivity.bundle.getString("datoNumero",null);
        String idBus = MainActivity.bundle.getString("datoId",null);
        try {
            mDatabase.child("crowd/cooloja/"+numBus+"/reporte_transporte/"+2+"/"+idBus).push().setValue(datos);

        }catch (Exception e){
        }
        Navigation.findNavController(view).navigate(R.id.inicioMenu);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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







