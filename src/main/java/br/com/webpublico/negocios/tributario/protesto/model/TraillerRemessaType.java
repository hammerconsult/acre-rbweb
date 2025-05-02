package br.com.webpublico.negocios.tributario.protesto.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "traillerRemessaType", propOrder = {
    "registroTrailler", "codigoPortador", "nomePortador", "dataMovimento", "somatorioSegurancaRegistro",
    "somatorioSegurancaValores", "complementoRegistro", "sequencialDoRegistro"
})
public class TraillerRemessaType {

    @XmlAttribute(name = "t01")
    private String registroTrailler;
    @XmlAttribute(name = "t02")
    private String codigoPortador;
    @XmlAttribute(name = "t03")
    private String nomePortador;
    @XmlAttribute(name = "t04")
    private String dataMovimento;
    @XmlAttribute(name = "t05")
    private String somatorioSegurancaRegistro;
    @XmlAttribute(name = "t06")
    private String somatorioSegurancaValores;
    @XmlAttribute(name = "t07")
    private String complementoRegistro;
    @XmlAttribute(name = "t08")
    private String sequencialDoRegistro;

    public String getRegistroTrailler() {
        return registroTrailler;
    }

    public void setRegistroTrailler(String registroTrailler) {
        this.registroTrailler = registroTrailler;
    }

    public String getCodigoPortador() {
        return codigoPortador;
    }

    public void setCodigoPortador(String codigoPortador) {
        this.codigoPortador = codigoPortador;
    }

    public String getNomePortador() {
        return nomePortador;
    }

    public void setNomePortador(String nomePortador) {
        this.nomePortador = nomePortador;
    }

    public String getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(String dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public String getSomatorioSegurancaRegistro() {
        return somatorioSegurancaRegistro;
    }

    public void setSomatorioSegurancaRegistro(String somatorioSegurancaRegistro) {
        this.somatorioSegurancaRegistro = somatorioSegurancaRegistro;
    }

    public String getSomatorioSegurancaValores() {
        return somatorioSegurancaValores;
    }

    public void setSomatorioSegurancaValores(String somatorioSegurancaValores) {
        this.somatorioSegurancaValores = somatorioSegurancaValores;
    }

    public String getComplementoRegistro() {
        return complementoRegistro;
    }

    public void setComplementoRegistro(String complementoRegistro) {
        this.complementoRegistro = complementoRegistro;
    }

    public String getSequencialDoRegistro() {
        return sequencialDoRegistro;
    }

    public void setSequencialDoRegistro(String sequencialDoRegistro) {
        this.sequencialDoRegistro = sequencialDoRegistro;
    }
}
