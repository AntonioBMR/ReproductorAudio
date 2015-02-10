package com.antonio.android.reproductoraudio;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
/**
 * Created by Antonio on 06/02/2015.
 */
public class Adaptador extends ArrayAdapter<String> {
private Context contexto;
private ArrayList<String> lista;
private int recurso;
static LayoutInflater i;

public Adaptador(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        this.contexto = context;
        this.lista = objects;
        this.recurso = resource;
        this.i = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

public static class ViewHolder {
    public TextView tv;
}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = i.inflate(recurso, null);
            vh = new ViewHolder();
            vh.tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        String cantante;
        cantante = lista.get(position);
        vh.tv.setText(cantante);
        return convertView;
    }
}