package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 10/08/2015.
 */
public class RazaoMovimentos {
    private String codigoUnidade;
    private String codigoOrgao;
    private String codigoEvento;
    private String descricaoEvento;
    private String codigoLCP;
    private String itemLCP;
    private String historico;
    private Date data;
    private String codigoContaContra;
    private String descricaoContaContra;
    private BigDecimal valorMovimento;
    private String codigoCLP;
    private String operacao;

    public RazaoMovimentos() {
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public String getDescricaoEvento() {
        return descricaoEvento;
    }

    public void setDescricaoEvento(String descricaoEvento) {
        this.descricaoEvento = descricaoEvento;
    }

    public String getCodigoLCP() {
        return codigoLCP;
    }

    public void setCodigoLCP(String codigoLCP) {
        this.codigoLCP = codigoLCP;
    }

    public String getItemLCP() {
        return itemLCP;
    }

    public void setItemLCP(String itemLCP) {
        this.itemLCP = itemLCP;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getCodigoContaContra() {
        return codigoContaContra;
    }

    public void setCodigoContaContra(String codigoContaContra) {
        this.codigoContaContra = codigoContaContra;
    }

    public String getDescricaoContaContra() {
        return descricaoContaContra;
    }

    public void setDescricaoContaContra(String descricaoContaContra) {
        this.descricaoContaContra = descricaoContaContra;
    }

    public BigDecimal getValorMovimento() {
        return valorMovimento;
    }

    public void setValorMovimento(BigDecimal valorMovimento) {
        this.valorMovimento = valorMovimento;
    }

    public String getCodigoCLP() {
        return codigoCLP;
    }

    public void setCodigoCLP(String codigoCLP) {
        this.codigoCLP = codigoCLP;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }
}
