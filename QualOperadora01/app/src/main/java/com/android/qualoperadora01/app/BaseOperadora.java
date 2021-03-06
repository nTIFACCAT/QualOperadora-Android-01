package com.android.qualoperadora01.app;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;


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
    // Instancia do layout para poder adicionar os componentes dinamicamente
    //private LinearLayout layoutPrincipal = (LinearLayout) findViewById(R.id.layoutPrincipal);

    final String tickerText = "Você recebeu uma notificação"; //Variável utilizada para notificação de atualização da agenda
    // Detalhes da mensagem, quem enviou e texto
    final CharSequence titulo = "Operadora";
    final CharSequence mensagem = "Faça a atualização da agenda do telefone. ";



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



        // Esconde os campos com os dados de contato para caso for fazer uma nova pesquisa iniba os campos antes te mostrar os dados
        // atualizados
       EditText txtFone = (EditText)findViewById(R.id.txtFone);

        txtFone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtContato = (TextView) findViewById(R.id.txtContato);
                TextView txtEstado = (TextView) findViewById(R.id.txtEstado);
                TextView txtPortabilidade = (TextView) findViewById(R.id.txtPortabilidade);
                ImageView imageOperadora = (ImageView) findViewById(R.id.imgOperadora);

                txtContato.setVisibility(View.INVISIBLE);
                txtEstado.setVisibility(View.INVISIBLE);
                txtPortabilidade.setVisibility(View.INVISIBLE);
                imageOperadora.setVisibility(View.INVISIBLE);




            }
        });



        /*
        *   Desativado temporariamente
         *  Vai ser utilizado para tarefa em background de atualização da agenda.
         *
        *   criarNotificacao(this,tickerText,titulo,mensagem);
        * */




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
            case R.id.action_atualizarAgenda:

                Intent it = new Intent(BaseOperadora.this, MostraAgenda.class);
