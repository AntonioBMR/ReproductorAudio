package com.antonio.android.reproductoraudio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class ServicioAudio extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener{

    // aleatoria
    // bucle
    // seekbar
    // previous next
    private int cont;
    private MediaPlayer mp;
    private int  milisegundo=500;
    private enum Estados{
        idle,
        initialized,
        prepairing,
        prepared,
        started,
        paused,
        completed,
        sttoped,
        end,
        error
    };
    private Estados estado;
    public static final String PLAY="play";
    public static final String PLAYNEW="playNew";
    public static final String STOP="stop";
    public static final String ADD="add";
    public static final String CONTADOR="contador";
    public final static String DURACION ="duracion";
    public static final String PAUSE="pause";
    public static final String SEEKMOVE="mover seekbar";
    public static final String SIGUIENTE="siguiente";
    public static final String ANTERIOR="anterior";
    public final static String SEEKPROGRESS ="progreso seekbar";
    private ReproduccionEnCurso rec;
    public final static String COMPLETADA ="completada";
    private String rutaCancion=null;
    private ArrayList<String> canciones;
    private boolean reproducir;
    private boolean pause;
    /* ******************************************************* */
    // METODOS SOBREESCRITOS //
    /* ****************************************************** */

    @Override
    public void onCreate() {
        super.onCreate();
        canciones=new ArrayList<String>();
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int r = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(r==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mp = new MediaPlayer();
            mp.setOnPreparedListener(this);
            mp.setOnCompletionListener(this);
            mp.setWakeMode(this,PowerManager.PARTIAL_WAKE_LOCK);
            estado = Estados.idle;
        } else {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        //mp.reset();
        mp.release();
        mp = null;
        rec.cancel(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        //String dato = intent.getStringExtra("cancion");
        if(action.equals(PLAY)){
            play();
        }else if(action.equals(PLAYNEW)){
            playNew();
        }else if(action.equals(ADD)){
            canciones = intent.getStringArrayListExtra("canciones");
            cont=intent.getExtras().getInt("posicion");
            add(canciones.get(cont).toString());
        }else if(action.equals(STOP)){
                    stop();
        }else if(action.equals(PAUSE)) {
                pause();
        }
        else if(action.equals(SIGUIENTE)) {
            if (canciones.size() > 0)
            siguiente();
        }else if(action.equals(ANTERIOR)) {
            if (canciones.size() > 0)
                anterior();
        }else if (action.equals(SEEKMOVE)) {
            milisegundo = intent.getIntExtra("milisegundo", -1);
            mp.seekTo(milisegundo);

        }
        return super.onStartCommand(intent, flags, startId);
    }

    /* ******************************************************* */
    // INTERFAZ PREPARED LISTENER //
    /* ****************************************************** */

    @Override
    public void onPrepared(MediaPlayer mp) {
        estado = Estados.prepared;
        Intent in = new Intent(DURACION);
        in.putExtra("duracion", mp.getDuration());
        sendBroadcast(in);
        milisegundo=500;
        rec=new ReproduccionEnCurso();
        rec.execute();
        if(reproducir){
            mp.start();
            estado = Estados.started;
        }

    }

    /* ******************************************************* */
    // INTERFAZ COMPLETED LISTENER //
    /* ****************************************************** */

    @Override
    public void onCompletion(MediaPlayer mp) {
        estado = Estados.completed;
        if ((cont + 1) != canciones.size()) {
            mp.seekTo(0);
            siguiente();
            play();
        }
    }

    /* ******************************************************* */
    // INTERFAZ AUDIO FOCUS CHANGED //
    /* ****************************************************** */


    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                play();
                mp.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mp.setVolume(0.1f, 0.1f);
                break;
        }
    }

    /* ******************************************************* */
    // METODOS DE AUDIO //
    /* ****************************************************** */

    private void play(){
        if(rutaCancion != null){
            if(estado == Estados.error){
                estado = Estados.idle;
            }
            if(estado == Estados.idle){
                try {
                    mp.setDataSource(rutaCancion);
                    estado = Estados.initialized;
                } catch (IOException e) {
                    estado= Estados.error;
                }
            }
            if(estado == Estados.initialized ||
                    estado == Estados.sttoped){
                reproducir = true;
                mp.prepareAsync();
                estado = Estados.prepairing;
            } else if(estado == Estados.prepairing) {
                reproducir = true;
            }
            if(estado == Estados.prepared ||
                    estado == Estados.paused ||
                    estado == Estados.completed
                    ) {
                mp.start();
                estado = Estados.started;
            }if (estado==Estados.completed) {
                pause = false;
                milisegundo = 500;
                mp.start();
                rec = new ReproduccionEnCurso();
                rec.execute();
                estado=Estados.started;
            }
        }
    }

    private void playNew(){
        if(rutaCancion != null){
            if(estado==Estados.started||estado==Estados.paused){
                    reproducir=false;
                mp.reset();
                estado=Estados.idle;
            }
            if(estado == Estados.error){
                estado = Estados.idle;
            }
            if(estado == Estados.idle){
                try {
                    mp.setDataSource(rutaCancion);
                    estado = Estados.initialized;
                } catch (IOException e) {
                    estado= Estados.error;
                }
            }
            if(estado == Estados.initialized ||
                    estado == Estados.sttoped){
                reproducir = true;
                mp.prepareAsync();
                estado = Estados.prepairing;
            } else if(estado == Estados.prepairing) {
                reproducir = true;
            }
            if(estado == Estados.prepared ||
                    estado == Estados.paused ||
                    estado == Estados.completed
                    ) {
                estado = Estados.started;
            }if (estado==Estados.completed) {
                pause = false;
                milisegundo = 500;
                mp.start();
                rec = new ReproduccionEnCurso();
                rec.execute();
                estado=Estados.started;
            }if(estado==Estados.started){
                mp.stop();
                estado=Estados.idle;
            }
        }
    }

    private void stop(){
        if(estado == Estados.prepared ||
                estado == Estados.started ||
                estado == Estados.paused ||
                estado == Estados.completed){
            rec.cancel(true);
            mp.seekTo(0);
            mp.stop();
            estado = Estados.sttoped;
        }
        reproducir = false;
    }
    private void siguiente(){
        cont=cont+1;
        if (cont == canciones.size()) {
            cont = 0;
        }
        add(canciones.get(cont).toString());
        playNew();
    }

    private void anterior(){
        if (cont == 0) {
            cont = canciones.size()-1;
        }else{
            cont=cont-1;
        }
        add(canciones.get(cont).toString());
        playNew();
    }

    private void pause() {
        if(estado==Estados.started){
            mp.pause();
            estado=Estados.paused;
           pause=true;
        }
    }

    private void add(String cancion){
        this.rutaCancion = cancion;
        Log.v("ADD",cancion);
        mp.reset();
    }
    class ReproduccionEnCurso extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for (milisegundo=500;milisegundo<mp.getDuration();milisegundo=milisegundo+500){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (this.isCancelled()){
                    return null;
                }
                while (pause==true){}
                publishProgress(milisegundo);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Intent in=new Intent(SEEKPROGRESS);
            in.putExtra("segundo", values[values.length - 1]);
            sendBroadcast(in);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}