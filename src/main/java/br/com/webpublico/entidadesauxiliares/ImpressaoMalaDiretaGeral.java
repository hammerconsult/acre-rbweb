package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

public class ImpressaoMalaDiretaGeral {

    private Long id;
    private Long idDam;
    private Long idPessoa;
    private String nomeContribuinte;
    private String cpfContribuinte;
    private String logradouroContribuinte;
    private String bairroContribuinte;
    private String numeroEnderecoContribuinte;
    private String cepContribuinte;
    private String cidadeContribuinte;
    private String ufContribuinte;
    private String tipoEnderecoContribuinte;
    private String numeroCadastro;
    private String logradouroCadastro;
    private String bairroCadastro;
    private String numeroEnderecoCadastro;
    private String complementoEnderecoCadastro;
    private String cepCadastro;
    private String loteCadastroImobiliario;
    private String setorCadastroImobiliario;
    private String quadraCadastroImobiliario;
    private String situacaoCadastralCadastroEconomico;
    private Date dataAberturaCadastroEconomico;
    private String razaoSocialCadastroEconomico;
    private String nomeFantasiaCadastroEconomico;
    private Date vencimentoDam;
    private String numeroDam;
    private BigDecimal valorOriginalDam;
    private BigDecimal jutosDam;
    private BigDecimal multaDam;
    private BigDecimal correcaoDam;
    private BigDecimal honorariosDam;
    private BigDecimal descontoDam;
    private String codigoBarrasDam;
    private String codigoBarrasDigitosDam;
    private String dividasDam;
    private String texto;
    private String cabecalho;
    private String emailContribuinte;
    private String qrCodePix;

    public ImpressaoMalaDiretaGeral() {
    }

    public String getNomeContribuinte() {
        return nomeContribuinte;
    }

    public void setNomeContribuinte(String nomeContribuinte) {
        this.nomeContribuinte = nomeContribuinte;
    }

    public String getCpfContribuinte() {
        return cpfContribuinte;
    }

    public void setCpfContribuinte(String cpfContribuinte) {
        this.cpfContribuinte = cpfContribuinte;
    }

    public String getLogradouroContribuinte() {
        return logradouroContribuinte;
    }

    public void setLogradouroContribuinte(String logradouroContribuinte) {
        this.logradouroContribuinte = logradouroContribuinte;
    }

    public String getBairroContribuinte() {
        return bairroContribuinte;
    }

    public void setBairroContribuinte(String bairroContribuinte) {
        this.bairroContribuinte = bairroContribuinte;
    }

    public String getNumeroEnderecoContribuinte() {
        return numeroEnderecoContribuinte;
    }

