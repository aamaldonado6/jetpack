package ec.edu.utpl.apptracker_f1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import ec.edu.utpl.apptracker_f1.interfaz.IcomunicacionMenu;
import ec.edu.utpl.apptracker_f1.manejador.GpsUbicacion;
import ec.edu.utpl.apptracker_f1.menuFragment.CodigoQR;
import ec.edu.utpl.apptracker_f1.menuFragment.Excesos;
import ec.edu.utpl.apptracker_f1.menuFragment.Info;
import ec.edu.utpl.apptracker_f1.menuFragment.InicioMenu;
import ec.edu.utpl.apptracker_f1.menuFragment.ReportarCond;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements IcomunicacionMenu, ReportarCond.OnFragmentInteractionListener, Excesos.OnFragmentInteractionListener, Info.OnFragmentInteractionListener, InicioMenu.OnFragmentInteractionListener, CodigoQR.OnFragmentInteractionListener {

    private DatabaseReference mDatabase;
    public double latitudM,longitudM,velocidadM;
    //definir los key para las variables a mostrar
    public static final String key_lati="lati",key_long="long",key_veloc="velocidad";

View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = new Bundle();
        bundle.putString("amount", "Sssss");

        Navigation.findNavController(view).navigate(R.id.info2, bundle);



        //txtLati.setText("as");
        //pedirPermisos
        permisosUbicacion();

        //inicializar Firebase
        try {
            FirebaseApp.initializeApp(this);
            mDatabase = FirebaseDatabase.getInstance().getReference();

        }
        catch (Exception e) {
        }
        gpsUb();

    }
    public void gpsLoc(Location location) {
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0){
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                if (!list.isEmpty()){
                    Address address = list.get(0);
                    //txtVelocidad.setText(address.getAddressLine(0));
                    System.out.println("yyyyyyyyyy"+address.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void gpsUb() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        GpsUbicacion gpsu = new GpsUbicacion();
        gpsu.setMainActivity(this);
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,(GpsUbicacion) gpsu);
    }

    public void permisosUbicacion() {

        //solicitar el permiso
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private String gg = "";

    Calendar calendario = new GregorianCalendar();
    int hora,minuto,segundo,mes,dia,anio;


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void iniCodigoQr(View v) {
        Navigation.findNavController(v).navigate(R.id.action_inicioMenu_to_codigoQR);
    }

    @Override
    public void iniExcesos(View v) {
        Navigation.findNavController(v).navigate(R.id.action_inicioMenu_to_excesos);
    }

    @Override
    public void iniMap(View v) {
        Navigation.findNavController(v).navigate(R.id.action_inicioMenu_to_mapsActivity01);
    }

    @Override
    public void iniReportar(View v) {
        Navigation.findNavController(v).navigate(R.id.action_inicioMenu_to_reportarCond);
    }

    @Override
    public void iniInfo(View v) {
        Navigation.findNavController(v).navigate(R.id.action_inicioMenu_to_info2);
    }
}
