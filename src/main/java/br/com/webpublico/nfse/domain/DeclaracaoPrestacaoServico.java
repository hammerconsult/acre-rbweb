
package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CNAE;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.enums.*;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * A DeclaracaoPrestacaoServico.
 */
@Tabelavel
@Entity
@Audited
public class DeclaracaoPrestacaoServico extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Natureza de Operação")
    @Enumerated(EnumType.STRING)
    private Exigibilidade naturezaOperacao;
    private Boolean optanteSimplesNacional;
    @Obrigatorio
    @Etiqueta("Competência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date competencia;
    @ManyToOne(cascade = CascadeType.ALL)
    private DadosPessoaisNfse dadosPessoaisPrestador;
    @ManyToOne(cascade = CascadeType.ALL)
    private DadosPessoaisNfse dadosPessoaisTomador;
    @ManyToOne(cascade = CascadeType.ALL)
    @Tabelavel
    private IntermediarioServico intermediario;
    @ManyToOne(cascade = CascadeType.ALL)
    private ConstrucaoCivil construcaoCivil;
    @ManyToOne(cascade = CascadeType.ALL)
    private PagamentoNfse condicaoPagamento;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "declaracaoPrestacaoServico")
    private List<ItemDeclaracaoServico> itens;
    @ManyToOne(cascade = CascadeType.ALL)
    private TributosFederais tributosFederais;
    @Tabelavel
    private Boolean issRetido;
    @Enumerated(EnumType.STRING)
    private ResponsavelRetencao responsavelRetencao;
    @Enumerated(EnumType.STRING)
    private TipoDocumentoNfse tipoDocumento;
    @Enumerated(EnumType.STRING)
    private OrigemEmissaoNfse origemEmissao;
    @Obrigatorio
    @Etiqueta("Situação")
    @Pesquisavel
    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Tabelavel
    private SituacaoNota situacao;
    @Obrigatorio
    @Etiqueta("Modalidade")
    @Enumerated(value = EnumType.STRING)
    private ModalidadeEmissao modalidade;
    @Tabelavel
    private BigDecimal totalServicos;
    @Tabelavel
    private BigDecimal deducoesLegais;
    @Tabelavel
    private BigDecimal descontosIncondicionais;
    @Tabelavel
    private BigDecimal descontosCondicionais;
    @Tabelavel
    private BigDecimal retencoesFederais;
    @Tabelavel
    private BigDecimal baseCalculo;
    @Tabelavel
    private BigDecimal issCalculado;
    @Tabelavel
    private BigDecimal issPagar;
    @Tabelavel
    private BigDecimal valorLiquido;
    @ManyToOne
    private CNAE cnae;
    private BigDecimal totalNota;

    public DeclaracaoPrestacaoServico(Long id) {
        this.id = id;
    }

    public DeclaracaoPrestacaoServico() {
        this.itens = Lists.newArrayList();
        this.totalServicos = BigDecimal.ZERO;
        this.deducoesLegais = BigDecimal.ZERO;
        this.descontosIncondicionais = BigDecimal.ZERO;
        this.descontosCondicionais = BigDecimal.ZERO;
        this.baseCalculo = BigDecimal.ZERO;
        this.issCalculado = BigDecimal.ZERO;
        this.issPagar = BigDecimal.ZERO;
        this.valorLiquido = BigDecimal.ZERO;
        this.retencoesFederais = BigDecimal.ZERO;
        this.situacao = SituacaoNota.EMITIDA;

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

    public ResponsavelRetencao getResponsavelRetencao() {
        return responsavelRetencao;
    }

    public void setResponsavelRetencao(ResponsavelRetencao responsavelRetencao) {
        this.responsavelRetencao = responsavelRetencao;
    }

    public Exigibilidade getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(Exigibilidade naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }

    public Boolean getOptanteSimplesNacional() {
        return optanteSimplesNacional;
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

    public IntermediarioServico getIntermediario() {
        return intermediario;
    }

    public void setIntermediario(IntermediarioServico intermediario) {
        this.intermediario = intermediario;
    }

    public ConstrucaoCivil getConstrucaoCivil() {
        return construcaoCivil;
    }

    public void setConstrucaoCivil(ConstrucaoCivil construcaoCivil) {
        this.construcaoCivil = construcaoCivil;
    }

    public PagamentoNfse getCondicaoPagamento() {
        return condicaoPagamento;
    }

    public void setCondicaoPagamento(PagamentoNfse pagamento) {
        this.condicaoPagamento = pagamento;
    }

    public List<ItemDeclaracaoServico> getItens() {
        return itens;
    }

    public void setItens(List<ItemDeclaracaoServico> itens) {
        this.itens = itens;
    }

    public TributosFederais getTributosFederais() {
        return tributosFederais;
    }

    public void setTributosFederais(TributosFederais tributosFederais) {
        this.tributosFederais = tributosFederais;
    }

    public DadosPessoaisNfse getDadosPessoaisTomador() {
        return dadosPessoaisTomador;
    }

    public void setDadosPessoaisTomador(DadosPessoaisNfse dadosPessoaisTomador) {
        this.dadosPessoaisTomador = dadosPessoaisTomador;
    }

    public DadosPessoaisNfse getDadosPessoaisPrestador() {
        return dadosPessoaisPrestador;
    }

    public void setDadosPessoaisPrestador(DadosPessoaisNfse dadosPessoaisPrestador) {
        this.dadosPessoaisPrestador = dadosPessoaisPrestador;
    }

    public TipoDocumentoNfse getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoNfse tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public SituacaoNota getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoNota situacao) {
        this.situacao = situacao;
    }

    public ModalidadeEmissao getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeEmissao modalidade) {
        this.modalidade = modalidade;
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

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public OrigemEmissaoNfse getOrigemEmissao() {
        return origemEmissao;
    }

    public void setOrigemEmissao(OrigemEmissaoNfse origemEmissaoNfse) {
        this.origemEmissao = origemEmissaoNfse;
    }

    public BigDecimal getTotalNota() {
        return totalNota;
    }

    public void setTotalNota(BigDecimal totalNota) {
        this.totalNota = totalNota;
    }

    @Transient
    public String getIssRetidoSimNao() {
        if (this.getIssRetido()) {
            return "Sim";
        }
        return "Não";
    }

    @Override
    public String toString() {
        return id + "";
    }

    public BigDecimal getDescontos() {
        return getDescontosCondicionais().add(getDescontosIncondicionais());
    }
}
