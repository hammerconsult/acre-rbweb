package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 26/12/13
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
public class ParametrosRelatorioConferenciaCreditoSalario {

    private String orgao;
    private String banco;
    private String agencia;
    private String conta;

    private Integer quantidadeRegistros;
    private BigDecimal totalCredito;

    private List<ItemRelatorioConferenciaCreditoSalario> itens;

    public ParametrosRelatorioConferenciaCreditoSalario() {
        quantidadeRegistros = 0;
        itens = Lists.newArrayList();
        totalCredito = BigDecimal.ZERO;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public Integer getQuantidadeRegistros() {
        return quantidadeRegistros;
    }

    public void setQuantidadeRegistros(Integer quantidadeRegistros) {
        this.quantidadeRegistros = quantidadeRegistros;
    }

    public BigDecimal getTotalCredito() {
        return totalCredito;
    }

    public void setTotalCredito(BigDecimal totalCredito) {
        this.totalCredito = totalCredito;
    }

    public List<ItemRelatorioConferenciaCreditoSalario> getItens() {
        return itens;
    }

    public void setItens(List<ItemRelatorioConferenciaCreditoSalario> itens) {
        this.itens = itens;
    }
}
