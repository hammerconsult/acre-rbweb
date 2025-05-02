package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class VOCertidaDividaAtiva implements Serializable {
    private Long idCda;
    private String numeroCda;
    private Date dataCda;
    private Integer ano;
    private String situacaoJudicial;
    private String situacaoCda;
    private String valorTotal;

    public Long getIdCda() {
        return idCda;
    }

    public void setIdCda(Long idCda) {
        this.idCda = idCda;
    }

    public String getNumeroCda() {
        return numeroCda;
    }

    public void setNumeroCda(String numeroCda) {
        this.numeroCda = numeroCda;
    }

    public Date getDataCda() {
        return dataCda;
    }

    public void setDataCda(Date dataCda) {
        this.dataCda = dataCda;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getSituacaoJudicial() {
        return situacaoJudicial;
    }

    public void setSituacaoJudicial(String situacaoJudicial) {
        this.situacaoJudicial = situacaoJudicial;
    }

    public String getSituacaoCda() {
        return situacaoCda;
    }

    public void setSituacaoCda(String situacaoCda) {
        this.situacaoCda = situacaoCda;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOCertidaDividaAtiva that = (VOCertidaDividaAtiva) o;
        return idCda.equals(that.idCda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCda);
    }
}
