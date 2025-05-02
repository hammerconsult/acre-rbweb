package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by Desenvolvimento on 12/04/2017.
 */
public class DetalhamentoProvimento {

    private String numeroVinculoFP;
	private String sequencia;
	private String tipoAtoLegal;
	private String numeroAtoLegal;
	private String ano;
	private Date dataEmissao;
	private Date dataPublicacao;
	private String tipoProvimento;
	private Date dataProvimento;
	private String observacao;

    public DetalhamentoProvimento() {
    }

    public String getNumeroVinculoFP() {
        return numeroVinculoFP;
    }

    public void setNumeroVinculoFP(String numeroVinculoFP) {
        this.numeroVinculoFP = numeroVinculoFP;
    }

    public String getSequencia() {
        return sequencia;
    }

    public void setSequencia(String sequencia) {
        this.sequencia = sequencia;
    }

    public String getTipoAtoLegal() {
        return tipoAtoLegal;
    }

    public void setTipoAtoLegal(String tipoAtoLegal) {
        this.tipoAtoLegal = tipoAtoLegal;
    }

    public String getNumeroAtoLegal() {
        return numeroAtoLegal;
    }

    public void setNumeroAtoLegal(String numeroAtoLegal) {
        this.numeroAtoLegal = numeroAtoLegal;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public String getTipoProvimento() {
        return tipoProvimento;
    }

    public void setTipoProvimento(String tipoProvimento) {
        this.tipoProvimento = tipoProvimento;
    }

    public Date getDataProvimento() {
        return dataProvimento;
    }

    public void setDataProvimento(Date dataProvimento) {
        this.dataProvimento = dataProvimento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }
}
