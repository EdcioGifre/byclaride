package com.example.byclarider.vista;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.example.byclarider.R;
import com.example.byclarider.presentador.PatallaInicioPre;

public class PantallaInicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicio);

        //Se llama al metodo contadorPantallaInicio() a travez del objeto patallaInicioPre
        PatallaInicioPre patallaInicioPre = new PatallaInicioPre();
        patallaInicioPre.contadorPantallaInicio(this);

        //tras el contador se llama a la activity main
        Intent intent = new Intent(PantallaInicio.this , MainActivity.class);
        startActivity(intent);
    }
}