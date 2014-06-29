package com.android.qualoperadora01.app;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Implementação dos métodos que fazem as operações na Web, GET e POST
 *
 */
public class Http{
    private final String CATEGORIA="HTTP";// Para uso de log para ver onde está imprimindo as mensagens
    final String url = "http://qualoperadora.herokuapp.com/consulta/";


    //Método que faz o GET na utl conforme o número
    public JSONObject consultaNumero(String numero){

        try {
            //Cria a URL
            URL u = new URL(url+numero);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            //Configura a requisição para GET
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.connect();
            InputStream in = conn.getInputStream();
            String result = readString(in);
            conn.disconnect();
            JSONObject json = new JSONObject(result);
            return json;

        }catch (MalformedURLException e){
            Log.e(CATEGORIA, e.getMessage(),e);
        }catch (IOException e){
            Log.e(CATEGORIA, e.getMessage(),e);
        } catch (JSONException e) {
          e.printStackTrace();
            Log.e(CATEGORIA, e.getMessage(),e);
        }

        return null;
    }


    // Método que faz a um POST e passando um Array de números e retorna os dados da URL de todos os números
    public JSONArray consultaNumeros(JSONObject telefones) {


        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        try {
            HttpPost post = new HttpPost(url);
            StringEntity se = new StringEntity(telefones.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
            response = client.execute(post);

            if (response != null) {
                InputStream in = response.getEntity().getContent();
                String result = readString(in);
                JSONArray jsonArray = new JSONArray(result);
                return jsonArray;

            }

/*

        try{

            //Cria a URL

            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            //Configura a requisição para POST
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("charset", "utf-8");
            conn.connect();
            OutputStream out = conn.getOutputStream();
            String params = String.valueOf(telefones);
            System.out.println("PARAMS : "+params);
            byte[] bytes = params.getBytes("UTF8");
            System.out.println("BYTES: "+bytes);
            out.write(bytes);
            out.flush();
            out.close();
            System.out.println("out.close realizado.");
            int totalSize = conn.getContentLength();
            System.out.println("totalSize = "+totalSize);
            InputStream in = conn.getInputStream();
            System.out.println("Antes readString.");
            // Lê o texto
            String result = readString(in);
            System.out.println("Imprimindo Resultado: in. "+result);
            conn.disconnect();
            JSONArray jsonArray = new JSONArray(result);
            return jsonArray;
*/
        }catch (MalformedURLException e){
            Log.e(CATEGORIA, e.getMessage(),e);
        }catch (IOException e){
            Log.e(CATEGORIA, e.getMessage(),e);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    //Faz a leitura do texto da InputStream retornada transformando em String
    private String readString(InputStream in)throws IOException{
        byte[] bytes = readBytes(in);
        String texto = new String(bytes);
        Log.i(CATEGORIA, "Http.readString: "+texto);
        return texto;

    }

    //Faz a leitura do array de bytes da InputStream retornada
    private byte[] readBytes(InputStream in) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer))>0){
                bos.write(buffer,0,len);
            }
            byte[] bytes = bos.toByteArray();
            return bytes;
        }finally {
            bos.close();
            in.close();
        }

    }

}
