package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.SituacaoDeclaracaoNfseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotaFiscalNfseDTO implements NfseDTO {
    public static byte tcSim = new Integer(1).byteValue();
    public static byte tcNao = new Integer(2).byteValue();

    private Long id;
    private Long numero;
    private String codigoVerificacao;
    private DeclaracaoPrestacaoServicoNfseDTO declaracaoPrestacaoServico;
    private RpsNfseDTO rps;
    private Date emissao;
    private Date ultimaEmissao;
    private SituacaoDeclaracaoNfseDTO situacao;
    private String emails;
    private String informacoesAdicionais;
    private ModalidadeEmissao modalidade;
    private BigDecimal totalServicos;
    private BigDecimal totalNota;
    private BigDecimal deducoesLegais;
    private BigDecimal descontosIncondicionais;
    private BigDecimal descontosCondicionais;
    private BigDecimal retencoesFederais;
    private BigDecimal baseCalculo;
    private BigDecimal issCalculado;
    private BigDecimal issPagar;
    private BigDecimal valorLiquido;
    private PrestadorServicoNfseDTO prestador;
    private Boolean tomadorHabitual;
    private TomadorServicoDTO tomador;
    private String descriminacaoServico;
    private String chaveAcesso;
    private BigDecimal aliquotaServico;
    private Boolean homologacao;
    private Boolean substitutoTributario;
    private Boolean enviouPorEmail;

    public NotaFiscalNfseDTO() {
        this.totalServicos = BigDecimal.ZERO;
        this.totalNota = BigDecimal.ZERO;
        this.deducoesLegais = BigDecimal.ZERO;
        this.descontosIncondicionais = BigDecimal.ZERO;
        this.descontosCondicionais = BigDecimal.ZERO;
        this.retencoesFederais = BigDecimal.ZERO;
        this.baseCalculo = BigDecimal.ZERO;
        this.issCalculado = BigDecimal.ZERO;
        this.issPagar = BigDecimal.ZERO;
        this.valorLiquido = BigDecimal.ZERO;
        this.aliquotaServico = BigDecimal.ZERO;
        this.homologacao = false;
        this.tomadorHabitual = Boolean.FALSE;
        this.substitutoTributario = Boolean.FALSE;
        this.enviouPorEmail = Boolean.FALSE;
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

    public String getCodigoVerificacao() {
        return codigoVerificacao != null ? codigoVerificacao.toUpperCase() : " ";
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public DeclaracaoPrestacaoServicoNfseDTO getDeclaracaoPrestacaoServico() {
        return declaracaoPrestacaoServico;
    }

    public void setDeclaracaoPrestacaoServico(DeclaracaoPrestacaoServicoNfseDTO declaracaoPrestacaoServico) {
        this.declaracaoPrestacaoServico = declaracaoPrestacaoServico;
    }

    public SituacaoDeclaracaoNfseDTO getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoDeclaracaoNfseDTO situacao) {
        this.situacao = situacao;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Date getUltimaEmissao() {
        return ultimaEmissao;
    }

    public void setUltimaEmissao(Date ultimaEmissao) {
        this.ultimaEmissao = ultimaEmissao;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getTotalNota() {
        return totalNota;
    }

    public void setTotalNota(BigDecimal totalNota) {
        this.totalNota = totalNota;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getDeducoesLegais() {
        return deducoesLegais;
    }

    public void setDeducoesLegais(BigDecimal deducoesLegais) {
        this.deducoesLegais = deducoesLegais;
    }

    public BigDecimal getDescontosIncondicionais() {
        return descontosIncondicionais != null ? descontosIncondicionais : BigDecimal.ZERO;
    }

    public void setDescontosIncondicionais(BigDecimal descontosIncondicionais) {
        this.descontosIncondicionais = descontosIncondicionais;
    }

    public BigDecimal getDescontosCondicionais() {
        return descontosCondicionais != null ? descontosCondicionais : BigDecimal.ZERO;
    }

    public void setDescontosCondicionais(BigDecimal descontosCondicionais) {
        this.descontosCondicionais = descontosCondicionais;
    }

    public BigDecimal getIssCalculado() {
        return issCalculado;
    }

    public void setIssCalculado(BigDecimal issCalculado) {
        this.issCalculado = issCalculado;
    }

    public BigDecimal getIssPagar() {
        return issPagar;
    }

    public void setIssPagar(BigDecimal issPagar) {
        this.issPagar = issPagar;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public ModalidadeEmissao getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeEmissao modalidade) {
        this.modalidade = modalidade;
    }

    public RpsNfseDTO getRps() {
        return rps;
    }

    public void setRps(RpsNfseDTO rps) {
        this.rps = rps;
    }

    public BigDecimal getRetencoesFederais() {
        return retencoesFederais;
    }

    public void setRetencoesFederais(BigDecimal retencoesFederais) {
        this.retencoesFederais = retencoesFederais;
    }

    public BigDecimal getAliquotaServico() {
        return aliquotaServico;
    }

    public void setAliquotaServico(BigDecimal aliquotaServico) {
        this.aliquotaServico = aliquotaServico;
    }

    @JsonIgnore
    public boolean tomadorJaCadastrado() {
        return this.getDeclaracaoPrestacaoServico() != null &&
            this.getTomador() != null &&
            this.getTomador() != null &&
            this.getTomador().getId() != null;
    }

    public PrestadorServicoNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PrestadorServicoNfseDTO prestador) {
        this.prestador = prestador;
    }

    public Boolean getTomadorHabitual() {
        return tomadorHabitual;
    }

    public void setTomadorHabitual(Boolean tomadorHabitual) {
        this.tomadorHabitual = tomadorHabitual;
    }

    public TomadorServicoDTO getTomador() {
        return tomador;
    }

    public void setTomador(TomadorServicoDTO tomador) {
        this.tomador = tomador;
    }

    public String getInformacoesAdicionais() {
        return informacoesAdicionais;
    }

    public void setInformacoesAdicionais(String informacoesAdicionais) {
        this.informacoesAdicionais = informacoesAdicionais;
    }

    public boolean intermediarioJaCadastrado() {
        return this.getDeclaracaoPrestacaoServico() != null &&
            this.getDeclaracaoPrestacaoServico().getIntermediario() != null &&
            this.getDeclaracaoPrestacaoServico().getIntermediario() != null &&
            this.getDeclaracaoPrestacaoServico().getIntermediario().getId() != null;
    }

    public String getDescriminacaoServico() {
        return descriminacaoServico;
    }

    public void setDescriminacaoServico(String descriminacaoServico) {
        this.descriminacaoServico = descriminacaoServico;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public boolean getHomologacao() {
        return homologacao;
    }

    public void setHomologacao(boolean homologacao) {
        this.homologacao = homologacao;
    }

    public Boolean getSubstitutoTributario() {
        return substitutoTributario;
    }

    public void setSubstitutoTributario(Boolean substitutoTributario) {
        this.substitutoTributario = substitutoTributario;
    }

    public Boolean getEnviouPorEmail() {
        return enviouPorEmail;
    }

    public void setEnviouPorEmail(Boolean enviouPorEmail) {
        this.enviouPorEmail = enviouPorEmail;
    }

    @JsonIgnore
    public BigDecimal getDescontos() {
        BigDecimal descontos = BigDecimal.ZERO;
        if (descontosCondicionais != null) {
            descontos = descontos.add(descontosCondicionais);
        }
        if (descontosCondicionais != null) {
            descontos = descontos.add(descontosIncondicionais);
        }
        return descontos;
    }

    public enum ModalidadeEmissao {
        IDENTIFICADO, SEM_CPF, NAO_IDENTIFICADO;
    }

    public NotaFiscalSearchDTO toSearch() {
        NotaFiscalSearchDTO dto = new NotaFiscalSearchDTO();
        dto.setId(getId());
        dto.setIdPrestador(getPrestador().getId());
        if (getTomador() != null && getTomador().getId() != null) {
            dto.setIdTomador(getTomador().getId());
            dto.setNomeTomador(getTomador().getDadosPessoais().getNomeRazaoSocial());
            dto.setCpfCnpjTomador(getTomador().getDadosPessoais().getCpfCnpj());
        }
        if (getRps() != null) {
            dto.setIdRps(getRps().getId());
            dto.setCodigoRps(getRps().getNumero());
            dto.setSerieRps(getRps().getSerie());
            dto.setTipoRps(getRps().getTipoRps().getDescricao());
        }
        dto.setNumero(getNumero());
        dto.setEmissao(getEmissao());
        dto.setNomePrestador(getPrestador().getPessoa().getDadosPessoais().getNomeRazaoSocial());
        dto.setCpfCnpjPrestador(getPrestador().getPessoa().getDadosPessoais().getCpfCnpj());
        dto.setSituacao(getSituacao().name());
        dto.setModalidade(getModalidade().name());
        dto.setIssRetido(getDeclaracaoPrestacaoServico().getIssRetido());
        dto.setTotalServicos(getTotalServicos());
        dto.setDeducoes(getDeducoesLegais());
        dto.setBaseCalculo(getBaseCalculo());
        dto.setIss(getIssPagar());
        dto.setIssCalculado(getIssCalculado());
        dto.setDiscriminacaoServico(getDescriminacaoServico());
        return dto;
    }

}
