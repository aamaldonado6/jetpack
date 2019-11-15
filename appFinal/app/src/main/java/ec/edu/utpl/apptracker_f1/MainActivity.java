package ec.edu.utpl.apptracker_f1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import ec.edu.utpl.apptracker_f1.interfaz.IcomunicacionMenu;
import ec.edu.utpl.apptracker_f1.manejadorUbicacion.GpsUbicacion;
import ec.edu.utpl.apptracker_f1.menuFragment.CodigoQR;
import ec.edu.utpl.apptracker_f1.menuFragment.Excesos;
import ec.edu.utpl.apptracker_f1.menuFragment.Info;
import ec.edu.utpl.apptracker_f1.menuFragment.InicioMenu;
import ec.edu.utpl.apptracker_f1.menuFragment.ReportarCond;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements IcomunicacionMenu, ReportarCond.OnFragmentInteractionListener, Excesos.OnFragmentInteractionListener, Info.OnFragmentInteractionListener, InicioMenu.OnFragmentInteractionListener, CodigoQR.OnFragmentInteractionListener {

    public TextView txtVelocidad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new IntentIntegrator(this).initiateScan();



        //crear los TextView para la velocidad
        txtVelocidad=findViewById(R.id.velocidad_actual);
        Bundle datosAEnviar = new Bundle();
        datosAEnviar.putInt("edad", 21);

        //pedirPermisos
        permisosUbicacion();
        //solicitar lat,long
        gpsUb();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelledasdasd", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scaasdasdndned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void iniCodigoQr(View v) {
        Navigation.findNavController(v).navigate(R.id.action_inicioMenu_to_codigoQR2);
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
