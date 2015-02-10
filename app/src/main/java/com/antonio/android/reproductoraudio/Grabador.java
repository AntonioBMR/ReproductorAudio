package com.antonio.android.reproductoraudio;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Grabador extends Activity {

    private MediaRecorder grabador;
    private TextView tv;
    private File carpeta;
    private boolean guardar = false;
    private String nombre = "";
    private Button btguardar, btborrar, btgrabar, btparar;
    private static final int GRABADOR=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_grabador);
        btgrabar = (Button) findViewById(R.id.btgrabar);
        btborrar = (Button) findViewById(R.id.btborrar);
        btguardar = (Button) findViewById(R.id.btguardar);
        btparar = (Button) findViewById(R.id.btparar);
        btparar.setEnabled(false);
        btguardar.setEnabled(false);
        btborrar.setEnabled(false);
        grabador = new MediaRecorder();
        tv = (TextView) findViewById(R.id.tvgrabar);
        carpeta = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC).toString());

    }

    @Override
    protected void onDestroy() {
        if (guardar == false) {
            File archivo = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC) + "/" + nombre + ".mp3");
            archivo.delete();
        }
        super.onDestroy();
    }

    public void grabar(View view) {
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        nombre = "record"+df.format(date);
        grabador.setAudioSource(
                MediaRecorder.AudioSource.MIC);
        grabador.setOutputFormat(
                MediaRecorder.OutputFormat.MPEG_4);
        grabador.setOutputFile(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC) + "/" + nombre + ".mp3");
        grabador.setAudioEncoder(
                MediaRecorder.AudioEncoder.AMR_NB);
        try {
            grabador.prepare();
            grabador.start();
            tv.setText(getString(R.string.grabando));
        } catch (IOException e) {
            e.printStackTrace();
        }
        btparar.setEnabled(true);
        btgrabar.setEnabled(false);
    }

    public void parar(View view) {
        grabador.stop();
        grabador.release();
        tv.setText("");
        btparar.setEnabled(false);
        btguardar.setEnabled(true);
        btborrar.setEnabled(true);

    }
    public void volver(View view) {
        grabador.stop();
        grabador.release();
        tv.setText("");
        btguardar.setEnabled(true);
        btborrar.setEnabled(true);
        setResult(GRABADOR);
        Grabador.this.finish();

    }
    public void guardar(View view) {
        guardar = true;
        Intent intent = new Intent
                (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File archivo = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC) + "/" + nombre + ".mp3");
        Uri uri = Uri.fromFile(archivo);
        intent.setData(uri);
        this.sendBroadcast(intent);
        tostada(getString(R.string.grabacionG));
    }

    public void borrar(View view) {
        File archivo = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC) + "/" + nombre + ".mp3");
        archivo.delete();
    }
    public Toast tostada(String t) {
        Toast toast =
                Toast.makeText(getApplicationContext(),
                        t + "", Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }
}