package com.bervatim.android.alarmas;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText et1Alarma, et2Alarma, et3Alarma, et4Alarma, et5Alarma;
    private TextView tvTiempoRestante, tvAlarmRestantes;
    private final String FILE="alarmas.txt";
    private ArrayList<Integer> tiempos;
    private int numAlarmas, numAlarmaActual;
    boolean alarmaFinalizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instanciarComponentes();
        instanciarVariables();
        establecerTiempoInicial();
    }

    private void instanciarComponentes()
    {
        et1Alarma = (EditText) findViewById(R.id.et1Alarma);
        et2Alarma = (EditText) findViewById(R.id.et2Alarma);
        et3Alarma = (EditText) findViewById(R.id.et3Alarma);
        et4Alarma = (EditText) findViewById(R.id.et4Alarma);
        et5Alarma = (EditText) findViewById(R.id.et5Alarma);
        tvAlarmRestantes = (TextView) findViewById(R.id.tvAlarmRestantes);
        tvTiempoRestante = (TextView) findViewById(R.id.tvTiempoRestante);

    }

    private void instanciarVariables() {
        tiempos = new ArrayList<Integer>();
        numAlarmas = 0;
        numAlarmaActual = 0;

        alarmaFinalizada = true;
    }

    private void establecerTiempoInicial() {
        //Comprobamos si existe el arcivho
        if (existsFile(FILE)) {
            //En caso de que exista, intentamos rellanar los EdiTexts, si no
            //se rellenan de forma corracte, el archivo.txt estaba corrupto.
            if (!rellenarEditTexts()){
                //Avisamos al usuario de que el archivo era corrupto.
                Toast.makeText(MainActivity.this, "Archivo corrupto, reiniciando " +
                        "parámetros...", Toast.LENGTH_LONG).show();
                //creamos de nuevo el archivo
                crearArchivoAlarmas();
                //Rellenamos los EditText con los valores asignados por defecto
                //en el archivo txt.
                rellenarEditTexts();
            }
        } else {
            //En el caso de que no existiera el archivo txt lo creamos y rellenamos
            //los EditTexts
            crearArchivoAlarmas();
            rellenarEditTexts();
        }
    }

    private void crearArchivoAlarmas() {
        try {
            //Creamos un objeto OutputStreamWriter, que será el que nos permita
            //escribir en el archivo de texto. Si el archivo no existía se creará
            //automáticamente.
            //La ruta en la que se creará el archivo sera /ruta de nuestro programa
            OutputStreamWriter outSWMensaje = new OutputStreamWriter(
                    openFileOutput(FILE, Context.MODE_PRIVATE));
            // Escribimos los 5 tiempos iniciales en el archivo.
            outSWMensaje.write("5\n5\n5\n5\n5\n5\n");
            //Cerramos el flujo de escritura del archivo, este paso es obligaotior,
            //de no hacerlo no se podrá hacceder posteriormente al archivo.
            outSWMensaje.close();

        } catch (Exception e){
            Log.e(null, e.getMessage());
        }
    }

    private boolean rellenarEditTexts() {
        try{
            //Creamos un objeto InputStreamReader, que será el que nos permita
            //leer el contenido del archivo de texto.

            InputStreamReader archivo = new InputStreamReader(
                    openFileInput(FILE));
            //Creamos un objeto buffer, en el que iremos almacenando el contenido
            //del archivo
            BufferedReader br = new BufferedReader(archivo);
            //Por cada EditText leemos una línea y escribimos el contenido en el
            //EditText.
            String texto = br.readLine();
            et1Alarma.setText(texto);
            texto = br.readLine();
            et2Alarma.setText(texto);
            texto = br.readLine();
            et3Alarma.setText(texto);
            texto = br.readLine();
            et4Alarma.setText(texto);
            texto = br.readLine();
            et5Alarma.setText(texto);
            //Cerramos el flujo de lectura dle archivo.
            br.close();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public boolean existsFile(String fileName) {
        for (String tmp : fileList()) {
            if (tmp.equals(fileName))
                return true;
        }
        return false;
    }

    public void onClick(View v) {

        if (et1Alarma.getText().length() != 0
                && et2Alarma.getText().length() != 0
                && et3Alarma.getText().length() != 0
                && et4Alarma.getText().length() != 0
                && et5Alarma.getText().length() != 0) {

            tiempos.add(Integer.parseInt(et1Alarma.getText().toString()));
            tiempos.add(Integer.parseInt(et2Alarma.getText().toString()));
            tiempos.add(Integer.parseInt(et3Alarma.getText().toString()));
            tiempos.add(Integer.parseInt(et4Alarma.getText().toString()));
            tiempos.add(Integer.parseInt(et5Alarma.getText().toString()));

            actualizarArchivoAlarmas();

            lanzarAlarma();
        }
    }

    private void actualizarArchivoAlarmas() {
        try {
            OutputStreamWriter outSWMensaje = new OutputStreamWriter(
                    openFileOutput(FILE, Context.MODE_PRIVATE));
            //Por cada tiempo escrito en los EditText escribimos una línea
            //en el archivo de alarmas.
            for (int i: tiempos) {
                outSWMensaje.write(i + "\n");
            }

            outSWMensaje.close();
        } catch (Exception e) {
            Log.e(null, e.getMessage());
            Toast.makeText(this, "No se pudo crear el archivo de alarmas",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void lanzarAlarma() {

        //Sacamos el tiempo restante para lanzar la alarma actual.
        int i = tiempos.get(numAlarmaActual);
        //Controlamos la alarma que tiene que sonar
        numAlarmaActual++;
        //Variable para controlar cuándo ha finalizado una alarma, para poder
        //lanzar la siguiente.
        alarmaFinalizada = false;
        //Actualizamos el texto de las alarmas restantes.
        tvAlarmRestantes.setText("Alarmas restantes: " + numAlarmas);
        //Instanciamos un nuevo contador quue se lanzará con el tiempo debido
        CountDownTimer timer = new CountDownTimer(i * 60 * 1000, 100) {
            @Override
            public void onTick(long l) {
                //Actualizamos el tiempo restante para la nueva alarma
                tvTiempoRestante.setText(String.valueOf(l/1000) + " s ");
            }

            @Override
            public void onFinish() {
                // Al finalizar reproducimos un archivo de audio guardado en la
                // carpeta raw.
                //MediaPlayer mp = ( MediaPlayer.create(MainActivity.this, R.raw.r2d2);
                //mp.start();
                // Creamos un AlertDialog que avise de la llegada de la alarma.
                AlertDialog.Builder alertDialog;
                alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Alarma");
                alertDialog.setMessage("Es la hora!!");
                // Creamos un botón en el AlertDialog, que será el que nos permita
                // reanudar el tiempo de la siguiente alarma.
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cuando se pulse el botón se marca la alarma como
                        // finalizada, se actualizan las alarmas restantes y
                        // en el caso de no ser la última alarma se vuelve a
                        // ejecutar el método lanzarAlarma.
                        alarmaFinalizada = true;
                        numAlarmas--;
                        tvAlarmRestantes.setText("Alarmas restantes: "
                                + numAlarmas);
                        if (numAlarmaActual < tiempos.size())
                            lanzarAlarma();
                    }
                });
                // Mostramos el AlertDialog al usuario.
                alertDialog.show();
            }
        };
        // Una vez creado el temporizador lo iniciamos.
        timer.start();
        }
    }



