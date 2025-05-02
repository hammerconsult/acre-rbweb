package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfoPeriodoAquisitivo {

    private Integer item;
    private Date inicio;
    private Date fim;
    private BigDecimal quantidadeDias;
    private BigDecimal saldoDireito;
    private String baseCargo;

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public BigDecimal getQuantidadeDias() {
        return quantidadeDias;
    }

    public void setQuantidadeDias(BigDecimal quantidadeDias) {
        this.quantidadeDias = quantidadeDias;
    }

    public BigDecimal getSaldoDireito() {
        return saldoDireito;
    }

    public void setSaldoDireito(BigDecimal saldoDireito) {
        this.saldoDireito = saldoDireito;
    }

    public String getBaseCargo() {
        return baseCargo;
    }

    public void setBaseCargo(String baseCargo) {
        this.baseCargo = baseCargo;
    }
}
