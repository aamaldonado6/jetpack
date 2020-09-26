package ec.edu.utpl.apptracker_f1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import ec.edu.utpl.apptracker_f1.actividades.DialogFragment;
import ec.edu.utpl.apptracker_f1.interfaz.IcomunicacionMenu;
import ec.edu.utpl.apptracker_f1.manejadorBdd.GlobalClass;
import ec.edu.utpl.apptracker_f1.manejadorBdd.ManejadorBdd;
import ec.edu.utpl.apptracker_f1.menuFragment.CodigoQR;
import ec.edu.utpl.apptracker_f1.menuFragment.Excesos;
import ec.edu.utpl.apptracker_f1.menuFragment.Info;
import ec.edu.utpl.apptracker_f1.menuFragment.InicioMenu;
import ec.edu.utpl.apptracker_f1.menuFragment.ReportarCond;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements IcomunicacionMenu, ReportarCond.OnFragmentInteractionListener, Excesos.OnFragmentInteractionListener, Info.OnFragmentInteractionListener, InicioMenu.OnFragmentInteractionListener, CodigoQR.OnFragmentInteractionListener {
    //variables para definir el tiempo en que cambia el location
    public static final int DEFAULT_UPDATE_INTERVAL = 1;
    public static final int FAST_UPDATE_INTERVAL = 1;
    public static final int PERMISSIONS_FINE_LOCATION = 99;

    //configuracion para el fusedlocation
    LocationRequest locationRequest;
    //configuracion para la respuesta si hay un cambio
    LocationCallback locationCallBack;
    //localizacion services
    private FusedLocationProviderClient fusedLocationClient;
    //
    //variable para saber si esta obteniendo la ubicacion o no
    boolean updateOn = false;
    //clase para enviar valores del location
    GlobalClass globalClass;

    public TextView txtVelocidad;
    public String idDispositivo;
    //id dispositivo
    String id = "";
    //bundle donde se van a guardar los datos del vehiculo y el id del dispositivo
    public static Bundle bundle = new Bundle();

    //sensor acelerometro
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    int movimiento=0;

    //base de datos
    ManejadorBdd mBdd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //guardar sharepreference
        guardarIdUs();
        //permisos de hubicacion
        permisosUbicacion();
        //clase global para manejar valores
        globalClass = ((GlobalClass) getApplicationContext());
        //clase manejador de la base de datos
        mBdd  = ManejadorBdd.getInstance();
        //parametros para el request
        createLocationRequest();

        //crear los TextView para la velocidad
        txtVelocidad = findViewById(R.id.velocidad_actual);
        //acceder a los sensores
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //escuchar el acelerometro
        sensorListerner();

        //empezar a actualziar la ubicacion
        startLocationUpdate();

    }

    private void sensorListerner() {


        sensorEventListener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //detectar cambios
                if(event.values[0]<-30 || event.values[1]<-30 || event.values[2]<-30 && movimiento==0 ){

                movimiento++;
                }else {
                    if(event.values[0]>30 || event.values[1]>30 || event.values[2]>30 || movimiento==1 ){

                        movimiento++;
                    }
                }
                if(movimiento==2){
                    System.out.println("CONGRASTTTT/////////////////*");
                    movimiento=0;
                }
                }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        //registrar el evento
        sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_GAME);
    }

    private void guardarIdUs() {
        SharedPreferences preferences = getSharedPreferences("idUsuarioApp", Context.MODE_PRIVATE);
        String user = preferences.getString("user",null);
        if (user == null){

            String usuario = Build.MODEL;
            usuario= usuario.replaceAll("\\s","");
            double random = Math.random()*(1-10)+10;
            usuario=usuario+random;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("user",usuario);
            editor.commit();

        }else {

        }

    }

    private void startLocationUpdate() {
        try {
            updateGps();
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallBack, null);

        } catch (Exception e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //actualiza la informacion si el caso lo amerita
                    updateGps();
                } else {
                    Toast.makeText(this, "Esta app necesita todos los permisos solicitados para poder funcionar (Localizacion, Cámara)", Toast.LENGTH_SHORT).show();
                }
        }
    }

    protected void createLocationRequest() {
        //definir las propiedades del locationRquest
        locationRequest = new LocationRequest();
        //locationRequest = LocationRequest.create();
        //con que exactitud se actualiza
        locationRequest.setInterval(0);
        //en que intervalo de tiempo se actualiza
        locationRequest.setFastestInterval(0);
        //que clase de metodo quiere si es por GPS o por wifi (PRIORITY_BALANCED_POWER_ACCURACY)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //funciona como un triggered cuando se actualiza
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //guardar la actualizacion
                updateUiValues(locationResult.getLastLocation());
            }
        };


    }

    public void openDialog(View v) {
        DialogFragment dialogFragment = new DialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "dialog alerta");
    }

    private void updateGps() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // dar permisos
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUiValues(location);
                }
            });

        } else {
            //si niega los permisis
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUiValues(Location location) {
        SharedPreferences preferences = getSharedPreferences("idUsuarioApp", Context.MODE_PRIVATE);
        String user = preferences.getString("user",null);


        try {
            if (location.hasSpeed()) {
                //obetener la velocidad en k/h
                double sp = ((location.getSpeed() * 3600) / 1000);
                String speed = (String.format("%.2f", sp));
                txtVelocidad.setText(speed);
                bundle.putString("datoSpeed", speed);
                bundle.putDouble("datoLat", location.getLatitude());
                bundle.putDouble("datoLong", location.getLongitude());
                //if (bundle.getString("datoNumero", null) != null) {

                    //enviar los datos para manejarlos en el global

                    globalClass.setLatitud(location.getLatitude());
                    globalClass.setLongitud(location.getLongitude());
                    globalClass.setNumVehiculo(bundle.getString("datoNumero", null));
                    globalClass.setNombreTransporte(bundle.getString("datoNombre", null));
                    globalClass.setIdDispositivo(user);
                    globalClass.setTipoUs(bundle.getString(" tipoUsuario", null));
                    globalClass.setVelocidad(sp);
                    //globalClass.enviarDatos();


                     mBdd.prueba(globalClass);
                }


            Geocoder geocoder = new Geocoder(MainActivity.this);
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                bundle.putString("datoDir", addresses.get(0).getAddressLine(0));
            } catch (Exception e) {
                Toast.makeText(this, "no existe el adress", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

        }


    }

    //actividad para escanear el codigo QR
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
            } else {
                bundle.putString("datosQr", result.getContents());
                Toast.makeText(this, "Datos Guardados", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void permisosUbicacion() {
        //solicitar el permiso
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
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
        Navigation.findNavController(v).navigate(R.id.action_inicioMenu_to_codigoQR2, bundle);
    }

    @Override
    public void iniExcesos(View v) {
        Navigation.findNavController(v).navigate(R.id.action_inicioMenu_to_excesos, bundle);
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

    @Override
    protected void onStop() {
        startLocationUpdate();
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();

    }

}