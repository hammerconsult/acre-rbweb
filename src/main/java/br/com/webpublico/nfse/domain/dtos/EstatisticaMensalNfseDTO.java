package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class EstatisticaMensalNfseDTO implements NfseDTO {

    private String id;
    private Integer mes;
    private Integer ano;
    private Integer emitidas;
    private Integer retidas;
    private Integer canceladas;
    private BigDecimal totalServicos;
    private BigDecimal iss;

    public EstatisticaMensalNfseDTO(Integer mes, Integer ano) {
        this();
        this.mes = mes;
        this.ano = ano;
    }

    public EstatisticaMensalNfseDTO() {
        this.emitidas = 0;
        this.retidas = 0;
        this.canceladas = 0;
        this.totalServicos = BigDecimal.ZERO;
        this.iss = BigDecimal.ZERO;
    }

    public void somarEmitidas(int emitidas) {
        this.emitidas += emitidas;
    }

    public void somarRetidas(int retidas) {
        this.retidas += retidas;
    }

    public void somarCanceladas(int canceladas) {
        this.canceladas += canceladas;
    }

    public void somarServico(BigDecimal totalServicos) {
        this.totalServicos = this.totalServicos.add(totalServicos);
    }

    public void somarIss(BigDecimal iss) {
        this.iss = this.iss.add(iss);
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getEmitidas() {
        return emitidas;
    }

    public void setEmitidas(Integer emitidas) {
        this.emitidas = emitidas;
    }

    public Integer getRetidas() {
        return retidas;
    }

    public void setRetidas(Integer retidas) {
        this.retidas = retidas;
    }

    public Integer getCanceladas() {
        return canceladas;
    }

    public void setCanceladas(Integer canceladas) {
        this.canceladas = canceladas;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EstatisticaMensalNfseDTO that = (EstatisticaMensalNfseDTO) o;

        if (mes != null ? !mes.equals(that.mes) : that.mes != null) return false;
        return !(ano != null ? !ano.equals(that.ano) : that.ano != null);

    }

    @Override
    public int hashCode() {
        int result = mes != null ? mes.hashCode() : 0;
        result = 31 * result + (ano != null ? ano.hashCode() : 0);
        return result;
    }

}
