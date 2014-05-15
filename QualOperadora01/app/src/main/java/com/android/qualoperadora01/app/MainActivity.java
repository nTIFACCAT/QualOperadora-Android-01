package com.android.qualoperadora01.app;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse; // Obtenção da resposta
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;


/*
*  Activity Main
*
*  Abre Tela principal do sistema e quando o usuário informar o númeroa de telefone então faz uma chamada a classe DownloadJSON.java
*  Esta chamada vai ler um JSON na web e retornar os dados de operadora conforme o número informado.
*
* */

public class MainActivity extends ActionBarActivity {


    Telefone fone = new Telefone();
    @Override
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

                    if (txtTelefone.getText().toString().equals("")) {

                        AlertDialog.Builder msg = new AlertDialog.Builder(MainActivity.this);
                        msg.setIcon(R.drawable.warning);
                        msg.setTitle("Operadora");
                        msg.setMessage("O telefone deve ser informado!");
                        msg.setNeutralButton("OK", null);
                        msg.show();
                        return;
                    } else {
                        try {
                            String telefone = txtTelefone.getText().toString();
                            // Instancia a classe para download dos dados da web retornando um JSON
                            //String result = new DownloadJSON().execute(telefone).get();
                            //DownloadJSON download = new DownloadJSON(MainActivity.this);
                            Log.i("MSG", "Antes de chamar download...");
                            String result = new buscaDadosJSON().execute(telefone).get();


                            // Instancia um objeto JSON com base no resultado obtido
                            JSONObject json = new JSONObject(result);
                            // Seta os dados na classe telefone
                            fone.setNumero(txtTelefone.getText().toString());
                            fone.setOperadora(json.getString("operadora"));
                            fone.setEstado(json.getString("estado"));
                            fone.setPortabilidade(Boolean.parseBoolean(json.getString("portabilidade")));

                            // Instancia uma string para receber o valor booleano de portabilidae da classe java
                            String portabilidade = new String();

                            if (fone.getPortabilidade()) {
                                portabilidade = "Sim";
                            } else {
                                portabilidade = "Não";
                            }

                            // Define valores da ListaDetalhes (ListView com duas linhas) que trará na tela os valores
                            // Estado e Portabilidade
                            String[] detalhes = new String[]{
                                    "Estado: " + fone.getEstado(),
                                    "Portabilidade: " + portabilidade
                            };

                            // Instancia a ListView para visualizar os detalhes
                            ListView lista = (ListView) findViewById(R.id.listaDetalhes);
                            // Instancia o adapter que recebe o array com os detalhes
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, detalhes);
                            // Seta os valores na lista
                            lista.setAdapter(adapter);

                            ImageView imgOperadora = (ImageView) findViewById(R.id.imageView);

                            if (fone.operadora.equals("Vivo - Celular")) {
                                imgOperadora.setImageResource(R.drawable.vivo);
                            } else if (fone.operadora.equals("Tim")) {
                                imgOperadora.setImageResource(R.drawable.tim);
                            } else if (fone.operadora.equals("Claro")) {
                                imgOperadora.setImageResource(R.drawable.claro);
                            } else if (fone.operadora.equals("Oi")) {
                                imgOperadora.setImageResource(R.drawable.oi);
                            } else {
                                imgOperadora.setImageResource(R.drawable.warning);
                            }

                        } catch (JSONException e) {
                            Log.e("Erro Json", "Erro no parsing JSON");
                            e.printStackTrace();
                            return;
                        } catch (InterruptedException e) {
                            e.printStackTrace();

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                } else {

                    AlertDialog.Builder msg = new AlertDialog.Builder(MainActivity.this);
                    msg.setIcon(R.drawable.warning);
                    msg.setTitle("Operadora");
                    msg.setMessage("Verifique sua conexão com a internet!");
                    msg.setNeutralButton("OK", null);
                    msg.show();
                    return;
                }

            }

        });


    }
/*



        TODO: Criar botão no layout main.xml

/*
        Button btLigar = (Button) findViewById(R.id.btLigar);
        btLigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txtTelefone.getText().toString().equals("")) {

                    AlertDialog.Builder msg = new AlertDialog.Builder(MainActivity.this);
                    msg.setIcon(R.drawable.warning);
                    msg.setTitle("Operadora");
                    msg.setMessage("O telefone deve ser informado!");
                    msg.setNeutralButton("OK", null);
                    msg.show();
                    return;
                } else{

                    //Representa o endereço que desejamos abrir
                    String tel = "tel:" + txtTelefone.getText().toString();
                    Uri uri = Uri.parse(tel);
                    //Cria a Intent com o endereço que fará a ligação
                    Intent i = new Intent(Intent.ACTION_CALL, uri);
                    //Envia a mensagem para o sistema operacional
                    startActivity(i);
            }
            }
        });



    }



*/

/* Classe privada que faz a busca dos dados na web no JSON.
   Esta extende uma AsyncTask pois esta busca não pode ser feita pela Thread principal da activity.
   Está sendo feita dentro da mesma activity visto que cada activity corresponde a uma tela da aplicação e também porque tempos que utilizar os métodos

   protected void onPreExecute e protected void onPostExecute que utilizam as preogress dialogs que avisam o usuário do andamento das atividades

*/


private class buscaDadosJSON extends AsyncTask<String, Void, String> {
    private String URL = "http://private-61fc-rodrigoknascimento.apiary-mock.com/consulta/";
    ProgressDialog dialogo = new ProgressDialog(MainActivity.this);


    @Override
    protected String doInBackground(String... strings) {
        String result;

        //Instancia um cliente http para posterior execução após passagem da url
        HttpClient httpCliente = new DefaultHttpClient();

        try {
            // Instancia um get e passa a url + a string com o número passado como parâmetro
            HttpGet httpGet = new HttpGet(URL + strings[0]);
            Log.i("Leu URL:", URL);
            // Retorno da chamada do Get
            HttpResponse response = httpCliente.execute(httpGet);
            Log.i("Exec: ", "Executou response..");
            // Pega a entity do retorno (dados)
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // InputStrean recebe dados de retorno
                InputStream instream = entity.getContent();
                // Transforma a stream em string
                result = toString(instream);
                instream.close();
                //result = entity.getContent().toString();
                Log.i("URL:", URL);
                Log.i("Fone informado:  ", strings[0]);
                Log.i("Retorno conexão:  ", result);
                return result;
            }

        } catch (ClientProtocolException e) {
            showMessage("Erro ao acessar. Verifique a conexão. Erro: "+e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            showMessage("Problemas na execução. Verifique a conexão. Erro: " + e.getMessage());
            e.printStackTrace();
        } catch (UnknownHostException e) {
            showMessage("Problemas no Host. Verifique a conexão. Erro: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Erro http", "Falha ao acessar Web Service");
        }
        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialogo.setMessage("Pesquisando. Aguarde...");
        dialogo.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (this.dialogo.isShowing()) {
            dialogo.dismiss();
        }
    }

    // Sobrescreve o método toString e traz o JSON em forma de string
    private String toString(InputStream is)
            throws IOException {

        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();
        int lidos;
        while ((lidos = is.read(bytes)) > 0) {
            baos.write(bytes, 0, lidos);
        }
        return new String(baos.toByteArray());
    }

    private void showMessage(String mensagem){

        AlertDialog.Builder msg = new AlertDialog.Builder(MainActivity.this);
        msg.setIcon(R.drawable.warning);
        msg.setTitle("Operadora");
        msg.setMessage(mensagem);
        msg.setNeutralButton("OK", null);
        msg.show();
    }
}


}
