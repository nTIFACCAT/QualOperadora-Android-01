package com.android.qualoperadora01.app;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
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



    // Método que faz a um POST e passando um Array de números e retorna os dados da URL de todos os números em um JsonArray
    public JSONArray consultaNumeros(JSONObject telefones) {
/*
        try {

            Log.i(CATEGORIA,"Recebeu telefones: "+telefones);

            Log.i(CATEGORIA, "Instancia HttpPost");
            HttpPost request = new HttpPost(url);
            request.setHeader("Accept","application/json");
            request.setHeader("Content-type", "application/json");
            Log.i(CATEGORIA, "Instancia String Entity");
            StringEntity entity = new StringEntity(telefones.toString());
            System.out.println("Telefones to string: "+telefones.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            Log.i(CATEGORIA, "Request entity");
            request.setEntity(entity);
            Log.i(CATEGORIA,"Instancia httpclient");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            Log.i(CATEGORIA,"Instancia response");
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();
            Log.i(CATEGORIA,"Executou Post");
            Log.i(CATEGORIA, String.valueOf(response));
            Log.i(CATEGORIA," Get  entity.");
            if (entity != null) {
                Log.i(CATEGORIA," Entrou no if ");
                //InputStream in = response.getEntity().getContent();
                //InputStream in = entity.getContent();
                //String result = EntityUtils.toString(entity);
                InputStream in = responseEntity.getContent();
                String result = readString(in);
                Log.i(CATEGORIA, "Print de Result: "+result);
                JSONArray jsonArray = new JSONArray(result);
                return jsonArray;

           }
*/


        try{

            //Cria a URL
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            //Configura a requisição para POST
            conn.setRequestMethod("POST");
            //conn.setDoInput(false);

            // Output - saída
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Content", String.valueOf(telefones));
//            conn.setRequestProperty("charset", "utf-8");
            conn.connect();
            OutputStream out = conn.getOutputStream();
            System.out.println("Output : "+out);
            String params = String.valueOf(telefones);
            System.out.println("PARAMS : "+params);
            byte[] bytes = params.getBytes("UTF8");
            System.out.println("BYTES: "+bytes);
            out.write(bytes);
            //out.flush();
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

        }catch (MalformedURLException e){
            Log.e(CATEGORIA, e.getMessage(),e);
        }catch (IOException e){
            Log.e(CATEGORIA, e.getMessage(),e);
        } catch (JSONException e) {
            Log.e(CATEGORIA, e.getMessage(),e);
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