//                it.putExtra("dados", String.valueOf(json));
                startActivity(it);
                //buscarTelefones();

               /*
                AlertDialog.Builder alert = new AlertDialog.Builder(BaseOperadora.this);
                alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buscarTelefones();
                    }
                });
                alert.setNegativeButton("Não",null);
                alert.setMessage("Deseja atualizar toda sua agenda com ícones da operadora? ");
                alert.setIcon(R.drawable.ic_stat_alerts_and_states_warning);
                alert.setTitle("Operadora");
                alert.show();
                return true;

                */

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
        //final ListView lista = (ListView) findViewById(R.id.listaDetalhes);
        // Cria uma string para receber os dados utilizados no array declarado logo abaixo.
        //final ArrayList<String> detalhes = new ArrayList<String>();
        // Instancia o adapter que recebe o array com os detalhes
        //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, detalhes);
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
                Log.i(CATEGORIA,"Antes do consulta GET");
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


                TextView txtEstado = (TextView) findViewById(R.id.txtEstado);
                txtEstado.setText("Estado: " + fone.getEstado().toString());
                txtEstado.setVisibility(View.VISIBLE);

                TextView txtPortabilidade = (TextView) findViewById(R.id.txtPortabilidade);
                txtPortabilidade.setText("Portabilidade: " + portabilidade.toString());
                txtPortabilidade.setVisibility(View.VISIBLE);

                ImageView imgOperadora = (ImageView) findViewById(R.id.imgOperadora);


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

                imgOperadora.setVisibility(View.VISIBLE);

                // Finaliza a dialog que estava executando
                if (dialogo.isShowing()) {
                    dialogo.dismiss();
                }
            }
        };
        // Executa a task (AsyncTask) acima com todos os métodos
        task.execute();
    };







    /*
    * Lê a agenda do telefone e passa um Array de números para a classe Http que faz a busca na Web pelos dados da operadora de cada
    * número de cada contato
    *
    *
    *
    *
    *
    *   INATIVO POIS ESTA SENDO UTILIZADO EM UMA ROTINA SEPARADA ATRAVES DE OUTRA VIEW
    *
    * */
    public void buscarTelefones() {

        final ProgressDialog dialogo = new ProgressDialog(this);

        // Instancia uma AsyncTask para executar a pesquisa na web em uma Thread fora da principal.
        AsyncTask<String, Void, JSONArray> task = new AsyncTask<String, Void, JSONArray>() {

            @Override
            protected JSONArray doInBackground(String... strings) {

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
                JSONArray retorno = http.consultaNumeros(jsonObject);

                //JSONArray retorno = null;
                return retorno;
            }


            @Override
            protected void onPreExecute() {
            /*
            * Método chamado antes da execução da Thread
            * Utilizado também para apresentar uma mensagem de que o processamento está sendo executado
            * */
                super.onPreExecute();
                Log.i(CATEGORIA, "onPreExecute");
                dialogo.setMessage("Pesquisando. Aguarde a atualização dos dados. Pode demorar alguns minutos...");
                dialogo.show();
            }

            @Override
            protected void onPostExecute(JSONArray json) {
              /*
              * Método executado a atualização da interface na Thread principal
              * Utilizado para atualizar as views da thread principal
              *
              *
              * Lê a agenda novamente mas agora comparando os contatos com o resultado do http post
              * Traz da web os dados da operadora de toda a lista e apresenta em uma nova tela os dados dos contatos.
              *
              * */
                super.onPostExecute(json);
                Log.i(CATEGORIA , "onPostExecute");

                /*
                * TODO: Implpementar aqui  atualização da agenda com base no JSON recebido da web
                * Ler toda a agenda e ir gravando comparando o número contido no json e o da agenda gravando a imagem se for igual
                * */
                //Imprime o resultado do httppost
                System.out.println("JSON retornado:  " + json);


                // Após obter o retorno dos dados da internet da operados dos números chama activity que trata os dados e mostra na tela
                Intent it = new Intent(BaseOperadora.this, MostraAgenda.class);
                it.putExtra("dados", String.valueOf(json));
                startActivity(it);

                // Finaliza a dialog que estava executando
                if (dialogo.isShowing()) {
                    dialogo.dismiss();
                }




            }
        };
        // Executa a task (AsyncTask) acima com todos os métodos
        task.execute();


    }



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


        // Esconde os campos com os dados de contato para caso for fazer uma nova pesquisa iniba os campos antes te mostrar os dados
        // atualizados
        TextView txtContato = (TextView) findViewById(R.id.txtContato);
        TextView txtEstado = (TextView) findViewById(R.id.txtEstado);
        TextView txtPortabilidade = (TextView) findViewById(R.id.txtPortabilidade);
        ImageView imageOperadora = (ImageView) findViewById(R.id.imgOperadora);

        txtContato.setVisibility(View.INVISIBLE);
        txtEstado.setVisibility(View.INVISIBLE);
        txtPortabilidade.setVisibility(View.INVISIBLE);
        imageOperadora.setVisibility(View.INVISIBLE);

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
            System.out.println("ID " + idContato);

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

            TextView txtNome= (TextView)findViewById(R.id.txtContato);
            txtNome.setVisibility(View.VISIBLE);
            txtNome.setText("Contato: "+nomeContato);
            c.close();
            telefones.close();

        }
    }



    /*
      *
      *  Exibe a notificação. Não está sendo utilizado na app pois utilizamos um método para exibir a agenda na tela e não para
      *  atualização da agenda do android
    */


    protected void criarNotificacao(Context context, CharSequence mensagemBarraStatus, CharSequence titulo, CharSequence mensagem){
        // Cria uma notificação que chama o serviço de atualização da agenda do telefone
        final Intent it = new Intent("SERVICE_1");
        //Intent intent = new Intent(this,ServicoTelefones.class);
        // Array de linhas para passar como texto da mensagem
        String[] lines=new String[]{"Atualize a agenda do telefone.",
                                    "Ao clicar na mensagem todos os contatos",
                                    "serão atualizados.",
                                    "Se não deseja atualizar ignore limpando",
                                    "esta notificação"};
        //lines[0]="Atualize a agenda do telefone.";
        //lines[1]="Ao clicar na mensagem todos os contatos serão atualizados.";
        //lines[2]="Se não deseja atualizar ignore limpando esta notificação";

        NotificationUtil.create(this,mensagemBarraStatus,titulo,mensagem,lines,R.drawable.ic_action_action_about,R.drawable.ic_action_action_about,it);




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