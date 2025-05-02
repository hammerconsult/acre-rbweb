package br.com.webpublico.negocios.tributario.protesto.model;

import com.google.common.collect.Lists;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "comarcaRemessaType", propOrder = {
        "codigoMunicipio", "header", "transacoes", "trailler"
})
public class ComarcaRemessaType {

    @XmlAttribute(name = "CodMun", required = true)
    private String codigoMunicipio;
    @XmlElement(name = "hd", required = true)
    private HeaderRemessaType header;
    @XmlElement(name = "tl", required = true)
    private TraillerRemessaType trailler;
    @XmlElement(name = "tr", required = true)
    private List<TransacaoRemessaType> transacoes;

    public ComarcaRemessaType() {
        this.transacoes = Lists.newArrayList();
    }

    public HeaderRemessaType getHeader() {
        return header;
    }

    public void setHeader(HeaderRemessaType header) {
        this.header = header;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public TraillerRemessaType getTrailler() {
        return trailler;
    }

    public void setTrailler(TraillerRemessaType trailler) {
        this.trailler = trailler;
    }

    public List<TransacaoRemessaType> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<TransacaoRemessaType> transacoes) {
        this.transacoes = transacoes;
    }
}
