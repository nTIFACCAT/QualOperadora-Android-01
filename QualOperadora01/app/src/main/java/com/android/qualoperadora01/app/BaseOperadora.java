package com.android.qualoperadora01.app;

import android.os.Bundle;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 *
 */
public class BaseOperadora extends Activity {

    // Constante para identificar a sub-activity lançada pelo botão agenda
    private static final int SELECIONAR_CONTATO = 1;
    private static final int GRAVAR_CONTATO = 2;
    private static final String CATEGORIA="BaseOperadora"; // Para uso de log para ver onde está imprimindo as mensagens
    private String nomeContato=null; // Variável que recebe o nome do contato para exibir na tla depois da consulta.
    final Telefone fone = new Telefone(); // Instancia do objeto telefone para setar os dados do telefone pesquisado

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btBuscar = (Button) findViewById(R.id.btBuscar);
        final EditText txtTelefone = (EditText) findViewById(R.id.txtFone);

        btBuscar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Pega a conectividade do contexto o qual o metodo foi chamado
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                //Cria o objeto netInfo que recebe as informacoes da NEtwork
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                //Se o objeto for nulo ou nao tem conectividade retorna mensagem de erro, senão segue a execução
                if ((netInfo != null) && (netInfo.isConnectedOrConnecting()) && (netInfo.isAvailable())) {
                    if (txtTelefone.getText().toString().equals("")||txtTelefone.length()<10) {
                        showMessage("O telefone informado está nulo ou é inválido. Verifique se o DDD foi informado.");
                        return;
                    } else {
                       // Seta null na lista para atualizar os dados quando for feita uma nova consulta
                       final ArrayList<String> detalhes = null;
                       buscarTelefone(txtTelefone.getText().toString());
                    }
                } else {
                    showMessage("Verifique sua conexão com a internet!");
                    return;
                }
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        EditText telefone = (EditText) findViewById(R.id.txtFone);

