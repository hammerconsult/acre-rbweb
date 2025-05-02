package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class BoletimCadastroEconomicoCNAE implements Serializable {
    private String codigoDescricao;
    private String grauDeRisco;

    public BoletimCadastroEconomicoCNAE() {
    }

    public String getCodigoDescricao() {
        return codigoDescricao;
    }

    public void setCodigoDescricao(String codigoDescricao) {
        this.codigoDescricao = codigoDescricao;
    }

    public String getGrauDeRisco() {
        return grauDeRisco;
    }

    public void setGrauDeRisco(String grauDeRisco) {
        this.grauDeRisco = grauDeRisco;
    }
}
