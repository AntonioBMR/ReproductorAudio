package com.antonio.android.reproductoraudio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private ArrayList<String> listaU;
    private ArrayList<String> listaC;
    private ListView lv;
    private TextView tv;
    private Adaptador ad;
    private int posRep, dur;
    private SeekBar sb;

    private BroadcastReceiver duracion = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                dur = bundle.getInt("duracion");
                sb.setMax(dur);
            }}};
    private BroadcastReceiver segundo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int seg = bundle.getInt("segundo");
                sb.setProgress(seg);
            }}};
    private static final int LISTAMUSICA=2;

    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == LISTAMUSICA) {
                listaC= data.getStringArrayListExtra("canciones");
                listaU= data.getStringArrayListExtra("rutas");
                tv.setText(listaC.get(0));
                Intent intent = new Intent(this, ServicioAudio.class);
                intent.putExtra("canciones", listaU);
                intent.putExtra("posicion", 0);
                intent.setAction(ServicioAudio.ADD);
                startService(intent);
                intent.setAction(ServicioAudio.PLAYNEW);
                startService(intent);
                ad.notifyDataSetChanged();
                ad = new Adaptador(this,R.layout.detalle,listaC);
                lv.setAdapter(ad);

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=(ListView)findViewById(R.id.listView);
        listaC=new ArrayList<String>();
        listaU=new ArrayList<String>();
        sb=(SeekBar)findViewById(R.id.seekBar);
        tv=(TextView)findViewById(R.id.tvReproduccion);
        ad = new Adaptador(this,R.layout.detalle,listaC);
        lv.setAdapter(ad);
        registerForContextMenu(lv);
        sb.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent=new Intent();
                    intent.setAction(ServicioAudio.SEEKMOVE);
                    intent.putExtra("milisegundo",sb.getProgress());
                    startService(intent);
                    return false;
                }
                return false;
            }
        });
        registerReceiver(duracion, new IntentFilter(ServicioAudio.DURACION));
        registerReceiver(segundo, new IntentFilter(ServicioAudio.SEEKPROGRESS));
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.contextual, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int id=item.getItemId();
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index= info.position;
        if (id == R.id.borrar) {
            listaC.remove(index);
            listaU.remove(index);
            ad.notifyDataSetChanged();
            ad = new Adaptador(this,R.layout.detalle,listaC);
            lv.setAdapter(ad);
            return true;
        }else if (id == R.id.escuchar) {
            sb.setProgress(0);
            tv.setText(listaC.get(index).toString());
            posRep=index;
            Intent intent = new Intent(MainActivity.this, ServicioAudio.class);
            intent.putExtra("canciones", listaU);
            intent.putExtra("posicion", posRep);
            intent.setAction(ServicioAudio.ADD);
            startService(intent);
            intent.setAction(ServicioAudio.PLAYNEW);
            startService(intent);
            return true;
        }
        return super.onContextItemSelected(item);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void play(View v){
        Intent intent = new Intent(MainActivity.this, ServicioAudio.class);
        intent = new Intent(this, ServicioAudio.class);
        intent.setAction(ServicioAudio.PLAY);
        startService(intent);

    }

    public void stop(View v){
        tv.setText("");
        Intent intent = new Intent(this, ServicioAudio.class);
        intent.setAction(ServicioAudio.STOP);
        startService(intent);
    }

    public void pause(View v){
        Intent intent = new Intent(this, ServicioAudio.class);
        intent.setAction(ServicioAudio.PAUSE);
        startService(intent);
    }
    public void siguiente(View v){
        Intent intent = new Intent(this, ServicioAudio.class);
        intent.setAction(ServicioAudio.SIGUIENTE);
        startService(intent);
    }
    public void anterior(View v){
        Intent intent = new Intent(this, ServicioAudio.class);
        intent.setAction(ServicioAudio.ANTERIOR);
        startService(intent);
    }
    public void add(View v){
        stopService(new Intent(this, ServicioAudio.class));
        Intent intent = new Intent(MainActivity.this,ListaMusica.class);
        startActivityForResult(intent, LISTAMUSICA);
    }

    public void pararServicio(View v){
        stopService(new Intent(this, ServicioAudio.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(duracion);
        unregisterReceiver(segundo);
        super.onDestroy();
    }
}
