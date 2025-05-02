package br.com.webpublico.entidadesauxiliares.administrativo.relatorio;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by wellington on 08/08/17.
 */
public class RelatorioAjustesBem implements Serializable {

    private Long idUnidadeOrcamentaria;
    private String codigoUnidadeOrcamentaria;
    private String descricaoUnidadeOrcamentaria;
    private Integer inventarioQuantidade;
    private BigDecimal inventarioValor;
    private Integer depreciacaoQuantidade;
    private BigDecimal depreciacaoValor;
    private Integer exaustaoQuantidade;
    private BigDecimal exaustaoValor;
    private Integer amortizacaoQuantidade;
    private BigDecimal amortizacaoValor;
    private Integer ajusteQuantidade;
    private BigDecimal ajusteValor;
    private Integer naoAplicavelQuantidade;
    private Integer naoAjustadoQuantidade;

    public Long getIdUnidadeOrcamentaria() {
        return idUnidadeOrcamentaria;
    }

    public void setIdUnidadeOrcamentaria(Long idUnidadeOrcamentaria) {
        this.idUnidadeOrcamentaria = idUnidadeOrcamentaria;
    }

    public String getCodigoUnidadeOrcamentaria() {
        return codigoUnidadeOrcamentaria;
    }

    public void setCodigoUnidadeOrcamentaria(String codigoUnidadeOrcamentaria) {
        this.codigoUnidadeOrcamentaria = codigoUnidadeOrcamentaria;
    }

    public String getDescricaoUnidadeOrcamentaria() {
        return descricaoUnidadeOrcamentaria;
    }

    public void setDescricaoUnidadeOrcamentaria(String descricaoUnidadeOrcamentaria) {
        this.descricaoUnidadeOrcamentaria = descricaoUnidadeOrcamentaria;
    }

    public Integer getInventarioQuantidade() {
        return inventarioQuantidade;
    }

    public void setInventarioQuantidade(Integer inventarioQuantidade) {
        this.inventarioQuantidade = inventarioQuantidade;
    }

    public BigDecimal getInventarioValor() {
        return inventarioValor;
    }

    public void setInventarioValor(BigDecimal inventarioValor) {
        this.inventarioValor = inventarioValor;
    }

    public Integer getDepreciacaoQuantidade() {
        return depreciacaoQuantidade;
    }

    public void setDepreciacaoQuantidade(Integer depreciacaoQuantidade) {
        this.depreciacaoQuantidade = depreciacaoQuantidade;
    }

    public BigDecimal getDepreciacaoValor() {
        return depreciacaoValor;
    }

    public void setDepreciacaoValor(BigDecimal depreciacaoValor) {
        this.depreciacaoValor = depreciacaoValor;
    }

    public Integer getExaustaoQuantidade() {
        return exaustaoQuantidade;
    }

    public void setExaustaoQuantidade(Integer exaustaoQuantidade) {
        this.exaustaoQuantidade = exaustaoQuantidade;
    }

    public BigDecimal getExaustaoValor() {
        return exaustaoValor;
    }

    public void setExaustaoValor(BigDecimal exaustaoValor) {
        this.exaustaoValor = exaustaoValor;
    }

    public Integer getAmortizacaoQuantidade() {
        return amortizacaoQuantidade;
    }

    public void setAmortizacaoQuantidade(Integer amortizacaoQuantidade) {
        this.amortizacaoQuantidade = amortizacaoQuantidade;
    }

    public BigDecimal getAmortizacaoValor() {
        return amortizacaoValor;
    }

    public void setAmortizacaoValor(BigDecimal amortizacaoValor) {
        this.amortizacaoValor = amortizacaoValor;
    }

    public Integer getNaoAplicavelQuantidade() {
        return naoAplicavelQuantidade;
    }

    public void setNaoAplicavelQuantidade(Integer naoAplicavelQuantidade) {
        this.naoAplicavelQuantidade = naoAplicavelQuantidade;
    }

    public Integer getNaoAjustadoQuantidade() {
        return naoAjustadoQuantidade;
    }

    public void setNaoAjustadoQuantidade(Integer naoAjustadoQuantidade) {
        this.naoAjustadoQuantidade = naoAjustadoQuantidade;
    }

    public Integer getAjusteQuantidade() {
        return ajusteQuantidade;
    }

    public void setAjusteQuantidade(Integer ajusteQuantidade) {
        this.ajusteQuantidade = ajusteQuantidade;
    }

    public BigDecimal getAjusteValor() {
        return ajusteValor;
    }

    public void setAjusteValor(BigDecimal ajusteValor) {
        this.ajusteValor = ajusteValor;
    }
}
