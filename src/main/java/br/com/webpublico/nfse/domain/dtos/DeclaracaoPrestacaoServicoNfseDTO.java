package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.ExigibilidadeNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.OrigemEmissaoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.ResponsavelRetencaoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.SituacaoDeclaracaoNfseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class DeclaracaoPrestacaoServicoNfseDTO implements NfseDTO {

    private Long id;
    private ExigibilidadeNfseDTO naturezaOperacao;
    private Boolean optanteSimplesNacional;
    private Date competencia;
    private IntermediarioServicoNfseDTO intermediario;
    private ConstrucaoCivilNfseDTO construcaoCivil;
    private PagamentoNfseDTO condicaoPagamento;
    private List<ItemDeclaracaoServicoNfseDTO> itens;
    private TributosFederaisNfseDTO tributosFederais;
    private Boolean issRetido;
    private ResponsavelRetencaoNfseDTO responsavelRetencao;
    private CancelamentoNfseDTO cancelamento;
    private SituacaoDeclaracaoNfseDTO situacao;
    private OrigemEmissaoNfseDTO origemEmissao;
    private DadosPessoaisNfseDTO dadosPessoaisPrestador;
    private DadosPessoaisNfseDTO dadosPessoaisTomador;
    private BigDecimal totalServicos;
    private BigDecimal deducoesLegais;
    private BigDecimal baseCalculo;
    private BigDecimal issCalculado;
    private BigDecimal issPagar;
    private NotaFiscalNfseDTO.ModalidadeEmissao modalidade;


    public DeclaracaoPrestacaoServicoNfseDTO() {
        itens = Lists.newArrayList();
    }

    public DeclaracaoPrestacaoServicoNfseDTO(NotaFiscalNfseDTO notaFiscal) {
        this.issRetido = notaFiscal.getDeclaracaoPrestacaoServico().getIssRetido();
        this.responsavelRetencao = notaFiscal.getDeclaracaoPrestacaoServico().getResponsavelRetencao();
        this.competencia = new Date();
        this.naturezaOperacao = ExigibilidadeNfseDTO.TRIBUTACAO_MUNICIPAL;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIssRetido() {
        return issRetido != null ? issRetido : false;
    }

    public void setIssRetido(Boolean issRetido) {
        this.issRetido = issRetido;
    }

    public ResponsavelRetencaoNfseDTO getResponsavelRetencao() {
        return responsavelRetencao;
    }

    public void setResponsavelRetencao(ResponsavelRetencaoNfseDTO responsavelRetencao) {
        this.responsavelRetencao = responsavelRetencao;
    }

    public ExigibilidadeNfseDTO getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(ExigibilidadeNfseDTO naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }

    public Boolean getOptanteSimplesNacional() {
        return optanteSimplesNacional != null ? optanteSimplesNacional : Boolean.FALSE;
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

    public IntermediarioServicoNfseDTO getIntermediario() {
        return intermediario;
    }

    public void setIntermediario(IntermediarioServicoNfseDTO intermediario) {
        this.intermediario = intermediario;
    }

    public ConstrucaoCivilNfseDTO getConstrucaoCivil() {
        return construcaoCivil;
    }

    public void setConstrucaoCivil(ConstrucaoCivilNfseDTO construcaoCivil) {
        this.construcaoCivil = construcaoCivil;
    }

    public PagamentoNfseDTO getCondicaoPagamento() {
        return condicaoPagamento;
    }

    public void setCondicaoPagamento(PagamentoNfseDTO pagamento) {
        this.condicaoPagamento = pagamento;
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

    public CancelamentoNfseDTO getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(CancelamentoNfseDTO cancelamento) {
        this.cancelamento = cancelamento;
    }

    public SituacaoDeclaracaoNfseDTO getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoDeclaracaoNfseDTO situacao) {
        this.situacao = situacao;
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

    public OrigemEmissaoNfseDTO getOrigemEmissao() {
        return origemEmissao;
    }

    public void setOrigemEmissao(OrigemEmissaoNfseDTO origemEmissao) {
        this.origemEmissao = origemEmissao;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getDeducoesLegais() {
        return deducoesLegais;
    }

    public void setDeducoesLegais(BigDecimal deducoesLegais) {
        this.deducoesLegais = deducoesLegais;
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

    public NotaFiscalNfseDTO.ModalidadeEmissao getModalidade() {
        return modalidade;
    }

    public void setModalidade(NotaFiscalNfseDTO.ModalidadeEmissao modalidade) {
        this.modalidade = modalidade;
    }
}
