package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

public class FichaCredorClassePessoa {

    private Date inicioVigencia;
    private Date fimVigencia;
    private String operacaoClasseCredor;
    private String codigoClasse;
    private String descricaoClasse;
    private String tipoClasseCredor;

    public FichaCredorClassePessoa() {
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public String getOperacaoClasseCredor() {
        return operacaoClasseCredor;
    }

    public void setOperacaoClasseCredor(String operacaoClasseCredor) {
        this.operacaoClasseCredor = operacaoClasseCredor;
    }

    public String getCodigoClasse() {
        return codigoClasse;
    }

    public void setCodigoClasse(String codigoClasse) {
        this.codigoClasse = codigoClasse;
    }

    public String getDescricaoClasse() {
        return descricaoClasse;
    }

    public void setDescricaoClasse(String descricaoClasse) {
        this.descricaoClasse = descricaoClasse;
    }

    public String getTipoClasseCredor() {
        return tipoClasseCredor;
    }

    public void setTipoClasseCredor(String tipoClasseCredor) {
        this.tipoClasseCredor = tipoClasseCredor;
    }
}
