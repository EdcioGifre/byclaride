package com.example.byclarider.presentador;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class PatallaInicioPre {
    //Metodo para mostrar un mensaje de bienvenida a travez de un toast tras un contador de 2.5 segundos
    public void contadorPantallaInicio(Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "Bienvenidooo!!!", Toast.LENGTH_SHORT).show();
            }
        }, 2500);
    }
}