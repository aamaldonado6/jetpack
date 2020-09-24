package ec.edu.utpl.apptracker_f1.actividades;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import ec.edu.utpl.apptracker_f1.manejadorBdd.GlobalClass;
import ec.edu.utpl.apptracker_f1.manejadorBdd.ManejadorBdd;

public class DialogFragment extends AppCompatDialogFragment {
    GlobalClass  globalClass;
    ManejadorBdd manejadorBdd;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        manejadorBdd  = ManejadorBdd.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enviar ALERTA").setMessage("Está seguro que desea enviar una alerta?..").setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                globalClass = ((GlobalClass) getActivity().getApplicationContext());
                try {
                    manejadorBdd.datosMovimiento(globalClass);
                }catch (Exception e){
                    System.out.println("errr:  "+e);
                }
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();

    }

    private void enviardatos() {

    }


}
