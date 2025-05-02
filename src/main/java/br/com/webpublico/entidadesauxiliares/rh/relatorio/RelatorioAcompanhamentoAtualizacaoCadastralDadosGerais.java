package br.com.webpublico.entidadesauxiliares.rh.relatorio;

import java.io.Serializable;
import java.math.BigDecimal;

public class RelatorioAcompanhamentoAtualizacaoCadastralDadosGerais implements Serializable {
    private String orgao;
    private Integer quantidadeConvocados;
    private Integer totalAtualizados;
    private BigDecimal percentualAtualizacao;
    private Integer totalNaoAtualizados;

    public RelatorioAcompanhamentoAtualizacaoCadastralDadosGerais() {
        quantidadeConvocados = 0;
        totalAtualizados = 0;
        totalNaoAtualizados = 0;
        percentualAtualizacao = BigDecimal.ZERO;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public Integer getQuantidadeConvocados() {
        return quantidadeConvocados;
    }

    public void setQuantidadeConvocados(Integer quantidadeConvocados) {
        this.quantidadeConvocados = quantidadeConvocados;
    }


    public BigDecimal getPercentualAtualizacao() {
        return percentualAtualizacao;
    }

    public void setPercentualAtualizacao(BigDecimal percentualAtualizacao) {
        this.percentualAtualizacao = percentualAtualizacao;
    }

    public Integer getTotalAtualizados() {
        return totalAtualizados;
    }

    public void setTotalAtualizados(Integer totalAtualizados) {
        this.totalAtualizados = totalAtualizados;
    }

    public Integer getTotalNaoAtualizados() {
        return totalNaoAtualizados;
    }

    public void setTotalNaoAtualizados(Integer totalNaoAtualizados) {
        this.totalNaoAtualizados = totalNaoAtualizados;
    }
}
