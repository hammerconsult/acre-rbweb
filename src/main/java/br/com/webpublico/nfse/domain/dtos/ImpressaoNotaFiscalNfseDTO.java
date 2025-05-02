package br.com.webpublico.nfse.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImpressaoNotaFiscalNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private Long id;
    private Long numero;
    private String numeroRPS;
    private Date emissao;
    private String emissaoFormatada;
    private Date emissaoRPS;
    private String serieRPS;
    private String tipoRPS;
    private String prestadorNomeFantasia;
    private String prestadorRazaoSocial;
    private String prestadorCnpjCpf;
    private String prestadorCadastroGeral;
    private String regimeFiscalDesc;
    private String prestadorLogradouro;
    private String prestadorBairro;
    private String prestadorComplemento;
    private String prestadorCidade;
    private String prestadorUF;
    private String prestadorCEP;
    private String prestadorEmail;
    private String prestadorTelefone;
    private String prestadorNumero;
    private String prestadorInscricaoMunicipal;
    private String prestadorInscricaoEstadual;
    private String prestadorTipoIssqn;
    private String tomadorRazaoSocial;
    private String tomadorCadastroGeral;
    private String tomadorInscricaoEstadual;
    private String tomadorCnpjCpf;
    private String tomadorLogradouro;
    private String tomadorBairro;
    private String tomadorComplemento;
    private String tomadorCidade;
    private String tomadorUF;
    private String tomadorCEP;
    private String tomadorNumero;
    private String tomadorInscricaoMunicipal;
    private String tomadorEmail;
    private String codigoListaServico;
    private String codigoListaServicoDesc;
    private String codigoCnae;
    private String mesCompetencia;
    private String anoCompetencia;
    private String codigoObra;
    private String incorporacao;
    private String art;
    private String situacaoNotaDesc;
    private String naturezaOperacaoDesc;
    private String localPrestacao;
    private String discriminacao;
    private boolean retencao;
    private ImpressaoNotaFiscalNfseItemDTO item;
    private ImpressaoNotaFiscalNfseImpostoDTO issqn;
    private ImpressaoNotaFiscalNfseImpostoDTO cofins;
    private ImpressaoNotaFiscalNfseImpostoDTO cpp;
    private ImpressaoNotaFiscalNfseImpostoDTO csll;
    private ImpressaoNotaFiscalNfseImpostoDTO inss;
    private ImpressaoNotaFiscalNfseImpostoDTO irrf;
    private ImpressaoNotaFiscalNfseImpostoDTO pis;
    private ImpressaoNotaFiscalNfseImpostoDTO outrasRetencoes;
    private ImpressaoNotaFiscalNfseImpostoDTO totalTributos;
    private String observacao;
    private BigDecimal totalServicos;
    private BigDecimal baseCalculo;
    private BigDecimal descontoIncondicionado;
    private BigDecimal descontoCondicionado;
    private BigDecimal deducoes;
    private BigDecimal totalNota;
    private BigDecimal valorLiquido;
    private String codigoVerificacao;
    private Boolean homologacao;
    private String descricaoAlteracao;
    private String responsavelIss;
    private Date vencimentoIss;
    private String mensagemRodape;
    private Long numeroCartaCorrecao;
    private Date emissaoCartaCorrecao;
    private String discriminacaoCartaCorrecao;

    public ImpressaoNotaFiscalNfseDTO() {
        item = new br.com.webpublico.nfse.domain.dtos.ImpressaoNotaFiscalNfseItemDTO();
        issqn = new br.com.webpublico.nfse.domain.dtos.ImpressaoNotaFiscalNfseImpostoDTO();
        cofins = new br.com.webpublico.nfse.domain.dtos.ImpressaoNotaFiscalNfseImpostoDTO();
        cpp = new br.com.webpublico.nfse.domain.dtos.ImpressaoNotaFiscalNfseImpostoDTO();
        csll = new br.com.webpublico.nfse.domain.dtos.ImpressaoNotaFiscalNfseImpostoDTO();
        inss = new br.com.webpublico.nfse.domain.dtos.ImpressaoNotaFiscalNfseImpostoDTO();
        irrf = new br.com.webpublico.nfse.domain.dtos.ImpressaoNotaFiscalNfseImpostoDTO();
        pis = new br.com.webpublico.nfse.domain.dtos.ImpressaoNotaFiscalNfseImpostoDTO();
        outrasRetencoes = new br.com.webpublico.nfse.domain.dtos.ImpressaoNotaFiscalNfseImpostoDTO();
        totalTributos = new br.com.webpublico.nfse.domain.dtos.ImpressaoNotaFiscalNfseImpostoDTO();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public String getEmissaoFormatada() {
        return emissaoFormatada;
    }

    public void setEmissaoFormatada(String emissaoFormatada) {
        this.emissaoFormatada = emissaoFormatada;
    }

    public String getPrestadorNomeFantasia() {
        return prestadorNomeFantasia;
    }

    public void setPrestadorNomeFantasia(String prestadorNomeFantasia) {
        this.prestadorNomeFantasia = prestadorNomeFantasia;
    }

    public String getPrestadorRazaoSocial() {
        return prestadorRazaoSocial;
    }

    public void setPrestadorRazaoSocial(String prestadorRazaoSocial) {
        this.prestadorRazaoSocial = prestadorRazaoSocial;
    }

    public String getPrestadorCnpjCpf() {
        return prestadorCnpjCpf;
    }

    public void setPrestadorCnpjCpf(String prestadorCnpjCpf) {
        this.prestadorCnpjCpf = prestadorCnpjCpf;
    }

    public String getPrestadorCadastroGeral() {
        return prestadorCadastroGeral;
    }

    public void setPrestadorCadastroGeral(String prestadorCadastroGeral) {
        this.prestadorCadastroGeral = prestadorCadastroGeral;
    }

    public String getRegimeFiscalDesc() {
        return regimeFiscalDesc;
    }

    public void setRegimeFiscalDesc(String regimeFiscalDesc) {
        this.regimeFiscalDesc = regimeFiscalDesc;
    }

    public String getPrestadorLogradouro() {
        return prestadorLogradouro;
    }

    public void setPrestadorLogradouro(String prestadorLogradouro) {
        this.prestadorLogradouro = prestadorLogradouro;
    }

    public String getPrestadorBairro() {
        return prestadorBairro;
    }

    public void setPrestadorBairro(String prestadorBairro) {
        this.prestadorBairro = prestadorBairro;
    }

    public String getPrestadorComplemento() {
        return prestadorComplemento;
    }

    public void setPrestadorComplemento(String prestadorComplemento) {
        this.prestadorComplemento = prestadorComplemento;
    }

    public String getPrestadorCidade() {
        return prestadorCidade;
    }

    public void setPrestadorCidade(String prestadorCidade) {
        this.prestadorCidade = prestadorCidade;
    }

    public String getPrestadorUF() {
        return prestadorUF;
    }

    public void setPrestadorUF(String prestadorUF) {
        this.prestadorUF = prestadorUF;
    }

    public String getPrestadorCEP() {
        return prestadorCEP;
    }

    public void setPrestadorCEP(String prestadorCEP) {
        this.prestadorCEP = prestadorCEP;
    }

    public String getTomadorRazaoSocial() {
        return tomadorRazaoSocial;
    }

    public void setTomadorRazaoSocial(String tomadorRazaoSocial) {
        this.tomadorRazaoSocial = tomadorRazaoSocial;
    }

    public String getTomadorCadastroGeral() {
        return tomadorCadastroGeral;
    }

    public void setTomadorCadastroGeral(String tomadorCadastroGeral) {
        this.tomadorCadastroGeral = tomadorCadastroGeral;
    }

    public String getTomadorInscricaoEstadual() {
        return tomadorInscricaoEstadual;
    }

    public void setTomadorInscricaoEstadual(String tomadorInscricaoEstadual) {
        this.tomadorInscricaoEstadual = tomadorInscricaoEstadual;
    }

    public String getTomadorCnpjCpf() {
        return tomadorCnpjCpf;
    }

    public void setTomadorCnpjCpf(String tomadorCnpjCpf) {
        this.tomadorCnpjCpf = tomadorCnpjCpf;
    }

    public String getTomadorLogradouro() {
        return tomadorLogradouro;
    }

    public void setTomadorLogradouro(String tomadorLogradouro) {
        this.tomadorLogradouro = tomadorLogradouro;
    }

    public String getTomadorBairro() {
        return tomadorBairro;
    }

    public void setTomadorBairro(String tomadorBairro) {
        this.tomadorBairro = tomadorBairro;
    }

    public String getTomadorComplemento() {
        return tomadorComplemento;
    }

    public void setTomadorComplemento(String tomadorComplemento) {
        this.tomadorComplemento = tomadorComplemento;
    }

    public String getTomadorCidade() {
        return tomadorCidade;
    }

    public void setTomadorCidade(String tomadorCidade) {
        this.tomadorCidade = tomadorCidade;
    }

    public String getTomadorUF() {
        return tomadorUF;
    }

    public void setTomadorUF(String tomadorUF) {
        this.tomadorUF = tomadorUF;
    }

    public String getTomadorCEP() {
        return tomadorCEP;
    }

    public void setTomadorCEP(String tomadorCEP) {
        this.tomadorCEP = tomadorCEP;
    }

    public String getCodigoListaServico() {
        return codigoListaServico;
    }

    public void setCodigoListaServico(String codigoListaServico) {
        this.codigoListaServico = codigoListaServico;
    }

    public String getCodigoListaServicoDesc() {
        return codigoListaServicoDesc;
    }

    public void setCodigoListaServicoDesc(String codigoListaServicoDesc) {
        this.codigoListaServicoDesc = codigoListaServicoDesc;
    }

    public String getCodigoCnae() {
        return codigoCnae;
    }

    public void setCodigoCnae(String codigoCnae) {
        this.codigoCnae = codigoCnae;
    }

    public String getMesCompetencia() {
        return mesCompetencia;
    }

    public void setMesCompetencia(String mesCompetencia) {
        this.mesCompetencia = mesCompetencia;
    }

    public String getAnoCompetencia() {
        return anoCompetencia;
    }

    public void setAnoCompetencia(String anoCompetencia) {
        this.anoCompetencia = anoCompetencia;
    }

    public String getCodigoObra() {
        return codigoObra;
    }

    public void setCodigoObra(String codigoObra) {
        this.codigoObra = codigoObra;
    }

    public String getIncorporacao() {
        return incorporacao;
    }

    public void setIncorporacao(String incorporacao) {
        this.incorporacao = incorporacao;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getSituacaoNotaDesc() {
        return situacaoNotaDesc;
    }

    public void setSituacaoNotaDesc(String situacaoNotaDesc) {
        this.situacaoNotaDesc = situacaoNotaDesc;
    }

    public String getNaturezaOperacaoDesc() {
        return naturezaOperacaoDesc;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getDescontoIncondicionado() {
        return descontoIncondicionado;
    }

    public void setDescontoIncondicionado(BigDecimal descontoIncondicionado) {
        this.descontoIncondicionado = descontoIncondicionado;
    }

    public BigDecimal getDescontoCondicionado() {
        return descontoCondicionado;
    }

    public void setDescontoCondicionado(BigDecimal descontoCondicionado) {
        this.descontoCondicionado = descontoCondicionado;
    }

    public BigDecimal getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getTotalNota() {
        return totalNota;
    }

    public void setTotalNota(BigDecimal totalNota) {
        this.totalNota = totalNota;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public void setNaturezaOperacaoDesc(String naturezaOperacaoDesc) {
        this.naturezaOperacaoDesc = naturezaOperacaoDesc;
    }

    public String getLocalPrestacao() {
        return localPrestacao;
    }

    public void setLocalPrestacao(String localPrestacao) {
        this.localPrestacao = localPrestacao;
    }

    public String getDiscriminacao() {
        return discriminacao;
    }

    public void setDiscriminacao(String discriminacao) {
        this.discriminacao = discriminacao;
    }

    public ImpressaoNotaFiscalNfseItemDTO getItem() {
        return item;
    }

    public void setItem(ImpressaoNotaFiscalNfseItemDTO item) {
        this.item = item;
    }

    public ImpressaoNotaFiscalNfseImpostoDTO getIssqn() {
        return issqn;
    }

    public void setIssqn(ImpressaoNotaFiscalNfseImpostoDTO issqn) {
        this.issqn = issqn;
    }

    public ImpressaoNotaFiscalNfseImpostoDTO getCofins() {
        return cofins;
    }

    public void setCofins(ImpressaoNotaFiscalNfseImpostoDTO cofins) {
        this.cofins = cofins;
    }

    public ImpressaoNotaFiscalNfseImpostoDTO getCpp() {
        return cpp;
    }

    public void setCpp(ImpressaoNotaFiscalNfseImpostoDTO cpp) {
        this.cpp = cpp;
    }

    public ImpressaoNotaFiscalNfseImpostoDTO getCsll() {
        return csll;
    }

    public void setCsll(ImpressaoNotaFiscalNfseImpostoDTO csll) {
        this.csll = csll;
    }

    public ImpressaoNotaFiscalNfseImpostoDTO getInss() {
        return inss;
    }

    public void setInss(ImpressaoNotaFiscalNfseImpostoDTO inss) {
        this.inss = inss;
    }

    public ImpressaoNotaFiscalNfseImpostoDTO getIrrf() {
        return irrf;
    }

    public void setIrrf(ImpressaoNotaFiscalNfseImpostoDTO irrf) {
        this.irrf = irrf;
    }

    public ImpressaoNotaFiscalNfseImpostoDTO getPis() {
        return pis;
    }

    public void setPis(ImpressaoNotaFiscalNfseImpostoDTO pis) {
        this.pis = pis;
    }

    public ImpressaoNotaFiscalNfseImpostoDTO getOutrasRetencoes() {
        return outrasRetencoes;
    }

    public void setOutrasRetencoes(ImpressaoNotaFiscalNfseImpostoDTO outrasRetencoes) {
        this.outrasRetencoes = outrasRetencoes;
    }

    public ImpressaoNotaFiscalNfseImpostoDTO getTotalTributos() {
        return totalTributos;
    }

    public void setTotalTributos(ImpressaoNotaFiscalNfseImpostoDTO totalTributos) {
        this.totalTributos = totalTributos;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public String getNumeroRPS() {
        return numeroRPS;
    }

    public void setNumeroRPS(String numeroRPS) {
        this.numeroRPS = numeroRPS;
    }

    public Date getEmissaoRPS() {
        return emissaoRPS;
    }

    public void setEmissaoRPS(Date emissaoRPS) {
        this.emissaoRPS = emissaoRPS;
    }

    public String getSerieRPS() {
        return serieRPS;
    }

    public void setSerieRPS(String serieRPS) {
        this.serieRPS = serieRPS;
    }

    public String getTipoRPS() {
        return tipoRPS;
    }

    public void setTipoRPS(String tipoRPS) {
        this.tipoRPS = tipoRPS;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public String getPrestadorEmail() {
        return prestadorEmail;
    }

    public void setPrestadorEmail(String prestadorEmail) {
        this.prestadorEmail = prestadorEmail;
    }

    public String getPrestadorTelefone() {
        return prestadorTelefone;
    }

    public void setPrestadorTelefone(String prestadorTelefone) {
        this.prestadorTelefone = prestadorTelefone;
    }

    public Boolean getHomologacao() {
        return homologacao;
    }

    public void setHomologacao(Boolean homologacao) {
        this.homologacao = homologacao;
    }

    public String getDescricaoAlteracao() {
        return descricaoAlteracao;
    }

    public void setDescricaoAlteracao(String descricaoAlteracao) {
        this.descricaoAlteracao = descricaoAlteracao;
    }

    public String getResponsavelIss() {
        return responsavelIss;
    }

    public void setResponsavelIss(String responsavelIss) {
        this.responsavelIss = responsavelIss;
    }

    public Date getVencimentoIss() {
        return vencimentoIss;
    }

    public void setVencimentoIss(Date vencimentoIss) {
        this.vencimentoIss = vencimentoIss;
    }

    public String getPrestadorNumero() {
        return prestadorNumero;
    }

    public void setPrestadorNumero(String prestadorNumero) {
        this.prestadorNumero = prestadorNumero;
    }

    public String getTomadorNumero() {
        return tomadorNumero;
    }

    public void setTomadorNumero(String tomadorNumero) {
        this.tomadorNumero = tomadorNumero;
    }


    public String getPrestadorInscricaoMunicipal() {
        return prestadorInscricaoMunicipal;
    }

    public void setPrestadorInscricaoMunicipal(String prestadorInscricaoMunicipal) {
        this.prestadorInscricaoMunicipal = prestadorInscricaoMunicipal;
    }

    public String getTomadorInscricaoMunicipal() {
        return tomadorInscricaoMunicipal;
    }

    public void setTomadorInscricaoMunicipal(String tomadorInscricaoMunicipal) {
        this.tomadorInscricaoMunicipal = tomadorInscricaoMunicipal;
    }

    public String getTomadorEmail() {
        return tomadorEmail;
    }

    public void setTomadorEmail(String tomadorEmail) {
        this.tomadorEmail = tomadorEmail;
    }

    public String getPrestadorInscricaoEstadual() {
        return prestadorInscricaoEstadual;
    }

    public void setPrestadorInscricaoEstadual(String prestadorInscricaoEstadual) {
        this.prestadorInscricaoEstadual = prestadorInscricaoEstadual;
    }

    public String getPrestadorTipoIssqn() {
        return prestadorTipoIssqn;
    }

    public void setPrestadorTipoIssqn(String prestadorTipoIssqn) {
        this.prestadorTipoIssqn = prestadorTipoIssqn;
    }

    public String getMensagemRodape() {
        return mensagemRodape;
    }

    public void setMensagemRodape(String mensagemRodape) {
        this.mensagemRodape = mensagemRodape;
    }

    public boolean isRetencao() {
        return retencao;
    }

    public void setRetencao(boolean retencao) {
        this.retencao = retencao;
    }

    public Long getNumeroCartaCorrecao() {
        return numeroCartaCorrecao;
    }

    public void setNumeroCartaCorrecao(Long numeroCartaCorrecao) {
        this.numeroCartaCorrecao = numeroCartaCorrecao;
    }

    public Date getEmissaoCartaCorrecao() {
        return emissaoCartaCorrecao;
    }

    public void setEmissaoCartaCorrecao(Date emissaoCartaCorrecao) {
        this.emissaoCartaCorrecao = emissaoCartaCorrecao;
    }

    public String getDiscriminacaoCartaCorrecao() {
        return discriminacaoCartaCorrecao;
    }

    public void setDiscriminacaoCartaCorrecao(String discriminacaoCartaCorrecao) {
        this.discriminacaoCartaCorrecao = discriminacaoCartaCorrecao;
    }
}
