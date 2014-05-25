package com.android.qualoperadora01.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;

/**
 *  Activity responsável pelos componentes visuais da tela de consulta.
 *  Esta activity não pode ser instanciada pois é uma classe abstrata, portanto todos os processamentos que envolvem atualizações de dados deverão ser feitos em
 *  activitys separadas como foi feita por exemplo a classe DownloadJSON que extende esta classe e fas os processamentos.
 *
 *
 * Created by Ricardo on 24/05/2014.
 */
public abstract class BaseOperadora extends Activity {


    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Button btBuscar = (Button) findViewById(R.id.btBuscar);
    final EditText txtTelefone = (EditText) findViewById(R.id.txtFone);

        /**
         * Created by Dimitri on 24/05/2014.
         */
    final List<String> nomeContatosList = new ArrayList<String>();
    final List<String> foneContatosList = new ArrayList<String>();
    nomeContatosList.add(""); // adiciona um campo vazio para caso o usuario nao queira informar um contato
    foneContatosList.add(""); // adiciona um campo vazio para caso o usuario nao queira informar um contato
    buscaContatos("nome", nomeContatosList);
    buscaContatos("fone", foneContatosList);

    final Spinner spinnerContatos = (Spinner) findViewById(R.id.spinnerContatos);

    Uri uri = Uri.parse("content://com.android.contacts/contacts/");
    Intent preencheSpinner = new Intent(Intent.ACTION_PICK, uri);
    ArrayAdapter adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nomeContatosList);
    spinnerContatos.setAdapter(adaptador);

    spinnerContatos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView parent, View v, int posicao, long id) {
            txtTelefone.setText(foneContatosList.get(posicao));

                /*
                Dá pau no app quando executa o botão com o número que vem do contato porque ele está com mascara.
                Tratar para aceitar assim (retirar máscara?)

                if (!spinnerContatos.getSelectedItem().toString().equals("")) {
                    btBuscar.performClick();
                }*/
        }

        @Override
        public void onNothingSelected(AdapterView parent) {

        }
    });



/**
 * Created by Ricardo on 24/05/2014.
 */
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
                    showMessage("O telefone deve ser informado!");
                    return;
                } else {
                    String telefone = txtTelefone.getText().toString();
                    buscarDados(telefone);
                }
            } else {
                showMessage("Verifique sua conexão com a internet!");
                return;
            }
        }

    });
}

/*
        Button btLigar  = (Button) findViewById(R.id.btLigar);
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

    protected void buscaContatos(String tipo, List<String> lista) {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()) {
            if (tipo == "nome" ) {
                lista.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            }
            else {
                lista.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
        }

        String[] contatos;
        contatos = new String[lista.size()];
        lista.toArray(contatos);
    }

    

    /*Método abstrato que as subclasses devem implementar como quiserem
      Esta classe apenas define a tela com o formulário para busca dos dados
    */
    protected abstract void buscarDados (String telefone);


    private void showMessage(String mensagem){
        AlertDialog.Builder msg = new AlertDialog.Builder(BaseOperadora.this);
        msg.setIcon(R.drawable.warning);
        msg.setTitle("Operadora");
        msg.setMessage(mensagem);
        msg.setNeutralButton("OK", null);
        msg.show();
    }
}