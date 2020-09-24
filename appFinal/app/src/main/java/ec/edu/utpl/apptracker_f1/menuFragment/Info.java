package ec.edu.utpl.apptracker_f1.menuFragment;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;
import ec.edu.utpl.apptracker_f1.MainActivity;
import ec.edu.utpl.apptracker_f1.R;
import ec.edu.utpl.apptracker_f1.interfaz.IcomunicacionMenu;
import ec.edu.utpl.apptracker_f1.manejadorBdd.GlobalClass;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Info.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Info#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Info extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Button btnShare;
    TextView txtLatitud;
    TextView txtLongitud;
    TextView txtVelocidad;
    TextView txtUbicacion;

    Activity activity;
    IcomunicacionMenu icomunicacionMenu;
    View view;
    String datosBundle;
    GlobalClass globalClass;

    private OnFragmentInteractionListener mListener;

    public Info() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Info.
     */
    // TODO: Rename and change types and number of parameters
    public static Info newInstance(String param1, String param2) {
        Info fragment = new Info();
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
            datosBundle =getArguments().getString("datos");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_info, container, false);
        //referencia a la global class
        globalClass = ((GlobalClass) getActivity().getApplicationContext());
        //reacr componentes
        txtLatitud= view.findViewById(R.id.share_latitud);
        txtLongitud=view.findViewById(R.id.share_longitud);
        txtVelocidad=view.findViewById(R.id.share_velocidad);
        txtUbicacion=view.findViewById(R.id.share_ubicacion);
        btnShare=view.findViewById(R.id.btn_share);



        txtLongitud.setText(String.valueOf(globalClass.getLongitud()));
        txtLatitud.setText(String.valueOf(globalClass.getLatitud()));
        txtUbicacion.setText(MainActivity.bundle.getString("datoDir","...."));
        txtVelocidad.setText(String.valueOf(globalClass.getVelocidad()));


        //boton para compartir los datos
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartirDatos();
                //Navigation.findNavController(view).navigate(R.id.inicioMenu);
            }

        });
        return view;
    }

    private void compartirDatos() {
        String uri = "http://maps.google.com/maps?saddr=" + MainActivity.bundle.getDouble("datoLat",0.0) + "," + MainActivity.bundle.getDouble("datoLong",0.0);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, uri );
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        Intent shareIntent = Intent.createChooser(sendIntent,null);
        startActivity(shareIntent);
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
        if (context instanceof InicioMenu.OnFragmentInteractionListener) {
            this.activity=(Activity) context;
            icomunicacionMenu=(IcomunicacionMenu) this.activity;
        }
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
