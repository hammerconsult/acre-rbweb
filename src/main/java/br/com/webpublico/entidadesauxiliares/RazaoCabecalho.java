package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Mateus on 07/08/2015.
 */
public class RazaoCabecalho {
    private String descricaoConta;
    private String unidade;
    private String orgao;
    private String unidadeGestora;
    private BigDecimal saldoAnterior;
    private List<RazaoMovimentos> movimentos;

    public RazaoCabecalho() {
    }

    public String getDescricaoConta() {
        return descricaoConta;
    }

    public void setDescricaoConta(String descricaoConta) {
        this.descricaoConta = descricaoConta;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public List<RazaoMovimentos> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<RazaoMovimentos> movimentos) {
        this.movimentos = movimentos;
    }
}
