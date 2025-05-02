package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class ReceitaTributariaBrutaNfseDTO implements NfseDTO {

    private Long id;
    private Integer ano;
    private Integer mes;
    private BigDecimal valorBruto;

    public ReceitaTributariaBrutaNfseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public BigDecimal getValorBruto() {
        return valorBruto;
    }

    public void setValorBruto(BigDecimal valorBruto) {
        this.valorBruto = valorBruto;
    }
}
