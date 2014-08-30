package com.android.qualoperadora01.app;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MostraAgenda extends ListActivity {
    private static final String CATEGORIA="BaseOperadora"; // Para uso de log para ver onde está imprimindo as mensagens
    // Objeto JsonArray que vai receber os dados da operadora para utilizar na leitura dos ícones da operadora
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vai ser usado para montar uma lista dos contatos
        //ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        String jsonOperadora = getIntent().getStringExtra("dados");
        Log.i("Dados Operadoras",jsonOperadora);

        try {

            jsonArray = new JSONArray(jsonOperadora);
            Log.i("Dados Operadoras","String retorno convertido para Json");

        } catch (JSONException e) {
            e.printStackTrace();
        }

/*
        try {

            JSONObject jsonObject = new JSONObject(jsonOperadora);
            Log.i("Dados Operadoras","String retorno convertido para Json");

        } catch (JSONException e) {
            e.printStackTrace();
        }
*/

        ArrayList<Operadora> arrayList = new ArrayList<Operadora>();

        // Visualiza a lista de contatos do Android
        Uri uri = Uri.parse("content://com.android.contacts/contacts/");
        //System.out.println("Contato ID: " + uri.toString());
        Cursor c = getContentResolver().query(uri, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);


        while (c.moveToNext()) {

            long idContato = Long.parseLong(c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID)));
            String nomeContato = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            Cursor telefones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + idContato, null, null);
            //Posiciona o cursor
            telefones.moveToFirst();

            //Recupera os números do contato se o resultado da pesquisa for maior que zero
            if (telefones.getCount() > 0) {
//                  Log.i("Telefones Count ", String.valueOf(telefones.getCount()));
                for (int i = 0; i < telefones.getCount(); i++) {
                    //HashMap<String,String> item = new HashMap<String, String>();
                    String fone= new String(telefones.getString(telefones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));


                    // Laço que lê o Array Json com os dados da operadora para ver qual ícone usar para o número do contato


                    for (int x=0;x<jsonArray.length();x++){

                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                            String telefoneOriginal = jsonObject.getString("telefoneOriginal");
                            if (telefoneOriginal.equals(fone)){
                                String operadora = jsonObject.getString("operadora");
                                if (operadora.equals("Vivo - Celular")) {
                                    arrayList.add(new Operadora(nomeContato,fone,Operadora.VIVO));
                                } else if (operadora.equals("TIM - Celular")) {
                                    arrayList.add(new Operadora(nomeContato,fone,Operadora.TIM));
                                } else if (operadora.equals("Claro - Celular")) {
                                    arrayList.add(new Operadora(nomeContato,fone,Operadora.CLARO));
                                } else if (operadora.equals("Oi - Celular")||operadora.equals("Oi - Fixo")) {
                                    arrayList.add(new Operadora(nomeContato,fone,Operadora.OI));
                                } else {
                                    arrayList.add(new Operadora(nomeContato,fone,Operadora.INVALIDA));
                                }

                                //Força a saída do laço for
                                x=jsonArray.length();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }





                    }


                    // Somente para teste, remove o primeiro caracter, o ZERO
                    //fone = fone.substring(1);
                    //item.put("nome",nomeContato);
                    //item.put("fone",fone);
                    ///Operadora op = new Operadora(nomeContato,fone,0);

                    //arrayList.add(new Operadora(nomeContato,fone,Operadora.OI));

                    telefones.moveToNext();
                   // list.add(item);
                }
            }

            telefones.close();
        }

        //setContentView(R.layout.activity_mostra_agenda);
        //ArrayList<Integer> lista = getIntent().getIntegerArrayListExtra("dados");
        //String[] from = new String[]{"nome","fone"};
        //int [] to = new int[] { android.R.id.text1, android.R.id.text2};
        //int layoutNativo = android.R.layout.two_line_list_item;
        //setListAdapter(new SimpleAdapter(this,list,layoutNativo,from,to));

        setListAdapter(new OperadoraAdapter(this,arrayList));




    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mostra_agenda, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
