package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;

/**
 * Created by fox_m on 30/03/2016.
 */
public class TermoRescisaoVerbaDescontoEVantagem {
    private String codigo;
    private String descricao;
    private BigDecimal referencia;
    private BigDecimal valor;
    private Boolean irrf;
    private Boolean inss;
    private Boolean rpps;

    public TermoRescisaoVerbaDescontoEVantagem() {
        irrf = false;
        inss = false;
        rpps = false;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getReferencia() {
        return referencia;
    }

    public void setReferencia(BigDecimal referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getIrrf() {
        return irrf;
    }

    public void setIrrf(Boolean irrf) {
        this.irrf = irrf;
    }

    public Boolean getInss() {
        return inss;
    }

    public void setInss(Boolean inss) {
        this.inss = inss;
    }

    public Boolean getRpps() {
        return rpps;
    }

    public void setRpps(Boolean rpps) {
        this.rpps = rpps;
    }
}
