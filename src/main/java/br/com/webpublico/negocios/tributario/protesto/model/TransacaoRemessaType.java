package br.com.webpublico.negocios.tributario.protesto.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transacaoRemessaType", propOrder = {
    "registroTransacao", "codigoPortador", "codigoAgenciaCedente", "cedente", "sacador", "documentoSacador", "enderecoSacador",
    "cepSacador", "cidadeSacador", "ufSacador", "nossoNumero", "especieParcela", "numeroDaParcela", "emissaoParcela",
    "vencimentoParcela", "tipoMoeda", "valorParcela", "saldoParcela", "pracaDeProtesto", "tipoEndosso", "infoAceite",
    "quantidadeContribuintes", "nomeContribuinte", "tipoDoctoContribuinte", "cpfCnpjContribuinte", "documentoContribuinte",
    "enderecoContribuinte", "cepContribuinte", "cidadeContribuinte", "ufContribuinte", "codigoCartorio", "protocoloCartorio",
    "tipoOcorrencia", "dataProtocolo", "valorCustasCartorio", "declaracaoDoPortador", "dataOcorrencia", "codigoIrregularidade",
    "bairroContribuinte", "valorCustasCartorioDistribuidor", "registroDeDistribuicao", "valorGravacaoEletronica", "numOperacaoBanco",
    "numContratoBanco", "numParcelaContrato", "tipoLetraCambio", "complementoCodigoIrregularidade", "protestoMotivoFalencia",
    "instrumentoProtesto", "valorDemaisDespesas", "complementoRegistro", "sequencialDoRegistro"
})
public class TransacaoRemessaType {

    @XmlAttribute(name = "t01")
    private String registroTransacao;
    @XmlAttribute(name = "t02")
    private String codigoPortador;
    @XmlAttribute(name = "t03")
    private String codigoAgenciaCedente;
    @XmlAttribute(name = "t04")
    private String cedente;
    @XmlAttribute(name = "t05")
    private String sacador;
    @XmlAttribute(name = "t06")
    private String documentoSacador;
    @XmlAttribute(name = "t07")
    private String enderecoSacador;
    @XmlAttribute(name = "t08")
    private String cepSacador;
    @XmlAttribute(name = "t09")
    private String cidadeSacador;
    @XmlAttribute(name = "t10")
    private String ufSacador;
    @XmlAttribute(name = "t11")
    private String nossoNumero;
    @XmlAttribute(name = "t12")
    private String especieParcela;
    @XmlAttribute(name = "t13")
    private String numeroDaParcela;
    @XmlAttribute(name = "t14")
    private String emissaoParcela;
    @XmlAttribute(name = "t15")
    private String vencimentoParcela;
    @XmlAttribute(name = "t16")
    private String tipoMoeda;
    @XmlAttribute(name = "t17")
    private String valorParcela;
    @XmlAttribute(name = "t18")
    private String saldoParcela;
    @XmlAttribute(name = "t19")
    private String pracaDeProtesto;
    @XmlAttribute(name = "t20")
    private String tipoEndosso;
    @XmlAttribute(name = "t21")
    private String infoAceite;
    @XmlAttribute(name = "t22")
    private String quantidadeContribuintes;
    @XmlAttribute(name = "t23")
    private String nomeContribuinte;
    @XmlAttribute(name = "t24")
    private String tipoDoctoContribuinte;
    @XmlAttribute(name = "t25")
    private String cpfCnpjContribuinte;
    @XmlAttribute(name = "t26")
    private String documentoContribuinte;
    @XmlAttribute(name = "t27")
    private String enderecoContribuinte;
    @XmlAttribute(name = "t28")
    private String cepContribuinte;
    @XmlAttribute(name = "t29")
    private String cidadeContribuinte;
    @XmlAttribute(name = "t30")
    private String ufContribuinte;
    @XmlAttribute(name = "t31")
    private String codigoCartorio;
    @XmlAttribute(name = "t32")
    private String protocoloCartorio;
    @XmlAttribute(name = "t33")
    private String tipoOcorrencia;
    @XmlAttribute(name = "t34")
    private String dataProtocolo;
    @XmlAttribute(name = "t35")
    private String valorCustasCartorio;
    @XmlAttribute(name = "t36")
    private String declaracaoDoPortador;
    @XmlAttribute(name = "t37")
    private String dataOcorrencia;
    @XmlAttribute(name = "t38")
    private String codigoIrregularidade;
    @XmlAttribute(name = "t39")
    private String bairroContribuinte;
    @XmlAttribute(name = "t40")
    private String valorCustasCartorioDistribuidor;
    @XmlAttribute(name = "t41")
    private String registroDeDistribuicao;
    @XmlAttribute(name = "t42")
    private String valorGravacaoEletronica;
    @XmlAttribute(name = "t43")
    private String numOperacaoBanco;
    @XmlAttribute(name = "t44")
    private String numContratoBanco;
    @XmlAttribute(name = "t45")
    private String numParcelaContrato;
    @XmlAttribute(name = "t46")
    private String tipoLetraCambio;
    @XmlAttribute(name = "t47")
    private String complementoCodigoIrregularidade;
    @XmlAttribute(name = "t48")
    private String protestoMotivoFalencia;
    @XmlAttribute(name = "t49")
    private String instrumentoProtesto;
    @XmlAttribute(name = "t50")
    private String valorDemaisDespesas;
    @XmlAttribute(name = "t51")
    private String complementoRegistro;
    @XmlAttribute(name = "t52")
    private String sequencialDoRegistro;

