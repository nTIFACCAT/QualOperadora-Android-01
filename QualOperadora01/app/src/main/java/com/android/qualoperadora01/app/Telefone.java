package com.android.qualoperadora01.app;

/**
 * Created by Ricardo on 06/05/2014.
* Classe para definição dos atributos de telefone
 */
public class Telefone{
    String numero;
    String operadora;
    String estado;
    Boolean portabilidade =false;


    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getOperadora() {
        return operadora;
    }

    public void setOperadora(String operadora) {
        this.operadora = operadora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getPortabilidade() {
        return portabilidade;
    }

    public void setPortabilidade(Boolean portabilidade) {
        this.portabilidade = portabilidade;
    }
}
