package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by William on 23/05/2017.
 */
public class RelatorioInconsistenciaISS {
    private String divida;
    private String servico;
    private String classificacaoAtividade;
    private Date dataCalculo;
    private BigDecimal valorLancamento;
    private BigDecimal mediaVariacao;
    private BigDecimal percentualVariacao;
    private BigDecimal baseCalculo;
    private String cadastroEconomico;
    private String razaoSocial;
    private String referencia;

    public Date getDataCalculo() {
        return dataCalculo;
    }

    public void setDataCalculo(Date dataCalculo) {
        this.dataCalculo = dataCalculo;
    }

    public BigDecimal getValorLancamento() {
        return valorLancamento;
    }

    public void setValorLancamento(BigDecimal valorLancamento) {
        this.valorLancamento = valorLancamento;
    }

    public BigDecimal getMediaVariacao() {
        return mediaVariacao;
    }

    public void setMediaVariacao(BigDecimal mediaVariacao) {
        this.mediaVariacao = mediaVariacao;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public String getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(String classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getDivida() {
        return divida;
    }

    public void setDivida(String divida) {
        this.divida = divida;
    }

    public String getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(String cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public BigDecimal getPercentualVariacao() {
        return percentualVariacao;
    }

    public void setPercentualVariacao(BigDecimal percentualVariacao) {
        this.percentualVariacao = percentualVariacao;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }
}