    public String getRegistroTransacao() {
        return registroTransacao;
    }

    public void setRegistroTransacao(String registroTransacao) {
        this.registroTransacao = registroTransacao;
    }

    public String getCodigoPortador() {
        return codigoPortador;
    }

    public void setCodigoPortador(String codigoPortador) {
        this.codigoPortador = codigoPortador;
    }

    public String getCodigoAgenciaCedente() {
        return codigoAgenciaCedente;
    }

    public void setCodigoAgenciaCedente(String codigoAgenciaCedente) {
        this.codigoAgenciaCedente = codigoAgenciaCedente;
    }

    public String getCedente() {
        return cedente;
    }

    public void setCedente(String cedente) {
        this.cedente = cedente;
    }

    public String getSacador() {
        return sacador;
    }

    public void setSacador(String sacador) {
        this.sacador = sacador;
    }

    public String getDocumentoSacador() {
        return documentoSacador;
    }

    public void setDocumentoSacador(String documentoSacador) {
        this.documentoSacador = documentoSacador;
    }

    public String getEnderecoSacador() {
        return enderecoSacador;
    }

    public void setEnderecoSacador(String enderecoSacador) {
        this.enderecoSacador = enderecoSacador;
    }

    public String getCepSacador() {
        return cepSacador;
    }

    public void setCepSacador(String cepSacador) {
        this.cepSacador = cepSacador;
    }

    public String getCidadeSacador() {
        return cidadeSacador;
    }

    public void setCidadeSacador(String cidadeSacador) {
        this.cidadeSacador = cidadeSacador;
    }

    public String getUfSacador() {
        return ufSacador;
    }

    public void setUfSacador(String ufSacador) {
        this.ufSacador = ufSacador;
    }

    public String getNossoNumero() {
        return nossoNumero;
    }

    public void setNossoNumero(String nossoNumero) {
        this.nossoNumero = nossoNumero;
    }

    public String getEspecieParcela() {
        return especieParcela;
    }

    public void setEspecieParcela(String especieParcela) {
        this.especieParcela = especieParcela;
    }

    public String getNumeroDaParcela() {
        return numeroDaParcela;
    }

    public void setNumeroDaParcela(String numeroDaParcela) {
        this.numeroDaParcela = numeroDaParcela;
    }

