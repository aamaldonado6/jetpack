package ec.edu.utpl.apptracker_f1.manejadorUbicacion;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class ServiceLocation extends Service {

    @Override
    public void onCreate(){

    }

    @Override
    public int onStartCommand(Intent intent,int flag, int idProcess){
        return START_STICKY;
    }

    @Override
    public void onDestroy(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
