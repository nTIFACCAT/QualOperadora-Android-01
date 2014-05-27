package com.android.qualoperadora01.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 /* Classe que faz a busca dos dados na web no JSON.
 Esta extende a activity BaseOperadora, que é acivity abstract que é criada para servir de base da tela que depois pode ser manipulada por qualquer activity
 que a extender .
 Utiliza uma AsyncTask pois esta busca não pode ser feita pela Thread principal da activity.
  protected void onPreExecute e protected void onPostExecute que utilizam as preogress dialogs que avisam o usuário do andamento das atividades

 */
public class DownloadJSON extends BaseOperadora {


    @Override
    protected void buscarDados(String telefone) {

        // Instancia a ListView para visualizar os detalhes
        final ListView lista = (ListView) findViewById(R.id.listaDetalhes);
        // Cria uma string para receber os dados utilizados no array declarado logo abaixo.
        final ArrayList<String> detalhes = new ArrayList<String>();
        // Instancia o adapter que recebe o array com os detalhes
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(DownloadJSON.this, android.R.layout.simple_list_item_1, detalhes);
        // Classe telefone que receberá os valores

        final Telefone fone = new Telefone();
        fone.setNumero(telefone);

        final String URL = "http://qualoperadora.herokuapp.com/consulta/";
        //final String URL = "http://private-61fc-rodrigoknascimento.apiary-mock.com/consulta/";

        final ProgressDialog dialogo = new ProgressDialog(DownloadJSON.this);


        // Instancia uma AsyncTask para executar a pesquisa na web.
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {

           @Override
            protected String doInBackground(String... strings) {
                /*
                * Método que executa automaticamente em uma thread
                * Faz o processamento em Background
                * */
                Log.i("Info: ", "Entrou na Async");
                Log.i("fone: ", fone.getNumero());
                String result = null;
                HttpClient httpCliente = new DefaultHttpClient();

                try {

                    HttpGet httpGet = new HttpGet(URL+fone.getNumero());
                    Log.i("Leu URL:", URL);
                    HttpResponse response = httpCliente.execute(httpGet);
                    Log.i("Exec: ", "Executou response..");
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {

                        result = EntityUtils.toString(entity);

                        Log.i("URL:", URL);
                        Log.i("Fone informado:  ", fone.getNumero());

                        try {
                            // Instancia um objeto JSON com base no resultado obtido
                            Log.i("RESULT depois de entity", result);
                            JSONObject json = new JSONObject(result);

                            // Seta os dados na classe telefone
                            fone.setOperadora(json.getString("operadora"));
                            fone.setEstado(json.getString("estado"));
                            fone.setPortabilidade(Boolean.parseBoolean(json.getString("portabilidade")));

                        } catch (JSONException e) {
                            Log.e("Erro Json", "Erro no parsing JSON");
                            e.printStackTrace();
                        }
                        return result;
                    }

                } catch (ClientProtocolException e) {
                    // showMessage("Erro ao acessar. Verifique a conexão. Erro: "+e.getMessage());
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    //showMessage("Problemas na execução. Verifique a conexão. Erro: " + e.getMessage());
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    //showMessage("Problemas no Host. Verifique a conexão. Erro: " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e("Erro http", "Falha ao acessar Web Service");
                }

                return result;
            }


            @Override
            protected void onPreExecute() {
            /*
            * Método chamado antes da execução da Thread
            * Utilizado também para apresentar uma mensagem de que o processamento está sendo executado
            * */
                super.onPreExecute();
                Log.i("Task:  ", "onPreExecute");
                dialogo.setMessage("Pesquisando. Aguarde...");
                dialogo.show();
            }

            @Override
            protected void onPostExecute(String s) {
              /*
              * Método executado automaticamente na Thread principal
              * Utilizado para atualizar as views da thread principal
              *
              * */

                super.onPostExecute(s);

                /* Atualiza a view da tela principal*/
                Log.i("Retorno PostExecute: ", s);
                // Define valores da ListaDetalhes (ListView com duas linhas) que trará na tela os valores
                // Estado e Portabilidade
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

                //Log.i("Detalhes : ", "Portabilidade:"+ detalhes[0][1]+" Estado: "+ detalhes[0][0]);

                Log.i("Retorno conexão:  ", "Retornou dados corretos: ");
                Log.i("Número:  ", fone.getNumero());
                Log.i("Operadora:  ", fone.getOperadora());
                Log.i("Estado:  ", fone.getEstado());
                Log.i("Portablidade:  ", portabilidade);

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
                // Finaliza a dialog que estava executando
                    if (dialogo.isShowing()) {
                        dialogo.dismiss();
                     }
                }

           //Método toString e traz o JSON em forma de string
/*            private String toString(InputStream is)
                    throws IOException {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    byte[] buf = new byte[1024];

                    int lidos;
                    //Log.i("Início Lidos: ", String.valueOf(lidos));
                    // Read lê um byte da inputStream
                    while ((lidos = is.read(buf)) > 0) {
                        Log.i("LEU: ", String.valueOf(lidos));
                        baos.write(buf, 0, lidos);
                    }
                    byte[] bytes = baos.toByteArray();

                    Log.i("String de Bytes", String.valueOf(bytes));
                    String result = new String(bytes);
                    return result;

                }finally {
                    baos.close();
                }
            }
*/
        };

        // Executa a task (AsyncTask) acima com todos os métodos
        task.execute();
}

}
