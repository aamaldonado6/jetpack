package ec.edu.utpl.apptracker_f1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import ec.edu.utpl.apptracker_f1.actividades.Constans;
import ec.edu.utpl.apptracker_f1.actividades.DialogFragment;
import ec.edu.utpl.apptracker_f1.interfaz.IcomunicacionMenu;
import ec.edu.utpl.apptracker_f1.manejadorBdd.GlobalClass;
import ec.edu.utpl.apptracker_f1.manejadorBdd.ManejadorBdd;
import ec.edu.utpl.apptracker_f1.manejadorUbicacion.Common;
import ec.edu.utpl.apptracker_f1.manejadorUbicacion.MyBackgroundService;
import ec.edu.utpl.apptracker_f1.manejadorUbicacion.SendLocationToActivity;
import ec.edu.utpl.apptracker_f1.menuFragment.CodigoQR;
import ec.edu.utpl.apptracker_f1.menuFragment.Excesos;
import ec.edu.utpl.apptracker_f1.menuFragment.Info;
import ec.edu.utpl.apptracker_f1.menuFragment.InicioMenu;
import ec.edu.utpl.apptracker_f1.menuFragment.ReportarCond;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IcomunicacionMenu, ReportarCond.OnFragmentInteractionListener, Excesos.OnFragmentInteractionListener, Info.OnFragmentInteractionListener, InicioMenu.OnFragmentInteractionListener, CodigoQR.OnFragmentInteractionListener {
    //variables para definir los permisos

    private static final  int REQUEST_CODA_LOCATION_PERMISSION = 1;


    //configuracion para el fusedlocation
    LocationRequest locationRequest;
    //configuracion para la respuesta si hay un cambio
    LocationCallback locationCallBack;
    //localizacion services
    private FusedLocationProviderClient fusedLocationClient;

    public TextView txtVelocidad;
    public GlobalClass globalClass;

    //bundle donde se van a guardar los datos del vehiculo y el id del dispositivo
    public static Bundle bundle = new Bundle();

    //manejador de los datos
    ManejadorBdd mBdd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //global class
        globalClass = ((GlobalClass) getApplicationContext());
        //orientacion de la app
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //guardar sharepreference
        guardarIdUs();
        //clase manejador de la base de datos
        mBdd  = ManejadorBdd.getInstance();
        //instanciar la clase para poder interactuar
        mBdd.setMainActivity(this);
        //crear los TextView para la velocidad
        txtVelocidad = findViewById(R.id.velocidad_actual);
        //empezar a crear las actualizaciones
        iniciarLocation();
        /*try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        }catch (Exception e){
            System.out.println("ERROR:"+e);
        }*/

    }

    private void iniciarLocation() {
        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODA_LOCATION_PERMISSION
            );
        }else {
            //iniciar el servicio
            startLocationService();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODA_LOCATION_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationService();
            }else {
                Toast.makeText(this,"Permisos denegados",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(MyBackgroundService.class.getName().equals(service.service.getClassName())){
                    if(service.foreground){
                        return  true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), MyBackgroundService.class);
            intent.setAction(Constans.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,"Localización activada",Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), MyBackgroundService.class);
            intent.setAction(Constans.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            //Toast.makeText(this,"Localización detenida",Toast.LENGTH_SHORT).show();
        }
    }



    private void guardarIdUs() {
        SharedPreferences preferences = getSharedPreferences("idUsuarioApp", Context.MODE_PRIVATE);
        String user = preferences.getString("user",null);
        if (user == null){

            String usuario = Build.MODEL;
            usuario= usuario.replaceAll("\\s","");
            int random = (int) Math.floor(Math.random()*(1-70+1)+70);
            System.out.println(random);
            usuario=usuario+random;
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("user",usuario);
            editor.commit();

        }
        globalClass.setIdDispositivo(user);
        bundle.putString("idUsuario", user);

    }


    public void openDialog(View v) {
        DialogFragment dialogFragment = new DialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "dialog alerta");
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
    protected void onDestroy() {
        super.onDestroy();
        stopLocationService();

    }
}