    public String getEmissaoParcela() {
        return emissaoParcela;
    }

    public void setEmissaoParcela(String emissaoParcela) {
        this.emissaoParcela = emissaoParcela;
    }

    public String getVencimentoParcela() {
        return vencimentoParcela;
    }

    public void setVencimentoParcela(String vencimentoParcela) {
        this.vencimentoParcela = vencimentoParcela;
    }

    public String getTipoMoeda() {
        return tipoMoeda;
    }

    public void setTipoMoeda(String tipoMoeda) {
        this.tipoMoeda = tipoMoeda;
    }

    public String getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(String valorParcela) {
        this.valorParcela = valorParcela;
    }

    public String getSaldoParcela() {
        return saldoParcela;
    }

    public void setSaldoParcela(String saldoParcela) {
        this.saldoParcela = saldoParcela;
    }

    public String getPracaDeProtesto() {
        return pracaDeProtesto;
    }

    public void setPracaDeProtesto(String pracaDeProtesto) {
        this.pracaDeProtesto = pracaDeProtesto;
    }

    public String getTipoEndosso() {
        return tipoEndosso;
    }

    public void setTipoEndosso(String tipoEndosso) {
        this.tipoEndosso = tipoEndosso;
    }

    public String getInfoAceite() {
        return infoAceite;
    }

    public void setInfoAceite(String infoAceite) {
        this.infoAceite = infoAceite;
    }

    public String getQuantidadeContribuintes() {
        return quantidadeContribuintes;
    }

    public void setQuantidadeContribuintes(String quantidadeContribuintes) {
        this.quantidadeContribuintes = quantidadeContribuintes;
    }

    public String getNomeContribuinte() {
        return nomeContribuinte;
    }

    public void setNomeContribuinte(String nomeContribuinte) {
        this.nomeContribuinte = nomeContribuinte;
    }

    public String getTipoDoctoContribuinte() {
        return tipoDoctoContribuinte;
    }

    public void setTipoDoctoContribuinte(String tipoDoctoContribuinte) {
        this.tipoDoctoContribuinte = tipoDoctoContribuinte;
    }

    public String getCpfCnpjContribuinte() {
        return cpfCnpjContribuinte;
    }

    public void setCpfCnpjContribuinte(String cpfCnpjContribuinte) {
        this.cpfCnpjContribuinte = cpfCnpjContribuinte;
    }

    public String getDocumentoContribuinte() {
        return documentoContribuinte;
    }

    public void setDocumentoContribuinte(String documentoContribuinte) {
        this.documentoContribuinte = documentoContribuinte;
    }

    public String getEnderecoContribuinte() {
        return enderecoContribuinte;
    }

    public void setEnderecoContribuinte(String enderecoContribuinte) {
        this.enderecoContribuinte = enderecoContribuinte;
    }

    public String getCepContribuinte() {
        return cepContribuinte;
    }

    public void setCepContribuinte(String cepContribuinte) {
        this.cepContribuinte = cepContribuinte;
    }

    public String getCidadeContribuinte() {
        return cidadeContribuinte;
    }

    public void setCidadeContribuinte(String cidadeContribuinte) {
        this.cidadeContribuinte = cidadeContribuinte;
    }

    public String getUfContribuinte() {
        return ufContribuinte;
    }

    public void setUfContribuinte(String ufContribuinte) {
        this.ufContribuinte = ufContribuinte;
    }

    public String getCodigoCartorio() {
        return codigoCartorio;
    }

    public void setCodigoCartorio(String codigoCartorio) {
        this.codigoCartorio = codigoCartorio;
    }

    public String getProtocoloCartorio() {
        return protocoloCartorio;
    }

    public void setProtocoloCartorio(String protocoloCartorio) {
        this.protocoloCartorio = protocoloCartorio;
    }

