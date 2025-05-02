package br.com.webpublico.nfse.domain.dtos;

public class RelatorioPrestadoresAutorizados {
    private String inscricaoCadastral;
    private String pessoa;
    private String situacaoCadastral;
    private String porte;
    private String simples;
    private String tipoIss;
    private String isento;
    private String imuneIss;
    private String regimeTributacao;
    private String classificacao;

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(String situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getSimples() {
        return simples;
    }

    public void setSimples(String simples) {
        this.simples = simples;
    }

    public String getTipoIss() {
        return tipoIss;
    }

    public void setTipoIss(String tipoIss) {
        this.tipoIss = tipoIss;
    }

    public String getIsento() {
        return isento;
    }

    public void setIsento(String isento) {
        this.isento = isento;
    }

    public String getImuneIss() {
        return imuneIss;
    }

    public void setImuneIss(String imuneIss) {
        this.imuneIss = imuneIss;
    }

    public String getRegimeTributacao() {
        return regimeTributacao;
    }

    public void setRegimeTributacao(String regimeTributacao) {
        this.regimeTributacao = regimeTributacao;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }
}
