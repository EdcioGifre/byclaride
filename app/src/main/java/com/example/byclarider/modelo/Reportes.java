package com.example.byclarider.modelo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


//import com.example.byclarider.Manifest;
import com.example.byclarider.R;
import com.example.byclarider.presentador.DetailRequestActivity;
import com.example.byclarider.presentador.MapaHome;

import java.io.File;
import java.io.IOException;

public class Reportes extends AppCompatActivity {

    ImageButton btnCamara;
    Button btnReporte;
    EditText editTextComentario;

    //ImageView visor;

    String ruta;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        btnCamara = findViewById(R.id.btnCamara);
        btnReporte = findViewById(R.id.button);
        editTextComentario = findViewById(R.id.editTextComentario);
        //visor = findViewById(R.id.iv_visor);
/*
        if (ContextCompat.checkSelfPermission(Reportes.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(Reportes.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Reportes.
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, 1000);
        }

 */
        btnReporte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Obtener el texto del campo de comentario
                String comentario = editTextComentario.getText().toString();

                // Verificar si el campo está vacío
                if (comentario.isEmpty()) {
                    // Mostrar un mensaje de error si el campo está vacío
                    Toast.makeText(Reportes.this, "El campo de comentario no puede estar vacío", Toast.LENGTH_SHORT).show();
                } else {
                    // Mostrar mensaje de reporte exitoso
                    Toast.makeText(Reportes.this, "El reporte se ha realizado correctamente", Toast.LENGTH_SHORT).show();
                    // Cerrar esta actividad y volver a la actividad anterior
                    onBackPressed();
                }
            }
        });

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camara();
            }
        });
    }
    //Abre o activa la camara
    private void camara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            File fotoArchivo = null;
            try{
                fotoArchivo = guardarImagen();
            }catch (IOException ioException){
                Log.e("error",ioException.toString());
            }
            if(fotoArchivo != null){
                Uri uri = FileProvider.getUriForFile(this,"com.example.byclarider.fileprovider",fotoArchivo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1  && requestCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            //Bitmap imgBitmap = BitmapFactory.decodeFile(ruta);
            //visor.setImageBitmap(imgBitmap);
        }
    }

    private File guardarImagen() throws IOException{
        String nombreFoto = "foto";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File foto = File.createTempFile(nombreFoto,".jpg",directorio);
        ruta = foto.getAbsolutePath();
        return foto;
    }
}