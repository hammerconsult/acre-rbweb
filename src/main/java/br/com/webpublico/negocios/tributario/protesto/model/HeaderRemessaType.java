package br.com.webpublico.negocios.tributario.protesto.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "headerRemessaType", propOrder = {
    "registroHeader", "codigoPortador", "nomePortador", "dataMovimento", "siglaRemetente", "siglaDestinatario", "siglaTransacao",
    "sequencia", "quantidadeParcelas", "quantidadeParcelasContribuintePrincipal", "quantidadeTitulosTipoDmiDriCbi", "quantidadeDemaisParcelas",
    "agenciaCentralizadora", "versaoLayout", "codigoMunicipio", "complementoRegistro", "sequenciaDoRegistro"
})
public class HeaderRemessaType {

    @XmlAttribute(name = "h01")
    private String registroHeader;
    @XmlAttribute(name = "h02")
    private String codigoPortador;
    @XmlAttribute(name = "h03")
    private String nomePortador;
    @XmlAttribute(name = "h04")
    private String dataMovimento;
    @XmlAttribute(name = "h05")
    private String siglaRemetente;
    @XmlAttribute(name = "h06")
    private String siglaDestinatario;
    @XmlAttribute(name = "h07")
    private String siglaTransacao;
    @XmlAttribute(name = "h08")
    private String sequencia;
    @XmlAttribute(name = "h09")
    private String quantidadeParcelas;
    @XmlAttribute(name = "h10")
    private String quantidadeParcelasContribuintePrincipal;
    @XmlAttribute(name = "h11")
    private String quantidadeTitulosTipoDmiDriCbi;
    @XmlAttribute(name = "h12")
    private String quantidadeDemaisParcelas;
    @XmlAttribute(name = "h13")
    private String agenciaCentralizadora;
    @XmlAttribute(name = "h14")
    private String versaoLayout;
    @XmlAttribute(name = "h15")
    private String codigoMunicipio;
    @XmlAttribute(name = "h16")
    private String complementoRegistro;
    @XmlAttribute(name = "h17")
    private String sequenciaDoRegistro;

    public String getRegistroHeader() {
        return registroHeader;
    }

    public void setRegistroHeader(String registroHeader) {
        this.registroHeader = registroHeader;
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

    public String getSiglaRemetente() {
        return siglaRemetente;
    }

    public void setSiglaRemetente(String siglaRemetente) {
        this.siglaRemetente = siglaRemetente;
    }

    public String getSiglaDestinatario() {
        return siglaDestinatario;
    }

    public void setSiglaDestinatario(String siglaDestinatario) {
        this.siglaDestinatario = siglaDestinatario;
    }

    public String getSiglaTransacao() {
        return siglaTransacao;
    }

    public void setSiglaTransacao(String siglaTransacao) {
        this.siglaTransacao = siglaTransacao;
    }

    public String getSequencia() {
        return sequencia;
    }

    public void setSequencia(String sequencia) {
        this.sequencia = sequencia;
    }

    public String getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(String quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public String getQuantidadeParcelasContribuintePrincipal() {
        return quantidadeParcelasContribuintePrincipal;
    }

    public void setQuantidadeParcelasContribuintePrincipal(String quantidadeParcelasContribuintePrincipal) {
        this.quantidadeParcelasContribuintePrincipal = quantidadeParcelasContribuintePrincipal;
    }

    public String getQuantidadeTitulosTipoDmiDriCbi() {
        return quantidadeTitulosTipoDmiDriCbi;
    }

    public void setQuantidadeTitulosTipoDmiDriCbi(String quantidadeTitulosTipoDmiDriCbi) {
        this.quantidadeTitulosTipoDmiDriCbi = quantidadeTitulosTipoDmiDriCbi;
    }

    public String getQuantidadeDemaisParcelas() {
        return quantidadeDemaisParcelas;
    }

    public void setQuantidadeDemaisParcelas(String quantidadeDemaisParcelas) {
        this.quantidadeDemaisParcelas = quantidadeDemaisParcelas;
    }

    public String getAgenciaCentralizadora() {
        return agenciaCentralizadora;
    }

    public void setAgenciaCentralizadora(String agenciaCentralizadora) {
        this.agenciaCentralizadora = agenciaCentralizadora;
    }

    public String getVersaoLayout() {
        return versaoLayout;
    }

    public void setVersaoLayout(String versaoLayout) {
        this.versaoLayout = versaoLayout;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public String getComplementoRegistro() {
        return complementoRegistro;
    }

    public void setComplementoRegistro(String complementoRegistro) {
        this.complementoRegistro = complementoRegistro;
    }

    public String getSequenciaDoRegistro() {
        return sequenciaDoRegistro;
    }

    public void setSequenciaDoRegistro(String sequenciaDoRegistro) {
        this.sequenciaDoRegistro = sequenciaDoRegistro;
    }
}
