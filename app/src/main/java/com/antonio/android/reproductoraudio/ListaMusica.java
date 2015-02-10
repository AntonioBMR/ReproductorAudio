package com.antonio.android.reproductoraudio;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Antonio on 07/02/2015.
 */
public class ListaMusica extends Activity {
    ArrayList<String> listaN;
    ArrayList<String> listaR;
    ArrayList<String> listaNA;
    ArrayList<String> listaRA;
    ListView lv;
    private static final int GRABADOR=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_listamusica);
        lv=(ListView)findViewById(R.id.lvLM);
        Uri ur= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String orden = "artist";
        listaN=new ArrayList<String>();
        listaR=new ArrayList<String>();
        listaNA=new ArrayList<String>();
        listaRA=new ArrayList<String>();
        Cursor cursor=getContentResolver().query(ur, null, null, null, orden);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int numc=cursor.getColumnIndex("title");
            String nombre = cursor.getString(numc);
            listaN.add(nombre);
            numc=cursor.getColumnIndex("_data");
            String ruta = cursor.getString(numc);
            listaR.add(ruta);
            cursor.moveToNext();
        }
        Adaptador ad = new Adaptador(this,R.layout.detalle,listaN);
        lv.setAdapter(ad);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nombre = listaN.get(position).toString();
                String rutas = listaR.get(position).toString();
                System.out.println("nombre y ruta "+nombre+" "+rutas);
                listaNA.add(nombre);
                listaRA.add(rutas);

            }
        });
    }
    public void agregar(View v){
        if(!listaNA.isEmpty()) {
            Intent intent = new Intent(ListaMusica.this, MainActivity.class);
            intent.putExtra("canciones", listaNA);
            intent.putExtra("rutas", listaRA);
            setResult(Activity.RESULT_OK, intent);
            ListaMusica.this.finish();
        }else{
            tostada("elija una cancion");
        }
    }
    public void grabar(View v){
        Intent intent = new Intent(getApplicationContext(), Grabador.class);
        startActivityForResult(intent, GRABADOR);
    }
    public Toast tostada(String t) {
        Toast toast =
                Toast.makeText(getApplicationContext(),
                        t + "", Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }
    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GRABADOR) {
                Uri ur= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String orden = "artist";
                Cursor cursor=getContentResolver().query(ur, null, null, null, orden);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    int numc=cursor.getColumnIndex("title");
                    String nombre = cursor.getString(numc);
                    listaN.add(nombre);
                    numc=cursor.getColumnIndex("_data");
                    String ruta = cursor.getString(numc);
                    listaR.add(ruta);
                    cursor.moveToNext();
                }
                Adaptador ad = new Adaptador(this,R.layout.detalle,listaN);
                lv.setAdapter(ad);
                ad.notifyDataSetChanged();
            }
        }
    }
}



