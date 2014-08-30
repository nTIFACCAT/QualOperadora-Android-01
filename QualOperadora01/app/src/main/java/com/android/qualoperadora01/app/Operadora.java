package com.android.qualoperadora01.app;

/**
 * Created by RICARDO on 18/08/2014.
 */
public class Operadora {
    public static final int VIVO = 0;
    public static final int TIM = 1;
    public static final int OI = 2;
    public static final int CLARO = 3;
    public static final int INVALIDA = 4;
    public String nomeContato;
    public String foneContato;
    public int operadora;


    public Operadora(String nome, String fone,int operadora){
        this.nomeContato = nome;
        this.foneContato = fone;
        this.operadora = operadora;
    }


    public int getImagem(){

        switch (operadora){
            case VIVO:
                return R.drawable.vivo1;
            case CLARO:
                return R.drawable.claro1;
            case OI:
                return R.drawable.oi1;
            case TIM:
                return R.drawable.tim1;
            case INVALIDA:
                return R.drawable.warning;
            default:
                return R.drawable.warning;
        }
    }

}
