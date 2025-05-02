package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Mateus on 08/07/2015.
 */
public class DemonstrativoBensMoveis {

    private String natureza;
    private String naturezaTotalizador;
    private String descricao;
    private BigDecimal saldoInicial;
    private BigDecimal credito;
    private BigDecimal debito;
    private BigDecimal saldoFinal;
    private String orgao;
    private String unidade;
    private String unidadeGestora;
    private List<DemonstrativoBensMoveis> itens;

    public DemonstrativoBensMoveis() {
    }

    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public List<DemonstrativoBensMoveis> getItens() {
        return itens;
    }

    public void setItens(List<DemonstrativoBensMoveis> itens) {
        this.itens = itens;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public String getNaturezaTotalizador() {
        return naturezaTotalizador;
    }

    public void setNaturezaTotalizador(String naturezaTotalizador) {
        this.naturezaTotalizador = naturezaTotalizador;
    }
}
