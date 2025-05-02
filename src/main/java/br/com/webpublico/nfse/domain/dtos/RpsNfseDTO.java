package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.ExigibilidadeNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.ResponsavelRetencaoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.TipoRpsNfseDTO;
import br.com.webpublico.tributario.enumeration.RegimeTributarioNfseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class RpsNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {


    private Long id;
    private TipoRpsNfseDTO tipoRps;
    private String numero;
    private String numeroLote;
    private Long idNotaFiscal;
    private Long idLote;
    private String numeroNotaFiscal;
    private String serie;
    private Date dataEmissao;
    private String dataEmissaoString;
    private PrestadorServicoNfseDTO prestador;
    private ExigibilidadeNfseDTO naturezaOperacao;
    private Boolean optanteSimplesNacional;
    private Date competencia;
    private DadosPessoaisNfseDTO dadosPessoaisPrestador;
    private DadosPessoaisNfseDTO dadosPessoaisTomador;
    private List<br.com.webpublico.nfse.domain.dtos.ItemDeclaracaoServicoNfseDTO> itens;
    private TributosFederaisNfseDTO tributosFederais;
    private ResponsavelRetencaoNfseDTO responsavelRetencao;
    private Boolean issRetido;
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
    private String descriminacaoServico;
    private IntermediarioServicoNfseDTO intermediario;
    private NotaFiscalNfseDTO.ModalidadeEmissao modalidadeEmissao;
    private RegimeTributarioNfseDTO regimeTributario;
    private Boolean incentivoFiscal;
    private ConstrucaoCivilNfseDTO construcaoCivil;

    public RpsNfseDTO() {
        itens = Lists.newArrayList();
    }

    public RpsNfseDTO(Long id, String numero, String serie, Date dataEmissao, br.com.webpublico.nfse.domain.dtos.PrestadorServicoNfseDTO prestador) {
        this.id = id;
        this.numero = numero;
        this.serie = serie;
        this.dataEmissao = dataEmissao;
        this.prestador = prestador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoRpsNfseDTO getTipoRps() {
        return tipoRps;
    }

    public void setTipoRps(TipoRpsNfseDTO tipoRps) {
        this.tipoRps = tipoRps;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getDataEmissaoString() {
        return dataEmissaoString;
    }

    public void setDataEmissaoString(String dataEmissaoString) {
        this.dataEmissaoString = dataEmissaoString;
    }

    public String getDescriminacaoServico() {
        return descriminacaoServico;
    }

    public void setDescriminacaoServico(String descriminacaoServico) {
        this.descriminacaoServico = descriminacaoServico;
    }

    public PrestadorServicoNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PrestadorServicoNfseDTO prestador) {
        this.prestador = prestador;
    }

    public ExigibilidadeNfseDTO getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(ExigibilidadeNfseDTO naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }

    public Boolean getOptanteSimplesNacional() {
        return optanteSimplesNacional != null ? optanteSimplesNacional : false;
    }

    public void setOptanteSimplesNacional(Boolean optanteSimplesNacional) {
        this.optanteSimplesNacional = optanteSimplesNacional;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public DadosPessoaisNfseDTO getDadosPessoaisPrestador() {
        return dadosPessoaisPrestador;
    }

    public void setDadosPessoaisPrestador(DadosPessoaisNfseDTO dadosPessoaisPrestador) {
        this.dadosPessoaisPrestador = dadosPessoaisPrestador;
    }

    public DadosPessoaisNfseDTO getDadosPessoaisTomador() {
        return dadosPessoaisTomador;
    }

    public void setDadosPessoaisTomador(DadosPessoaisNfseDTO dadosPessoaisTomador) {
        this.dadosPessoaisTomador = dadosPessoaisTomador;
    }

    public List<br.com.webpublico.nfse.domain.dtos.ItemDeclaracaoServicoNfseDTO> getItens() {
        return itens;
    }

    public void setItens(List<br.com.webpublico.nfse.domain.dtos.ItemDeclaracaoServicoNfseDTO> itens) {
        this.itens = itens;
    }

    public TributosFederaisNfseDTO getTributosFederais() {
        return tributosFederais;
    }

    public void setTributosFederais(TributosFederaisNfseDTO tributosFederais) {
        this.tributosFederais = tributosFederais;
    }

    public Boolean getIssRetido() {
        return issRetido != null ? issRetido : false;
    }

    public void setIssRetido(Boolean issRetido) {
        this.issRetido = issRetido;
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

    public BigDecimal getDeducoesLegais() {
        return deducoesLegais;
    }

    public void setDeducoesLegais(BigDecimal deducoesLegais) {
        this.deducoesLegais = deducoesLegais;
    }

    public BigDecimal getDescontosIncondicionais() {
        return descontosIncondicionais;
    }

    public void setDescontosIncondicionais(BigDecimal descontosIncondicionais) {
        this.descontosIncondicionais = descontosIncondicionais;
    }

    public BigDecimal getDescontosCondicionais() {
        return descontosCondicionais;
    }

    public void setDescontosCondicionais(BigDecimal descontosCondicionais) {
        this.descontosCondicionais = descontosCondicionais;
    }

    public RegimeTributarioNfseDTO getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(RegimeTributarioNfseDTO regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    @JsonIgnore
    public BigDecimal getDescontos() {
        BigDecimal descontos = BigDecimal.ZERO;
        if (descontosIncondicionais != null) {
            descontos = descontos.add(descontosIncondicionais);
        }
        if (descontosCondicionais != null) {
            descontos = descontos.add(descontosCondicionais);
        }
        return descontos;
    }

    public BigDecimal getRetencoesFederais() {
        return retencoesFederais;
    }

    public void setRetencoesFederais(BigDecimal retencoesFederais) {
        this.retencoesFederais = retencoesFederais;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
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

    public String getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public Long getIdNotaFiscal() {
        return idNotaFiscal;
    }

    public void setIdNotaFiscal(Long idNotaFiscal) {
        this.idNotaFiscal = idNotaFiscal;
    }

    public Long getIdLote() {
        return idLote;
    }

    public void setIdLote(Long idLote) {
        this.idLote = idLote;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public ResponsavelRetencaoNfseDTO getResponsavelRetencao() {
        return responsavelRetencao;
    }

    public void setResponsavelRetencao(ResponsavelRetencaoNfseDTO responsavelRetencao) {
        this.responsavelRetencao = responsavelRetencao;
    }

    public IntermediarioServicoNfseDTO getIntermediario() {
        return intermediario;
    }

    public void setIntermediario(IntermediarioServicoNfseDTO intermediario) {
        this.intermediario = intermediario;
    }

    public NotaFiscalNfseDTO.ModalidadeEmissao getModalidadeEmissao() {
        return modalidadeEmissao;
    }

    public void setModalidadeEmissao(NotaFiscalNfseDTO.ModalidadeEmissao modalidadeEmissao) {
        this.modalidadeEmissao = modalidadeEmissao;
    }

    public Boolean getIncentivoFiscal() {
        return incentivoFiscal != null ? incentivoFiscal : Boolean.FALSE;
    }

    public void setIncentivoFiscal(Boolean incentivoFiscal) {
        this.incentivoFiscal = incentivoFiscal;
    }

    public ConstrucaoCivilNfseDTO getConstrucaoCivil() {
        return construcaoCivil;
    }

    public void setConstrucaoCivil(ConstrucaoCivilNfseDTO construcaoCivil) {
        this.construcaoCivil = construcaoCivil;
    }

    @Override
    public String toString() {
        return "Rps{" +
            "id=" + id +
            ", numero='" + numero + '\'' +
            ", serie='" + serie + '\'' +
            ", dataEmissao=" + dataEmissao +
            ", prestador=" + prestador +
            '}';
    }
}