        switch (item.getItemId()){
            case R.id.action_ligar :
                Ligar(telefone.getText().toString());
                return true;
            case R.id.action_agenda :
                Agenda();
                return true;
            case R.id.action_gravar :
                Gravar(telefone.getText().toString());
                return true;

            default :
                return  super . onOptionsItemSelected ( item );

        }
    }



    /**
     *
    * Método que faz a requisição dos dados para a classe Http
    */
    public void buscarTelefone (final String telefone){
        // Instancia a ListView para visualizar os detalhes
        final ListView lista = (ListView) findViewById(R.id.listaDetalhes);
        // Cria uma string para receber os dados utilizados no array declarado logo abaixo.
        final ArrayList<String> detalhes = new ArrayList<String>();
        // Instancia o adapter que recebe o array com os detalhes
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, detalhes);
        // Classe telefone que receberá os valores

        final ProgressDialog dialogo = new ProgressDialog(this);

        // Instancia uma AsyncTask para executar a pesquisa na web em uma Thread fora da principal.
        AsyncTask<String, Void, JSONObject> task = new AsyncTask<String, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(String... strings) {
                Http http = new Http();
                /*
                * Método que executa automaticamente a AssyncTask
                * Faz o processamento em Background
                * */
                //Chama o método que executa a pesquisa que retorna um Json
                JSONObject json = http.consultaNumero(telefone);
                //Passa o json para o método onPostExecute
                return json;
            }

            @Override
            protected void onPreExecute() {
            /*
            * Método chamado antes da execução da Thread
            * Utilizado também para apresentar uma mensagem de que o processamento está sendo executado
            * */
                super.onPreExecute();
                Log.i(CATEGORIA, "onPreExecute");
                dialogo.setMessage("Pesquisando. Aguarde...");
                dialogo.show();
            }

            @Override
            protected void onPostExecute(JSONObject json) {
              /*
              * Método executado a atualização da interface na Thread principal
              * Utilizado para atualizar as views da thread principal
              *
              * */
                super.onPostExecute(json);
                Log.i(CATEGORIA , "onPreExecute");
                /* Atualiza a view da tela principal*/


                /* Define valores da ListaDetalhes (ListView com duas linhas) que trará na tela os valores
                   Estado e Portabilidade
                */

                if (json!= null) {
                    try {
                        // Seta os dados na classe telefone
                        fone.setNumero(telefone);
                        fone.setOperadora(json.getString("operadora"));
                        fone.setEstado(json.getString("estado"));
                        fone.setPortabilidade(Boolean.parseBoolean(json.getString("portabilidade")));
                    } catch (JSONException e) {
                        Log.e("Erro Json", "Erro no parsing JSON");
                        e.printStackTrace();
                    }
                }
                // Instancia uma string para receber o valor booleano de portabilidae da classe java
                String portabilidade = new String();
                if (fone.getPortabilidade()) {
                    portabilidade = "Sim";
                } else {
                    portabilidade = "Não";
                }
                //Adiciona os valores no arraylist para compor a lista do adapter abaixo
                detalhes.add(new String("Estado: " + fone.getEstado().toString()));
                detalhes.add(new String("Portabilidade: " + portabilidade.toString()));
                detalhes.add(new String("Nome: "+nomeContato));
                // Seta os valores na lista
                lista.setAdapter(adapter);
                ImageView imgOperadora = (ImageView) findViewById(R.id.imageView);
                if (fone.getOperadora().equals("Vivo - Celular")) {
                    imgOperadora.setImageResource(R.drawable.vivo);
                } else if (fone.getOperadora().equals("TIM - Celular")) {
                    imgOperadora.setImageResource(R.drawable.tim);
                } else if (fone.getOperadora().equals("Claro - Celular")) {
                    imgOperadora.setImageResource(R.drawable.claro);
                } else if (fone.getOperadora().equals("Oi - Celular")||fone.getOperadora().equals("Oi - Fixo")) {
                    imgOperadora.setImageResource(R.drawable.oi);
                } else {
                    imgOperadora.setImageResource(R.drawable.warning);
                }
                // Finaliza a dialog que estava executando
                if (dialogo.isShowing()) {
                    dialogo.dismiss();
                }
            }
        };
        // Executa a task (AsyncTask) acima com todos os métodos
        task.execute();
    };


    // Método que recebe um número e faz a ligação
    protected void Ligar(String numero){

        if (numero.toString().equals("")) {
            showMessage("O telefone deve ser informado!");
            return;
        } else{
            //Representa o endereço que desejamos abrir
            String tel = "tel:" + numero.toString();
            Uri uri = Uri.parse(tel);
            //Cria a Intent com o endereço que fará a ligação
            Intent i = new Intent(Intent.ACTION_CALL, uri);
            //Envia a mensagem para o sistema operacional
            startActivity(i);
        }
    }

    // Método que chama a agenda do telefone
    protected void Agenda (){
        // Visualiza a lista de contatos
        Uri uri = Uri.parse("content://com.android.contacts/contacts/");
        Intent i = new Intent(Intent.ACTION_PICK, uri);
        startActivityForResult(i, SELECIONAR_CONTATO);
    }


    protected void Gravar(String numero) {
        // Pega telefone do campo e grava como contato do telefone
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, "Novo Contato");
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, numero);
        startActivity(intent);
    }


    // Método que recebe o retorno da intent chamada
    protected void onActivityResult(int requestCode, int resulTCode, Intent intent){
        // Recebe uma intent de retorno o qual podemos recuperar os dados, ou seja.
        // Ao retornar da intent chamada o Android traz uma nova intent como retorno e então podemos manipular os dados de retorno

        // Text que recebe o número selecionado, representa a txtFone do layout da activity_main.xml
        final EditText tFone = (EditText) findViewById(R.id.txtFone);

        /*
        * Se não for selecionado nenhum contato então vai retornar 0
        * Se for diferente de zero e o retonro for SELECIONARCONTATO que é o código da intent que chamou a agenda
        * então trata os números do contato
        */
        if (resulTCode!=0&&requestCode==SELECIONAR_CONTATO) {

            //Uri que identifica o contato, resultado do click no contato da agenda do android. Trazido pela intent de retorno
            Uri uri = intent.getData();
       /*Busca o contato no banco de dados do telefone utilizando a Uri do contato selecionado
        O cursor c contém os dados do contato selecionado
        */
            Cursor c = getContentResolver().query(uri, null, null, null, null);
            c.moveToNext();

        /*
        * O contato não possui todos os dados necessários referentes aos números , pois são guardados em tabelas diferentes
        *  Então pega-se o idContato da Uri atual para servir de filtro para pesquisa no banco de dados que contém os demais dados
        * */

            long idContato = ContentUris.parseId(uri);
            this.nomeContato = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

        /*
         *  Novo cursor para buscar os demais dados do contato. O primeiro parâmentro, obrigatório, é a URI onde constao os dados dos contatos, o terceiro é o parâmetro de filtro,
         *  Funciona como a cláusula Where de uma consulta SQL
         */

            //Array com os número do contato
            final ArrayList<String> numeros = new ArrayList<String>();
            // Adapter para a spinner dos números
            ArrayAdapter<String> adapterNumeros = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, numeros);

            Cursor telefones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + idContato, null, null);
            //Posiciona o cursor
            telefones.moveToFirst();
            //Recupera os números do contato se o resultado da pesquisa for maior que zero
            if (telefones.getCount() > 0) {
                Log.i("Telefones Count ", String.valueOf(telefones.getCount()));
                for (int i = 0; i < telefones.getCount(); i++) {
                    //Preenche o array numeros com os números do contato
                    numeros.add(telefones.getString(telefones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    telefones.moveToNext();
                }

            /*
            * Caso o contato tenha mais que um número então exibe uma mensagem de alerta com o array de números,
            * senão seta no campo de pesquisa o único número do contato
            */

                if (numeros.size() > 1) {
                    tFone.setText(numeros.get(0));
                    final AlertDialog.Builder alertaNumeros = new AlertDialog.Builder(this);
                    alertaNumeros.setTitle("Contato: " + nomeContato);
                    alertaNumeros.setPositiveButton("Ok", null);
                    alertaNumeros.setAdapter(adapterNumeros, null);
                    alertaNumeros.setSingleChoiceItems(adapterNumeros, 0, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            tFone.setText(numeros.get(i));
                        }
                    });
                    alertaNumeros.show();
                } else {
                    tFone.setText(numeros.get(0));
                }
            } else {
                showMessage("Não existe número cadastrado.");
                tFone.setText("");
            }

            c.close();
            telefones.close();

        }
    }

    private void showMessage(String mensagem){
        AlertDialog.Builder msg = new AlertDialog.Builder(BaseOperadora.this);
        msg.setIcon(R.drawable.ic_stat_alerts_and_states_warning);
        msg.setTitle("Operadora");
        msg.setMessage(mensagem);
        msg.setNeutralButton("OK", null);
        msg.show();
    }
}