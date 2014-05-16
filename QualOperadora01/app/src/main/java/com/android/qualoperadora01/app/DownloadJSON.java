package com.android.qualoperadora01.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

/**
 /* Classe que faz a busca dos dados na web no JSON.
 Esta extende uma AsyncTask pois esta busca não pode ser feita pela Thread principal da activity.
 Está sendo feita dentro da mesma activity visto que cada activity corresponde a uma tela da aplicação e também porque tempos que utilizar os métodos

 protected void onPreExecute e protected void onPostExecute que utilizam as preogress dialogs que avisam o usuário do andamento das atividades

 */
public class DownloadJSON extends AsyncTask<String, Void, String> {

        private Activity activity;
        private String URL = "http://private-61fc-rodrigoknascimento.apiary-mock.com/consulta/";
        ProgressDialog dialogo = new ProgressDialog(this.activity);


    public DownloadJSON (Activity activity){
        this.activity = activity;

    }

        @Override
        protected String doInBackground(String... strings) {

            String result;
            HttpClient httpCliente = new DefaultHttpClient();
            //httpCliente.getParams().setParameter(strings[0], "telefone");

            try {
                HttpGet httpGet = new HttpGet(URL + strings[0]);
                //httpPost.setHeader("Content-Type", "text/plain; charset=utf-8");
                Log.i("Leu URL:", URL);
                HttpResponse response = httpCliente.execute(httpGet);
                Log.i("Exec: ", "Executou response..");
                HttpEntity entity = response.getEntity();
                //result = entity.getContent().toString();

                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = toString(instream);
                    instream.close();
                    //result = entity.getContent().toString();
                    Log.i("URL:", URL);
                    Log.i("Fone informado:  ", strings[0]);
                    Log.i("Retorno conexão:  ", result);
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

            AlertDialog.Builder msg = new AlertDialog.Builder(this.activity);
            msg.setIcon(R.drawable.warning);
            msg.setTitle("Operadora");
            msg.setMessage(mensagem);
            msg.setNeutralButton("OK", null);
            msg.show();
        }

}
