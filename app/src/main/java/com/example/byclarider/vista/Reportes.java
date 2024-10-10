package com.example.byclarider.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


//import com.example.byclarider.Manifest;
import com.example.byclarider.R;
import com.example.byclarider.modelo.ReportesModelo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Reportes extends AppCompatActivity {

    ImageButton btnCamara;
    private EditText editTextComentario;
    private CheckBox radioBtnBache, radioBtnLuz, radioBtnRobo;
    String ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        btnCamara = findViewById(R.id.btnCamara);
        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camara();
            }
        });

        //buscar todos los archivos TXT existentes
        String Archivos [] = fileList();
        editTextComentario = findViewById(R.id.editTextComentario);
        //Vamos a utilizar un condicional para verificar el nombre del archivo
        //Y lo haremos utilizando un metodo booleano
        if(ArchivoExistente(Archivos, "reportes.txt")){
            //Abrir el archivo en modo lectura
            try {
                //Abrir el archivo en formato lectura y almacenar la info en el buffer cargado en la variable archivo
                InputStreamReader archivo = new InputStreamReader(openFileInput("reportes.txt"));
                //Leemos el contenido del archivo que se a cargado en un buffer
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine(); /// variable para guardar 1 linea
                String tareas = ""; ///variable para guardar todas las lineas
                while (linea != null){
                    tareas = tareas + linea + "\n";
                    linea = br.readLine();
                }
                //Cerra el buffer
                br.close();
                //Cerrar el archivo
                archivo.close();
                //Muestre el contenido del archivo en la pantalla (EditText Multilinea)
                //editTextComentario.setText(tareas);
            }catch (IOException e){
                Toast.makeText(this, "Error!!! el archivo no se pudo abrir" + e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
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

    //Metodo busca archivo requerido
    private boolean ArchivoExistente(String Archivos [], String nombreArchivo){
        for(int i = 0; i<Archivos.length; i++)
            if (nombreArchivo.equals(Archivos[i]))
                return true;
        return false;
    }

    public void reporte(View v){
        // Obtener el texto del campo de comentario
        String comentario = editTextComentario.getText().toString();

        // Verificar si el campo está vacío
        if (comentario.isEmpty() && (radioBtnBache.isChecked() || radioBtnLuz.isChecked() || radioBtnRobo.isChecked())){
            // Mostrar un mensaje de error si el campo está vacío
            Toast.makeText(Reportes.this, "El campo de comentario no puede estar vacío", Toast.LENGTH_SHORT).show();
        } else {
            try {
                //Escribiremos en el archivo
                OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput("reportes.txt", Activity.MODE_APPEND));
                //debemos capturar lo que hay en el EditText Multilinea
                archivo.write(editTextComentario.getText().toString());
                //Limpiar el buffer
                archivo.flush();
                //cerramos el archivo
                archivo.close();
            }catch (IOException e){
                Toast.makeText(Reportes.this, "Error!!! no se pudo guardar el archivo" + e.getMessage(),Toast.LENGTH_LONG).show();
            }
            // Mostrar mensaje de reporte exitoso
            Toast.makeText(Reportes.this, "El reporte se ha realizado correctamente", Toast.LENGTH_SHORT).show();

            //Limpia el campo de comentario
            editTextComentario.setText(""); // <-- Esta línea limpia el campo de comentario

            //cerrar el activity
            finish();

            // Cerrar esta actividad y volver a la actividad anterior
            onBackPressed();
        }
    }

//borrar view para cambiar el onclic
    public void writeFireBase(View view){
//        String comentario = editTextComentario.getText().toString();
//        String categoria = obtenerCategoriaSeleccionada();
//        String id = "";
//        ReportesModelo reportesModelo;
//
//        //creamos un objeto de firebase
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//        //obtenemos el usuario actual
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        id = currentUser.getUid();
//
//        //Mandamos las variables al objeto reportesModelo
//
//        reportesModelo= new ReportesModelo(categoria, comentario);
//
//        //Mandamos el objeto a la base de datos a travez del objeto de referencia a la base de datos myRef
//        DatabaseReference myRef = database.getReference(id);
//
//        myRef.setValue(reportesModelo);
//        //myRef.child("reportes").child(id).setValue(reportesModelo);

        //creamos un objeto de firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //obtenemos el usuario actual
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //obtenemos una referencia a la base de datos con el user actual
        DatabaseReference myRef = database.getReference(currentUser.getUid());
        myRef.setValue(", World!");
        readFireBase();
    }

    //        //creamos un objeto de firebase
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        //obtenemos el usuario actual
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        //obtenemos una referencia a la base de datos con el user actual
//        DatabaseReference myRef = database.getReference(currentUser.getUid());
//        myRef.setValue("Hello, World!");
//
//        readFireBase();

    private String obtenerCategoriaSeleccionada(){
        radioBtnBache = findViewById(R.id.radioBtnBache);
        radioBtnLuz = findViewById(R.id.radioBtnLuz);
        radioBtnRobo = findViewById(R.id.radioBtnRobo);

        String categoria = "";
        if (radioBtnBache.isChecked()){
            categoria = "Bache";
        }else if (radioBtnLuz.isChecked()) {
            categoria = "Luz";
        }else{
            categoria = "Robo";
        }
        return categoria;
    }

    public void readFireBase(){
        //creamos un objeto de firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //obtenemos el usuario actual
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference myRef = database.getReference(currentUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                editTextComentario.setText(snapshot.getValue().toString()); // <-- Esta línea escribe en el edittext en pantalla el valor de la base de datos
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}