    public void setNumeroEnderecoContribuinte(String numeroEnderecoContribuinte) {
        this.numeroEnderecoContribuinte = numeroEnderecoContribuinte;
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

    public String getTipoEnderecoContribuinte() {
        return tipoEnderecoContribuinte;
    }

    public void setTipoEnderecoContribuinte(String tipoEnderecoContribuinte) {
        this.tipoEnderecoContribuinte = tipoEnderecoContribuinte;
    }

    public String getNumeroCadastro() {
        return numeroCadastro;
    }

    public void setNumeroCadastro(String numeroCadastro) {
        this.numeroCadastro = numeroCadastro;
    }

    public String getLogradouroCadastro() {
        return logradouroCadastro;
    }

    public void setLogradouroCadastro(String logradouroCadastro) {
        this.logradouroCadastro = logradouroCadastro;
    }

    public String getBairroCadastro() {
        return bairroCadastro;
    }

    public void setBairroCadastro(String bairroCadastro) {
        this.bairroCadastro = bairroCadastro;
    }

    public String getNumeroEnderecoCadastro() {
        return numeroEnderecoCadastro;
    }

    public void setNumeroEnderecoCadastro(String numeroEnderecoCadastro) {
        this.numeroEnderecoCadastro = numeroEnderecoCadastro;
    }

    public String getComplementoEnderecoCadastro() {
        return complementoEnderecoCadastro;
    }

    public void setComplementoEnderecoCadastro(String complementoEnderecoCadastro) {
        this.complementoEnderecoCadastro = complementoEnderecoCadastro;
    }

    public String getCepCadastro() {
        return cepCadastro;
    }

    public void setCepCadastro(String cepCadastro) {
        this.cepCadastro = cepCadastro;
    }

    public String getLoteCadastroImobiliario() {
        return loteCadastroImobiliario;
    }

    public void setLoteCadastroImobiliario(String loteCadastroImobiliario) {
        this.loteCadastroImobiliario = loteCadastroImobiliario;
    }

    public String getSetorCadastroImobiliario() {
        return setorCadastroImobiliario;
    }

    public void setSetorCadastroImobiliario(String setorCadastroImobiliario) {
        this.setorCadastroImobiliario = setorCadastroImobiliario;
    }

    public String getQuadraCadastroImobiliario() {
        return quadraCadastroImobiliario;
    }

    public void setQuadraCadastroImobiliario(String quadraCadastroImobiliario) {
        this.quadraCadastroImobiliario = quadraCadastroImobiliario;
    }

    public Date getVencimentoDam() {
        return vencimentoDam;
    }

    public void setVencimentoDam(Date vencimentoDam) {
        this.vencimentoDam = vencimentoDam;
    }

    public String getNumeroDam() {
        return numeroDam;
    }

    public void setNumeroDam(String numeroDam) {
        this.numeroDam = numeroDam;
    }

    public BigDecimal getValorOriginalDam() {
        return valorOriginalDam;
    }

    public void setValorOriginalDam(BigDecimal valorOriginalDam) {
        this.valorOriginalDam = valorOriginalDam;
    }

    public BigDecimal getJutosDam() {
        return jutosDam;
    }

    public void setJutosDam(BigDecimal jutosDam) {
        this.jutosDam = jutosDam;
    }

    public BigDecimal getMultaDam() {
        return multaDam;
    }

    public void setMultaDam(BigDecimal multaDam) {
        this.multaDam = multaDam;
    }

    public BigDecimal getCorrecaoDam() {
        return correcaoDam;
    }

    public void setCorrecaoDam(BigDecimal correcaoDam) {
        this.correcaoDam = correcaoDam;
    }

    public BigDecimal getHonorariosDam() {
        return honorariosDam;
    }

    public void setHonorariosDam(BigDecimal honorariosDam) {
        this.honorariosDam = honorariosDam;
    }

    public BigDecimal getDescontoDam() {
        return descontoDam;
    }

    public void setDescontoDam(BigDecimal descontoDam) {
        this.descontoDam = descontoDam;
    }

    public String getCodigoBarrasDam() {
        return codigoBarrasDam;
    }

    public void setCodigoBarrasDam(String codigoBarrasDam) {
        this.codigoBarrasDam = codigoBarrasDam;
    }

    public String getCodigoBarrasDigitosDam() {
        return codigoBarrasDigitosDam;
    }

    public void setCodigoBarrasDigitosDam(String codigoBarrasDigitosDam) {
        this.codigoBarrasDigitosDam = codigoBarrasDigitosDam;
    }

    public String getDividasDam() {
        return dividasDam;
    }

    public void setDividasDam(String dividasDam) {
        this.dividasDam = dividasDam;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdDam() {
        return idDam;
    }

    public void setIdDam(Long idDam) {
        this.idDam = idDam;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getCabecalho() {
        return cabecalho;
    }

    public void setCabecalho(String cabecalho) {
        this.cabecalho = cabecalho;
    }

    public String getEmailContribuinte() {
        return emailContribuinte;
    }

    public void setEmailContribuinte(String emailContribuinte) {
        this.emailContribuinte = emailContribuinte;
    }

    public String getSituacaoCadastralCadastroEconomico() {
        return situacaoCadastralCadastroEconomico;
    }

    public void setSituacaoCadastralCadastroEconomico(String situacaoCadastralCadastroEconomico) {
        this.situacaoCadastralCadastroEconomico = situacaoCadastralCadastroEconomico;
    }

    public Date getDataAberturaCadastroEconomico() {
        return dataAberturaCadastroEconomico;
    }

    public void setDataAberturaCadastroEconomico(Date dataAberturaCadastroEconomico) {
        this.dataAberturaCadastroEconomico = dataAberturaCadastroEconomico;
    }

    public String getRazaoSocialCadastroEconomico() {
        return razaoSocialCadastroEconomico;
    }

    public void setRazaoSocialCadastroEconomico(String razaoSocialCadastroEconomico) {
        this.razaoSocialCadastroEconomico = razaoSocialCadastroEconomico;
    }

    public String getNomeFantasiaCadastroEconomico() {
        return nomeFantasiaCadastroEconomico;
    }

    public void setNomeFantasiaCadastroEconomico(String nomeFantasiaCadastroEconomico) {
        this.nomeFantasiaCadastroEconomico = nomeFantasiaCadastroEconomico;
    }

    public String getQrCodePix() {
        return qrCodePix;
    }

    public void setQrCodePix(String qrCodePix) {
        this.qrCodePix = qrCodePix;
    }
}
