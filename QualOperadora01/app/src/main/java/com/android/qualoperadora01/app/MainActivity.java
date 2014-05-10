package com.android.qualoperadora01.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


/*
*  Activity Main
*
*  Abre Tela principal do sistema e quando o usuário informar o númeroa de telefone então faz uma chamada a classe DownloadJSON.java
*  Esta chamada vai ler um JSON na web e retornar os dados de operadora conforme o número informado.
*
* */

public class MainActivity extends ActionBarActivity {

    Telefone fone = new Telefone();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btBuscar = (Button) findViewById(R.id.btBuscar);
        final EditText txtTelefone = (EditText) findViewById(R.id.txtFone);
        btBuscar.setOnClickListener(new View.OnClickListener() {



        // Recebem valores dinamicamente em tempo de execução
        TextView txtUF = (TextView) findViewById(R.id.txtUF);
        TextView txtPortabilidade = (TextView) findViewById(R.id.txtPortabilidade);
        ImageView imgOperadora = (ImageView) findViewById(R.id.imageView);



        // Texto estático, estão sendo declaradas para manipular a visibilidade
        TextView textUF = (TextView) findViewById(R.id.textUF);
        TextView textPortabilidade = (TextView) findViewById(R.id.textPortabilidade);


                                        @Override
                                        public void onClick(View view) {


                                            if (txtTelefone.getText().toString().equals("")) {
/*
* TODO: Implementar mensagem de erro.
 */
                                                return;
                                            } else try {
                                                String telefone = txtTelefone.getText().toString();

                                                // Instancia a classe para download dos dados da web retornando um JSON
                                                String result = new DownloadJSON().execute(telefone).get();
                                                // Instancia um objeto JSON com base no resultado obtido
                                                JSONObject json = new JSONObject(result);
                                                // Seta os dados na classe telefone
                                                fone.setNumero(txtTelefone.getText().toString());
                                                fone.setOperadora(json.getString("operadora"));
                                                fone.setEstado(json.getString("estado"));
                                                fone.setPortabilidade(Boolean.parseBoolean(json.getString("portabilidade")));


                                                txtUF.setText(fone.getEstado());



                                                if (fone.getPortabilidade()) {
                                                    txtPortabilidade.setText("Sim");
                                                } else {
                                                    txtPortabilidade.setText("Não");
                                                }

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

                                            } catch (JSONException e) {
                                                Log.e("Erro Json", "Erro no parsing JSON");
                                                e.printStackTrace();
                                                return;
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();

                                            } catch (ExecutionException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                     });



        Button btLigar = (Button) findViewById(R.id.btLigar);
        btLigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Representa o endereço que desejamos abrir
                Uri uri = Uri.parse("tel:"+txtTelefone.getText().toString());
                //Cria a Intent com o endereço que fará a ligação
                Intent i = new Intent(Intent.ACTION_CALL, uri);
                //Envia a mensagem para o sistema operacional
                startActivity(i);
            }
        });

    }

}
