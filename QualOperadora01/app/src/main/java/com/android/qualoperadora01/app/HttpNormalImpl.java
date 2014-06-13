package com.android.qualoperadora01.app;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by RICARDO on 12/06/2014.
 * Implementação de um método HttpGet para busca de dados conforme a URL passada
 *
 */
public class HttpNormalImpl extends Http {
    private final String CATEGORIA="livro";

    @Override
    public String downloadArquivo(String url) {
        Log.i(CATEGORIA,"Http.downloadArquivo: "+url);

        try {
            //Cria a URL
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            //Configura a requisição para GET
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.connect();
            InputStream in = conn.getInputStream();
            String arquivo = readString(in);
            conn.disconnect();
            return arquivo;

        }catch (MalformedURLException e){
            Log.e(CATEGORIA, e.getMessage(),e);
        }catch (IOException e){
            Log.e(CATEGORIA, e.getMessage(),e);
        }
        return null;
    }

    @Override
    public byte[] downloadImagem(String url) {
        return new byte[0];
    }

    @Override
    public String doPost(String url, Map map) {
        return null;
    }

    //Faz a leitura do texto da InputStream retornada
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
