package com.android.qualoperadora01.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;


/**
 *  Activity responsável pelos componentes visuais da tela de consulta.
 *  Esta activity não pode ser instanciada pois é uma classe abstrata, portanto todos os processamentos que envolvem atualizações de dados deverão ser feitos em
 *  activitys separadas como foi feita por exemplo a classe DownloadJSON que extende esta classe e fas os processamentos.
 *
 *
 * Created by Ricardo on 24/05/2014.
 */
public abstract class BaseOperadora extends Activity {
    // Constante para identificar a sub-activity lançada pelo botão agenda
    private static final int SELECIONAR_CONTATO = 1;
    private static final int GRAVAR_CONTATO = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btBuscar = (Button) findViewById(R.id.btBuscar);
        final EditText txtTelefone = (EditText) findViewById(R.id.txtFone);
        ActionBar actionBar = getActionBar();
        //actionBar.hide();


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        EditText telefone = (EditText) findViewById(R.id.txtFone);

        switch (item.getItemId()){
            case R.id.action_ligar :
                Ligar(telefone.getText().toString());
                return true;
            case R.id.action_agenda :
                Agenda();
                return true;
            case R.id.action_gravar :
                Gravar(telefone.getText().toString());
                return true;
            default :
                return  super . onOptionsItemSelected ( item );

        }
    }



    /**
     * Created by Ricardo on 26/05/2014.
     */

        /**
         *
         * Created by Dimitri on 24/05/2014.
         * Desabilitado - Trocamos por uma alert dialog que pode selecionar o número caso tenha mais de um
         *

        spinnerNumeros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View v, int posicao, long id) {

                tFone.setText(numeros.get(posicao));
                /*
                Dá pau no app quando executa o botão com o número que vem do contato porque ele está com mascara.
                Tratar para aceitar assim (retirar máscara?)

                if (!spinnerContatos.getSelectedItem().toString().equals("")) {
                    btBuscar.performClick();
                }

            }
            @Override
            public void onNothingSelected(AdapterView parent) {

            }



        });

         */


    /**
     * Created by Dimitri on 24/05/2014.
     *
     *
     * Desabilidado momentaneamente - Vai ser utilizado na próxima funcionalidade do sistema
     *




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
     */


    /**
     * Created by Ricardo on 24/05/2014.
     */
    /*Método abstrato que as subclasses devem implementar como quiserem
      Esta classe apenas define a tela com o formulário para busca dos dados
    */
    protected abstract void buscarDados (String telefone);


    // Método que recebe um número e faz a ligação
    protected void Ligar(String numero){


        if (numero.toString().equals("")) {
            showMessage("O telefone deve ser informado!");
            return;
        } else{
            //Representa o endereço que desejamos abrir
            String tel = "tel:" + numero.toString();
            Uri uri = Uri.parse(tel);
            //Cria a Intent com o endereço que fará a ligação
            Intent i = new Intent(Intent.ACTION_CALL, uri);
            //Envia a mensagem para o sistema operacional
            startActivity(i);
        }
    }

    // Método que chama a agenda do telefone
    protected void Agenda (){
        // Visualiza a lista de contatos
        Uri uri = Uri.parse("content://com.android.contacts/contacts/");
        Intent i = new Intent(Intent.ACTION_PICK, uri);
        startActivityForResult(i, SELECIONAR_CONTATO);
    }

    protected void Gravar(String numero) {
        // Pega telefone do campo e grava como contato do telefone
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, "Novo Contato");
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, numero);

        startActivity(intent);
    }


    // Método que recebe o retorno da intent chamada
    protected void onActivityResult(int requestCode, int resulTCode, Intent intent){
        // Recebe uma intent de retorno o qual podemos recuperar os dados, ou seja.
        // Ao retornar da intent chamada o Android traz uma nova intent como retorno e então podemos manipular os dados de retorno

        // Text que recebe o número selecionado, representa a txtFone do layout da activity_main.xml
        final EditText tFone = (EditText) findViewById(R.id.txtFone);

        //Uri que identifica o contato, resultado do click no contato da agenda do android. Trazido pela intent de retorno
        Uri uri = intent.getData();
       /*Busca o contato no banco de dados do telefone utilizando a Uri do contato selecionado
        O cursor c contém os dados do contato selecionado
        */
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        c.moveToNext();

        /*
        * O contato não possui todos os dados necessários referentes aos números , pois são guardados em tabelas diferentes
        *  Então pega-se o idContato da Uri atual para servir de filtro para pesquisa no banco de dados que contém os demais dados
        * */

        long idContato = ContentUris.parseId(uri);
        String nomeContato = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

        /*
         *  Novo cursor para buscar os demais dados do contato. O primeiro parâmentro, obrigatório, é a URI onde constao os dados dos contatos, o terceiro é o parâmetro de filtro,
         *  Funciona como a cláusula Where de uma consulta SQL
         */

        //Array com os número do contato
        final ArrayList<String> numeros = new ArrayList<String>();
        // Adapter para a spinner dos números
        ArrayAdapter<String> adapterNumeros = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,numeros);

        Cursor telefones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+idContato, null, null);
        //Posiciona o cursor
        telefones.moveToFirst();
        //Recupera os números do contato se o resultado da pesquisa for maior que zero
        if (telefones.getCount()>0){
            Log.i("Telefones Count ", String.valueOf(telefones.getCount()));
            for (int i=0;i<telefones.getCount();i++){
                //Preenche o array numeros com os números do contato
                numeros.add(telefones.getString(telefones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                telefones.moveToNext();
            }

            /*
            * Caso o contato tenha mais que um número então exibe uma mensagem de alerta com o array de números,
            * senão seta no campo de pesquisa o único número do contato
            */

            if (numeros.size()>1){
                tFone.setText(numeros.get(0));
                final AlertDialog.Builder alertaNumeros = new AlertDialog.Builder(this);
                alertaNumeros.setTitle("Contato: "+nomeContato);
                alertaNumeros.setPositiveButton("Ok", null);
                alertaNumeros.setAdapter(adapterNumeros, null);
                alertaNumeros.setSingleChoiceItems(adapterNumeros,0,new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tFone.setText(numeros.get(i));
                    }
                });
                alertaNumeros.show();
            }else{
                tFone.setText(numeros.get(0));
            }
        }else {
            AlertDialog.Builder msg = new AlertDialog.Builder(BaseOperadora.this);
            msg.setIcon(R.drawable.warning);
            msg.setTitle("Operadora");
            msg.setMessage("Não existe número cadastrado. ");
            msg.setNeutralButton("OK", null);
            msg.show();

            tFone.setText("");
        }

        c.close();
        telefones.close();

    }

    private void showMessage(String mensagem){
        AlertDialog.Builder msg = new AlertDialog.Builder(BaseOperadora.this);
        msg.setIcon(R.drawable.warning);
        msg.setTitle("Operadora");
        msg.setMessage(mensagem);
        msg.setNeutralButton("OK", null);
        msg.show();
    }
}