package com.android.qualoperadora01.app;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;


/**
 /* Classe que faz a busca dos dados na web no JSON.
 Esta extende a activity BaseOperadora, que é acivity abstract que é criada para servir de base da tela que depois pode ser manipulada por qualquer activity
 que a extender .
 Utiliza uma AsyncTask pois esta busca não pode ser feita pela Thread principal da activity.
  protected void onPreExecute e protected void onPostExecute que utilizam as preogress dialogs que avisam o usuário do andamento das atividades

 */
public class DownloadJSON extends BaseOperadora {

    @Override
    protected void buscarDados(final String telefone, final String nome) {

        // Instancia a ListView para visualizar os detalhes
        final ListView lista = (ListView) findViewById(R.id.listaDetalhes);
        // Cria uma string para receber os dados utilizados no array declarado logo abaixo.
        final ArrayList<String> detalhes = new ArrayList<String>();
        // Instancia o adapter que recebe o array com os detalhes
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(DownloadJSON.this, android.R.layout.simple_list_item_1, detalhes);
        // Classe telefone que receberá os valores

        final Telefone fone = new Telefone();
        fone.setNumero(telefone);

        //URL de pesquisa mais o número da pessoa para fazer o get
        final String url = "http://qualoperadora.herokuapp.com/consulta/"+fone.getNumero();

        final ProgressDialog dialogo = new ProgressDialog(DownloadJSON.this);

       // Instancia uma AsyncTask para executar a pesquisa na web.
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {

           @Override
            protected String doInBackground(String... strings) {
                /*
                * Método que executa automaticamente em uma thread
                * Faz o processamento em Background
                * */

               //Chama a classe que faz executa o http na url
               String result = Http.getInstance(Http.NORMAL).downloadArquivo(url);
               try {

                   if (result != null) {

                       try {
                           // Instancia um objeto JSON com base no resultado obtido
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
                detalhes.add(new String("Nome: "+nome));

                // Seta os valores na lista
                lista.setAdapter(adapter);
                ImageView imgOperadora = (ImageView) findViewById(R.id.imageView);

                if (fone.operadora.equals("Vivo - Celular")) {
                    imgOperadora.setImageResource(R.drawable.vivo);
                } else if (fone.operadora.equals("TIM - Celular")) {
                    imgOperadora.setImageResource(R.drawable.tim);
                } else if (fone.operadora.equals("Claro - Celular")) {
                    imgOperadora.setImageResource(R.drawable.claro);
                } else if (fone.operadora.equals("Oi - Celular")||fone.operadora.equals("Oi - Fixo")) {
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
}

}
