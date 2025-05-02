package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class TransmissoesImpressaoLaudoITBI implements Serializable {

    private Long id;
    private Integer ordem;
    private Boolean isento;
    private String isencao;
    private BigDecimal valorReal;
    private List<PessoasImpressaoLaudoITBI> adquirentes;
    private List<PessoasImpressaoLaudoITBI> transmitentes;

    public TransmissoesImpressaoLaudoITBI() {
        this.adquirentes = Lists.newArrayList();
        this.transmitentes = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public List<PessoasImpressaoLaudoITBI> getAdquirentes() {
        return adquirentes;
    }

    public void setAdquirentes(List<PessoasImpressaoLaudoITBI> adquirentes) {
        this.adquirentes = adquirentes;
    }

    public List<PessoasImpressaoLaudoITBI> getTransmitentes() {
        return transmitentes;
    }

    public void setTransmitentes(List<PessoasImpressaoLaudoITBI> transmitentes) {
        this.transmitentes = transmitentes;
    }

    public Boolean getIsento() {
        return isento;
    }

    public void setIsento(Boolean isento) {
        this.isento = isento;
    }

    public BigDecimal getValorReal() {
        return valorReal;
    }

    public void setValorReal(BigDecimal valor) {
        this.valorReal = valor;
    }

    public String getIsencao() {
        return isencao;
    }

    public void setIsencao(String isencao) {
        this.isencao = isencao;
    }
}
