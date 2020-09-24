package ec.edu.utpl.apptracker_f1.actividades;

import androidx.appcompat.app.AppCompatActivity;
import ec.edu.utpl.apptracker_f1.MainActivity;
import ec.edu.utpl.apptracker_f1.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class FondoLogin extends AppCompatActivity {
    //velocidad que se muestra en el menu inicial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fondo_login);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(FondoLogin.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },1000);
    }
}
