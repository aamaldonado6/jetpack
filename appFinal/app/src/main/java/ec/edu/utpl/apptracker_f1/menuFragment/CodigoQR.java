package ec.edu.utpl.apptracker_f1.menuFragment;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import ec.edu.utpl.apptracker_f1.MainActivity;
import ec.edu.utpl.apptracker_f1.R;
import ec.edu.utpl.apptracker_f1.interfaz.IcomunicacionMenu;
import ec.edu.utpl.apptracker_f1.manejadorBdd.GlobalClass;
import ec.edu.utpl.apptracker_f1.manejadorBdd.ManejadorBdd;

public class CodigoQR extends Fragment {
    GlobalClass globalClass;

    private OnFragmentInteractionListener mListener;
    public Button btnQR;
    TextView txtQr;
    Activity activity;
    IcomunicacionMenu icomunicacionMenu;
    View view;
    String datosBundle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            datosBundle =getArguments().getString("datosQr");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getActivity().getApplicationContext(), "Cancelado", Toast.LENGTH_LONG).show();
            } else {
                String resultadoQR = result.getContents();
                MainActivity.bundle.putString("datosQr",resultadoQR);
                guardarDatos(resultadoQR);
                Toast.makeText(getActivity().getApplicationContext(), "Datos Guardados", Toast.LENGTH_LONG).show();
                //Navigation.findNavController(view).navigate(R.id.inicioMenu);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }

    private void guardarDatos(String qr) {
        //ordenar los datos capturados por el codigo QR
        String[] parts = qr.split("=");
        System.out.println(parts[0]);
        if(parts[0].equals("apptrackerUTPL")){
            globalClass = ((GlobalClass) getActivity().getApplicationContext());
            globalClass.setNombreTransporte(parts[1]);
            globalClass.setNumVehiculo(parts[3]);
            globalClass.setTipoUs(parts[4]);
            globalClass.setRuta(parts[2]);
        txtQr.setText("Compañía:"+parts[1]+"\n\nRuta: "+parts[2]+"\n\nNúmero de vehículo: "+parts[3]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_codigo_qr, container, false);
        btnQR = view.findViewById(R.id.btn_qr);
        txtQr = view.findViewById(R.id.txt_qr);


        if(datosBundle != null){
            //ordenar los datos capturados por el codigo QR
            String[] parts = datosBundle.split("=");
            if(parts[0].equals("apptrackerUTPL")){
                txtQr.setText("Compañía:"+parts[1]+"\n\nRuta: "+parts[2]+"\n\nNúmero de vehículo: "+parts[3]);
            }
        }else {
            txtQr.setText("Por favor debe escanear un código QR para registrar los datos del vehículo");
        }

        //boton para escanear un codigo QR
        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanearQr();
                //Navigation.findNavController(view).navigate(R.id.inicioMenu);
            }

        });
        return view;
    }
    //configuracion del escaner QR
    public void escanearQr() {
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(CodigoQR.this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
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
