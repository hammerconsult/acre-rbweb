package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class VOMotoristaAuxiliar implements Serializable {
    private String inscricaoCadastral;
    private String nomePessoa;

    public VOMotoristaAuxiliar() {
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }
}
