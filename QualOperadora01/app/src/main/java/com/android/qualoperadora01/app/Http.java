package com.android.qualoperadora01.app;

import java.util.Map;

/**
 * Created by RICARDO on 12/06/2014.
 *
 * Classe utilizada para chamar as implementações de acesso a web,
 * GET, POST e também o get utilizando a API HttpCLiente Jakarta
 */
public abstract class Http {
    //Utliza UrlConnection
    public static final int NORMAL=1;
    //Utiliza o Jakarta HttpClient
    public static final int JAKARTA=2;
    public static Http getInstance(int tipo){
        switch (tipo){
            case NORMAL:
                //UrlConnection
                return new HttpNormalImpl();
            case JAKARTA:
                return null;
            /*
            * TODO: Fazer a implemantação utilizando Jakarta HttpClient
            * */
                //return new HttpClientImpl();

            default:
                //UrlConnection
                return new HttpNormalImpl();
        }

    }

    //Retorna o texto do arquivo ou Json conforme a url informada
    public abstract String downloadArquivo(String url);
    //Retorna os bytes da imagem
    public abstract byte[] downloadImagem(String url);
    //Faz post enviando parâmetos
    public abstract String doPost(String url, Map map);


}
