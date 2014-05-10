package com.android.qualoperadora01.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.apache.http.protocol.HTTP.*;
import static org.apache.http.protocol.HTTP.UTF_8;

/**
 * Created by Ricardo on 08/05/2014.
 */
public class DownloadJSON extends AsyncTask<String, Void, String> {
    private static String URL = "http://private-61fc-rodrigoknascimento.apiary-mock.com/consulta/";

    @Override
    protected String doInBackground(String... strings) {

        String result;
        HttpClient httpCliente = new DefaultHttpClient();
        //httpCliente.getParams().setParameter(strings[0], "telefone");

        try {
            HttpGet httpGet = new HttpGet(URL+strings[0]);
            //httpPost.setHeader("Content-Type", "text/plain; charset=utf-8");
            Log.i("Leu URL:",URL);
            HttpResponse response = httpCliente.execute(httpGet);
            Log.i("Exec: ","Executou response..");
            HttpEntity entity = response.getEntity();
            //result = entity.getContent().toString();

            if (entity != null) {
                InputStream instream = entity.getContent();
                result = toString(instream);
                instream.close();
                //result = entity.getContent().toString();
                Log.i("URL:",URL);
                Log.i("Fone informado:  ", strings[0]);
                Log.i("Retorno conexão:  ", result);
                return result;
            }



        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            Log.e("Erro http", "Falha ao acessar Web Service");
        }
        return null;

}


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    private String toString (InputStream is)
            throws IOException {

        Log.i("Str ", "Entrou");
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();
        int lidos;
        while ((lidos = is.read(bytes)) > 0) {
            baos.write(bytes, 0, lidos);
        }
        return new String(baos.toByteArray());
    }


}
