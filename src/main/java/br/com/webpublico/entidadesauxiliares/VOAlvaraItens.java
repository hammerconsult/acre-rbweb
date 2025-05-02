package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VOAlvaraItens implements Serializable {
    private Long id;
    private BigDecimal valor;
    private Date vencimento;
    private Boolean isento;

    public VOAlvaraItens() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public Boolean getIsento() {
        return isento;
    }

    public void setIsento(Boolean isento) {
        this.isento = isento;
    }
}