    public String getTipoOcorrencia() {
        return tipoOcorrencia;
    }

    public void setTipoOcorrencia(String tipoOcorrencia) {
        this.tipoOcorrencia = tipoOcorrencia;
    }

    public String getDataProtocolo() {
        return dataProtocolo;
    }

    public void setDataProtocolo(String dataProtocolo) {
        this.dataProtocolo = dataProtocolo;
    }

    public String getValorCustasCartorio() {
        return valorCustasCartorio;
    }

    public void setValorCustasCartorio(String valorCustasCartorio) {
        this.valorCustasCartorio = valorCustasCartorio;
    }

    public String getDeclaracaoDoPortador() {
        return declaracaoDoPortador;
    }

    public void setDeclaracaoDoPortador(String declaracaoDoPortador) {
        this.declaracaoDoPortador = declaracaoDoPortador;
    }

    public String getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(String dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public String getCodigoIrregularidade() {
        return codigoIrregularidade;
    }

    public void setCodigoIrregularidade(String codigoIrregularidade) {
        this.codigoIrregularidade = codigoIrregularidade;
    }

    public String getBairroContribuinte() {
        return bairroContribuinte;
    }

    public void setBairroContribuinte(String bairroContribuinte) {
        this.bairroContribuinte = bairroContribuinte;
    }

    public String getValorCustasCartorioDistribuidor() {
        return valorCustasCartorioDistribuidor;
    }

    public void setValorCustasCartorioDistribuidor(String valorCustasCartorioDistribuidor) {
        this.valorCustasCartorioDistribuidor = valorCustasCartorioDistribuidor;
    }

    public String getRegistroDeDistribuicao() {
        return registroDeDistribuicao;
    }

    public void setRegistroDeDistribuicao(String registroDeDistribuicao) {
        this.registroDeDistribuicao = registroDeDistribuicao;
    }

    public String getValorGravacaoEletronica() {
        return valorGravacaoEletronica;
    }

    public void setValorGravacaoEletronica(String valorGravacaoEletronica) {
        this.valorGravacaoEletronica = valorGravacaoEletronica;
    }

    public String getNumOperacaoBanco() {
        return numOperacaoBanco;
    }

    public void setNumOperacaoBanco(String numOperacaoBanco) {
        this.numOperacaoBanco = numOperacaoBanco;
    }

    public String getNumContratoBanco() {
        return numContratoBanco;
    }

    public void setNumContratoBanco(String numContratoBanco) {
        this.numContratoBanco = numContratoBanco;
    }

    public String getNumParcelaContrato() {
        return numParcelaContrato;
    }

    public void setNumParcelaContrato(String numParcelaContrato) {
        this.numParcelaContrato = numParcelaContrato;
    }

    public String getTipoLetraCambio() {
        return tipoLetraCambio;
    }

    public void setTipoLetraCambio(String tipoLetraCambio) {
        this.tipoLetraCambio = tipoLetraCambio;
    }

    public String getComplementoCodigoIrregularidade() {
        return complementoCodigoIrregularidade;
    }

    public void setComplementoCodigoIrregularidade(String complementoCodigoIrregularidade) {
        this.complementoCodigoIrregularidade = complementoCodigoIrregularidade;
    }

    public String getProtestoMotivoFalencia() {
        return protestoMotivoFalencia;
    }

    public void setProtestoMotivoFalencia(String protestoMotivoFalencia) {
        this.protestoMotivoFalencia = protestoMotivoFalencia;
    }

    public String getInstrumentoProtesto() {
        return instrumentoProtesto;
    }

    public void setInstrumentoProtesto(String instrumentoProtesto) {
        this.instrumentoProtesto = instrumentoProtesto;
    }

    public String getValorDemaisDespesas() {
        return valorDemaisDespesas;
    }

    public void setValorDemaisDespesas(String valorDemaisDespesas) {
        this.valorDemaisDespesas = valorDemaisDespesas;
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
