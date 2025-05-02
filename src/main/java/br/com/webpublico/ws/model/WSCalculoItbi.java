package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.AdquirentesCalculoITBI;
import br.com.webpublico.entidades.CalculoITBI;
import br.com.webpublico.entidades.TransmitentesCalculoITBI;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class WSCalculoItbi implements Serializable {
    private BigDecimal valor;
    private BigDecimal baseCalculo;
    private List<WSPessoa> adquirentes;
    private List<WSPessoa> transmitentes;
    private Boolean isento;
    private Integer ordemCalculo;

    public WSCalculoItbi() {
        this.valor = BigDecimal.ZERO;
        this.baseCalculo = BigDecimal.ZERO;
        this.adquirentes = Lists.newArrayList();
        this.transmitentes = Lists.newArrayList();
    }

    public WSCalculoItbi(CalculoITBI calculoITBI) {
        this();
        for (AdquirentesCalculoITBI adq : calculoITBI.getAdquirentesCalculo()) {
            adquirentes.add(new WSPessoa(adq.getAdquirente()));
        }
        for (TransmitentesCalculoITBI trs : calculoITBI.getTransmitentesCalculo()) {
            transmitentes.add(new WSPessoa(trs.getPessoa()));
        }
        valor = calculoITBI.getValorEfetivo();
        baseCalculo = calculoITBI.getBaseCalculo();
        isento = calculoITBI.getIsento();
        ordemCalculo = calculoITBI.getOrdemCalculo();
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public List<WSPessoa> getAdquirentes() {
        return adquirentes;
    }

    public void setAdquirentes(List<WSPessoa> adquirentes) {
        this.adquirentes = adquirentes;
    }

    public List<WSPessoa> getTransmitentes() {
        return transmitentes;
    }

    public void setTransmitentes(List<WSPessoa> transmitentes) {
        this.transmitentes = transmitentes;
    }

    public Boolean getIsento() {
        return isento;
    }

    public void setIsento(Boolean isento) {
        this.isento = isento;
    }

    public Integer getOrdemCalculo() {
        return ordemCalculo;
    }

    public void setOrdemCalculo(Integer ordemCalculo) {
        this.ordemCalculo = ordemCalculo;
    }
}
