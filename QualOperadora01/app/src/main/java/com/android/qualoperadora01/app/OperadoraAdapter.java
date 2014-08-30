package com.android.qualoperadora01.app;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RICARDO on 18/08/2014.
 */
public class OperadoraAdapter extends BaseAdapter {
    private Context context;
    private List<Operadora> lista;

    public OperadoraAdapter(Context context, List<Operadora> lista){
        this.context = context;
        this.lista = lista;

    }


    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int i) {
        return lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Operadora operadora = lista.get(i);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.operadora_adapter_layout, null);

        TextView textNome = (TextView) v.findViewById(R.id.nome);
        textNome.setText(operadora.nomeContato);

        TextView textFone = (TextView) v.findViewById(R.id.fone);
        textFone.setText(operadora.foneContato);

        ImageView img = (ImageView) v.findViewById(R.id.img);
        img.setImageResource(operadora.getImagem());

        return v;
    }
}
