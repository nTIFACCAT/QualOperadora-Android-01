package com.android.qualoperadora01.app;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MostraAgenda extends ListActivity {
    private static final String CATEGORIA="BaseOperadora"; // Para uso de log para ver onde está imprimindo as mensagens
    // Objeto JsonArray que vai receber os dados da operadora para utilizar na leitura dos ícones da operadora
    JSONArray jsonArray;
    ArrayList<Operadora> arrayList = new ArrayList<Operadora>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ProgressDialog dialogo = new ProgressDialog(this);
        super.onCreate(savedInstanceState);

/*

        //dialogo.setMessage("Montando lista. Aguarde...");
        //dialogo.show();

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

        setListAdapter(new OperadoraAdapter(this,arrayList));
        //dialogo.dismiss();
*/


        buscarTelefones();

    }




    public void buscarTelefones() {

        final ProgressDialog dialogo = new ProgressDialog(this);

        /* Instancia uma AsyncTask para executar a pesquisa na web em uma Thread fora da principal.
        AsyncTask<Params, Progress, result>
*/

        AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
            /*
            * Método chamado antes da execução da Thread
            * Utilizado também para apresentar uma mensagem de que o processamento está sendo executado
            * */
                super.onPreExecute();
                Log.i(CATEGORIA, "onPreExecute");
                dialogo.setMessage("Pesquisando. Aguarde... Pode demorar alguns minutos...");
                dialogo.show();
            }


            @Override
            protected String doInBackground(String... strings) {

                // Faz a busca na web dos dados ada operadora de todos os telefones da agenda
                Http http = new Http();

                // JsonObject - recebe os telefones formando um objeto para fazer o post
                JSONObject jsonObject = new JSONObject();
                // JonArray - recebe os telefones formando um array para ser incluído no objeto
                JSONArray jsonArray = new JSONArray();
                // Visualiza a lista de contatos do Android
                Uri uri = Uri.parse("content://com.android.contacts/contacts/");
                //System.out.println("Contato ID: " + uri.toString());
                Cursor c = getContentResolver().query(uri, null, null, null, null);

                while (c.moveToNext()) {
                    long idContato = Long.parseLong(c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID)));
                    String nomeContato = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    System.out.println("ID " + idContato + "Nome: " + nomeContato);

                    Cursor telefones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + idContato, null, null);
                    //Posiciona o cursor
                    telefones.moveToFirst();
                    //Recupera os números do contato se o resultado da pesquisa for maior que zero
                    if (telefones.getCount() > 0) {
//                  Log.i("Telefones Count ", String.valueOf(telefones.getCount()));
                        for (int i = 0; i < telefones.getCount(); i++) {
                            String fone= new String(telefones.getString(telefones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            // Somente para teste, remove o primeiro caracter, o ZERO
                            //fone = fone.substring(1);
                            System.out.println("Telefone :"+fone);

                            //publishProgress(i);
                            jsonArray.put(fone);
                            telefones.moveToNext();
                        }
                    }
                    telefones.close();
                }

                try {
                    jsonObject.put("phones",jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Imprime o resultado formado pelo JSONOBJECT
                System.out.println("JSON:  "+jsonObject);
                // Passa para a classe http o jsonobject para busca dos dados
                jsonArray = http.consultaNumeros(jsonObject);


              /* Depois de obter dados do site abaixo manipula os dados montando a lista de contatos com os dados da operadora.*/


                //dialogo.setMessage("Montando lista...");

                Log.i(CATEGORIA , "onPostExecute");

                //Imprime o resultado do httppost
                System.out.println("JSON retornado:  " +jsonArray );

                Log.i("Dados Operadoras","String retorno convertido para Json");


                // Visualiza a lista de contatos do Android
                c = getContentResolver().query(uri, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
                c.moveToFirst();

                while (c.moveToNext()) {

                    long idContato = Long.parseLong(c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID)));
                    String nomeContato = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    Cursor telefones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + idContato, null, null);
                    //Posiciona o cursor
                    telefones.moveToFirst();

                    //Recupera os números do contato se o resultado da pesquisa for maior que zero
                    if (telefones.getCount() > 0) {

                        for (int i = 0; i < telefones.getCount(); i++) {
                            String fone= new String(telefones.getString(telefones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            // Laço que lê o Array Json com os dados da operadora para ver qual ícone usar para o número do contato

                            //Atualiza o progresso de execução
                            publishProgress(c.getPosition(),c.getCount());

                            // Caso não retornem dados da web, mostra a agenda com ícone de inválido
                            if (jsonArray!=null) {


                                for (int x = 0; x < jsonArray.length(); x++) {

                                    try {
                                        JSONObject jsonObj = jsonArray.getJSONObject(x);
                                        String telefoneOriginal = jsonObj.getString("telefoneOriginal");
                                        if (telefoneOriginal.equals(fone)) {
                                            String operadora = jsonObj.getString("operadora");
                                            if (operadora.equals("Vivo - Celular")) {
                                                arrayList.add(new Operadora(nomeContato, fone, Operadora.VIVO));
                                            } else if (operadora.equals("TIM - Celular")) {
                                                arrayList.add(new Operadora(nomeContato, fone, Operadora.TIM));
                                            } else if (operadora.equals("Claro - Celular")) {
                                                arrayList.add(new Operadora(nomeContato, fone, Operadora.CLARO));
                                            } else if (operadora.equals("Oi - Celular") || operadora.equals("Oi - Fixo")) {
                                                arrayList.add(new Operadora(nomeContato, fone, Operadora.OI));
                                            } else {
                                                arrayList.add(new Operadora(nomeContato, fone, Operadora.INVALIDA));
                                            }

                                            //Força a saída do laço for
                                            x = jsonArray.length();

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }else{
                                System.out.println("A Web retornou Json Nulo...");
                                arrayList.add(new Operadora(nomeContato, fone, Operadora.INVALIDA));
                            }

                            telefones.moveToNext();
                        }
                    }

                    telefones.close();
                }

                return null;
            }


            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                // Atualiza a interface
                setListAdapter(new OperadoraAdapter(MostraAgenda.this,arrayList));
                if (dialogo.isShowing()) {
                    dialogo.dismiss();
                }
            }

            //Publica o progresso de execução
            @Override
            protected void onProgressUpdate(Integer... values) {
                ProgressBar mProgress;
                super.onProgressUpdate(values);
                Integer progress = values[0];
                Integer total = values[1];
                Log.i("progress", String.valueOf(progress));
                Log.i("total", String.valueOf(total));
                dialogo.setMessage("Total Contatos: "+total +"    Lidos: "+progress);
                //mProgress = new ProgressBar(MostraAgenda.this);
                //mProgress.setProgress(progress);


            }
        };

        // Executa a task (AsyncTask) acima com todos os métodos
        task.execute();


    }




    public void mostrarDados(){

        new Thread(){
            public void run(){
                //Faz o processamento em background

                // Vai ser usado para montar uma lista dos contatos
                String jsonOperadora = getIntent().getStringExtra("dados");
                Log.i("Dados Operadoras",jsonOperadora);

                try {

                    jsonArray = new JSONArray(jsonOperadora);
                    Log.i("Dados Operadoras","String retorno convertido para Json");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


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


                //setListAdapter(new OperadoraAdapter(this,arrayList));
             /*
            * Método chamado antes da execução da Thread
            * Utilizado também para apresentar uma mensagem de que o processamento está sendo executado
            *
                Log.i(CATEGORIA, "onPreExecute");
                dialogo.setMessage("Montando Lista. Aguarde...");
                dialogo.show();

        setListAdapter(new OperadoraAdapter(this,arrayList));
        dialogo.dismiss();
*/


                //atualiza a interface
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //setListAdapter(new OperadoraAdapter(this,arrayList));

                    }
                });
            }
        }.start();

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
