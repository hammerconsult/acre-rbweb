package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class PessoasImpressaoLaudoITBI implements Serializable {
    private String nome;
    private String cpfCnpj;
    private String percentual;

    public PessoasImpressaoLaudoITBI() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getPercentual() {
        return percentual;
    }

    public void setPercentual(String percentual) {
        this.percentual = percentual;
    }
}
