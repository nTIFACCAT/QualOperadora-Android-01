package com.android.qualoperadora01.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ricardo on 06/05/2014.
 *
 * Classe  que recebe um número de telefone e retorna dados da web de um JSON
 *
 *
 * TODO: Verificar se necessário fazer em activity ou podemos maipular somente com a classe java
 *       Ou ver se implementaremos exemplo para apresentar resultados em outra tela no caso esta activity
 */


public class TelefoneJSON extends Activity {




        protected void onCreate(Bundle savedInstanceState){
           super.onCreate(savedInstanceState);

           /* Teste de passagem de parâmetro
              Como esta activity foi iniciada através de uma intent então podemos pegar os Extras (parâmetros) passados e manipular ou utilizar
            */
            String fone = this.getIntent().getStringExtra("numero");
            Log.i("Número","Número passado como parâmetro: "+fone.toString());

            Intent i = new Intent();
            i.putExtra("operadora", "Vivo");
            i.putExtra("estado", "RS");
            i.putExtra("portabilidade", "false");
            // Seta o resultado com o código da actiity que vai retornar para a mesma que foi chamada
            setResult(0, i);
            //Finaliza a intent voltando para a anterior
            finish();



        }

}
