package com.android.qualoperadora01.app;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by RICARDO on 06/07/2014.
 */
public class ServicoTelefones extends Service implements Runnable {
    private static final String CATEGORIA= "Service Operadora";
    private boolean ativo;

    @Override
    public IBinder onBind(Intent intent) {
        //sem interação com o serviço
        return null;
    }

    @Override
    public void onCreate(){
        Log.i(CATEGORIA,"Servico.onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
        //Método chamado depois do onCreate(), logo depois que o serviço é iniciado
        //O parâmetro <StartId> representa o identificador deste serviço
        ativo=true;
        //Delega parra auma Thread (passamos o nome no construtor para visuaalizar no debug)
        new Thread(this,"Servico-"+startId).start();
        Log.i(CATEGORIA, "Iniciou: "+"Servico-"+startId);
        //Chama a implementação da classe mãe
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void run() {
            buscaTelefones();
            Log.i(CATEGORIA, "ExemploServico fim");
            //Auto encerra o serviço se o contador chegou a 10
            stopSelf();

    }

    private void buscaTelefones(){

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
        System.out.println("Retorno JsonArray:  "+retorno);


    }

    @Override
    public void onDestroy(){
        // Ao encerrar o serviço, altera o flag oara a thread oara (isto é importante para encerrar a thread caso alguém tenha chamado
        // o stopService(intent))
        ativo=false;
        Log.i(CATEGORIA, "Servico.onDestroy()");

    }

}
