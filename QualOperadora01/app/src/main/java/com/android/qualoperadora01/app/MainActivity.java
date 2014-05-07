package com.android.qualoperadora01.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/*
*  Activity Main
*
*  Abre Tela principal do sistema e quando o usuário informar o númeroa de telefone então faz uma chamada a outra atividade que é a "TelefoneJSON" configurada em
*  AndroidManifest.xml
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

                                        @Override
                                        public void onClick(View view) {


                                            if (txtTelefone.getText().toString().equals("")){
/*
* TODO: Implementar mensagem de erro.
 */
                                                return;
                                            }else {


                                                fone.setNumero(txtTelefone.getText().toString());
                                                // Cria uma intent configurada previamente no intent filter
                                                Intent i = new Intent("TelefoneJSON");
                                                // Seta o número como um parâmetro na intent criada
                                                i.putExtra("numero", fone.getNumero());
                                                // starta a activity com a intent levando o número do telefone como parâmetro e o código da activity
                                                startActivityForResult(i, 0);
                                            }

                                        }
                                    }
        );



        Button btLigar = (Button) findViewById(R.id.btligar);
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

    // Retorna o resultado da activity codigo retornado é o código da activity iniciada que ao ser finalizada traz o mesmo código

    protected void onActivityResult(int codigo, int resultado, Intent i){
        // Retorna os resultados da activity (intent) chamada acima trazendo os dados da operadora conforme o telefone
        if (i == null){
            Toast.makeText(this, "Nenhum dado retornado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (codigo==0){
            // Pega da intent retornada os valores setados anteriormente na activity (TelefoneJSON)

            Bundle params = i.getExtras();

            fone.setEstado(params.getString("estado"));
            fone.setPortabilidade(Boolean.parseBoolean(params.getString("portabilidade")));
            fone.setOperadora(params.getString("operadora"));
            ImageView imgOperadora = (ImageView) findViewById(R.id.imageView);
            TextView txtUF = (TextView) findViewById(R.id.txtUF);
            txtUF.setText(fone.getEstado());
            TextView txtPortabilidade = (TextView) findViewById(R.id.txtPortabilidade);

            if (fone.getPortabilidade()){
                txtPortabilidade.setText("Sim");
            }else{
                txtPortabilidade.setText("Não");
            }


           if (fone.operadora.equals("Vivo")){
               imgOperadora.setImageResource(R.drawable.vivo);
           }else if (fone.operadora.equals("Tim")){
                imgOperadora.setImageResource(R.drawable.tim);
            }else if (fone.operadora.equals("Claro")){
               imgOperadora.setImageResource(R.drawable.claro);
           }else if (fone.operadora.equals("Oi")){
               imgOperadora.setImageResource(R.drawable.oi);
           }else{
               imgOperadora.setImageResource(R.drawable.warning);
           }

        }
    }
}
