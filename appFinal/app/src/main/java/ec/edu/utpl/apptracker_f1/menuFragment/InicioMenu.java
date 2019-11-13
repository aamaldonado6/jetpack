package ec.edu.utpl.apptracker_f1.menuFragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.net.sip.SipSession;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;
import ec.edu.utpl.apptracker_f1.R;
import ec.edu.utpl.apptracker_f1.interfaz.IcomunicacionMenu;
import ec.edu.utpl.apptracker_f1.manejador.GpsUbicacion;

import static ec.edu.utpl.apptracker_f1.MainActivity.key_lati;
import static ec.edu.utpl.apptracker_f1.MainActivity.key_long;
import static ec.edu.utpl.apptracker_f1.MainActivity.key_veloc;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InicioMenu.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InicioMenu} factory method to
 * create an instance of this fragment.
 */
public class InicioMenu extends Fragment {

    private OnFragmentInteractionListener mListener;

    View view;
    Activity activity;
    IcomunicacionMenu icomunicacionMenu;
    CardView cardQR,cardReportar,cardExcesos,cardInfo,cardMapa;
    public double velocidad,longitud,latitud;
    public TextView txtVelocidad,txtlongi,txtLati;


    public InicioMenu() {
        // Required empty public constructor
    }


    public double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            velocidad= getArguments().getDouble("s",213.2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_inicio_menu, container, false);
        cardQR=view.findViewById(R.id.card_qr);
        cardMapa=view.findViewById(R.id.card_mapa);
        cardReportar=view.findViewById(R.id.card_reporte);
        cardInfo=view.findViewById(R.id.card_info);
        cardExcesos=view.findViewById(R.id.card_excesos);
        txtVelocidad=view.findViewById(R.id.velocidad_actual);
        txtlongi= view.findViewById(R.id.txt_longitud);
        txtLati= view.findViewById(R.id.txt_latitud);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("xxxxxxxxxx"+velocidad);
        //eventos interface
        //textView.setText("asdasd");
        txtLati.setText("s");
        txtlongi.setText("s");
        txtVelocidad.setText("ss"+getVelocidad());
        eventosMenu();
        GpsUbicacion s = new GpsUbicacion();
    }


    private void eventosMenu(){

        cardQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icomunicacionMenu.iniCodigoQr(v);
            }
        });
        cardMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icomunicacionMenu.iniMap(v);
            }
        });
        cardInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icomunicacionMenu.iniInfo(v);
            }
        });
        cardReportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { icomunicacionMenu.iniReportar(v);}
        });
        cardExcesos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icomunicacionMenu.iniExcesos(v);
            }
        });
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
