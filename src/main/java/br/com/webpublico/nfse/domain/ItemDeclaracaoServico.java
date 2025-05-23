package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.*;
import br.com.webpublico.nfse.enums.TipoDeducaoNfse;
import br.com.webpublico.nfse.enums.TipoOperacaoNfse;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;


/**
 * A ItemDeclaracaoServico.
 */
@Entity
@Audited
public class ItemDeclaracaoServico extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal iss;
    private BigDecimal baseCalculo;
    private BigDecimal descontosIncondicionados;
    private BigDecimal descontosCondicionados;
    private BigDecimal quantidade;
    private BigDecimal valorServico;
    private String descricao;
    private String nomeServico;
    private BigDecimal aliquotaServico;
    private Boolean prestadoNoPais;
    private String observacoes;
    @ManyToOne
    private Servico servico;
    @ManyToOne
    private CNAE cnae;
    @ManyToOne
    private Cidade municipio;
    @ManyToOne
    private DeclaracaoPrestacaoServico declaracaoPrestacaoServico;
    @ManyToOne
    /** Apenas para declarações de serviços prestados fora do pais **/
    private Pais pais;
    @ManyToOne
    /** Apenas para declarações de instituição financeira **/
    private PlanoGeralContasComentado conta;
    /**
     * Apenas paraitem de dedução
     **/
    private BigDecimal deducoes;
    private Boolean deducao;
    @Enumerated(EnumType.STRING)
    private TipoDeducaoNfse tipoDeducao;
    @Enumerated(EnumType.STRING)
    private TipoOperacaoNfse tipoOperacao;
    private String numeroDocumentoFiscal;
    private String cpfCnpjDeducao;
    private BigDecimal valorDocumentoFiscal;
    private BigDecimal saldoAnterior;
    private BigDecimal credito;
    private BigDecimal debito;
    @OneToMany(mappedBy = "itemDeclaracaoServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalheItemDeclaracaoServico> detalhes;

    public ItemDeclaracaoServico() {
        detalhes = Lists.newArrayList();
    }

    public static ItemDeclaracaoServico copy(ItemDeclaracaoServico itemDeclaracaoServico, DeclaracaoPrestacaoServico declaracaoPrestacaoServico) {
        ItemDeclaracaoServico item = new ItemDeclaracaoServico();
        item.aliquotaServico = itemDeclaracaoServico.getAliquotaServico();
        item.baseCalculo = itemDeclaracaoServico.getBaseCalculo();
        item.descricao = itemDeclaracaoServico.getDescricao();
        item.declaracaoPrestacaoServico = declaracaoPrestacaoServico;
        item.deducoes = itemDeclaracaoServico.getDeducoes();
        item.descontosCondicionados = itemDeclaracaoServico.getDescontosCondicionados();
        item.descontosIncondicionados = itemDeclaracaoServico.getDescontosIncondicionados();
        item.iss = itemDeclaracaoServico.getIss();
        item.municipio = itemDeclaracaoServico.getMunicipio();
        item.nomeServico = itemDeclaracaoServico.getNomeServico();
        item.observacoes = itemDeclaracaoServico.getObservacoes();
        item.quantidade = itemDeclaracaoServico.getQuantidade();
        item.pais = itemDeclaracaoServico.getPais();
        item.prestadoNoPais = itemDeclaracaoServico.getPrestadoNoPais();
        item.servico = itemDeclaracaoServico.getServico();
        item.cnae = itemDeclaracaoServico.getCnae();
        item.deducao = itemDeclaracaoServico.getDeducao();
        item.tipoDeducao = itemDeclaracaoServico.getTipoDeducao();
        item.tipoOperacao = itemDeclaracaoServico.getTipoOperacao();
        item.numeroDocumentoFiscal = itemDeclaracaoServico.getNumeroDocumentoFiscal();
        item.cpfCnpjDeducao = itemDeclaracaoServico.getCpfCnpjDeducao();
        item.valorDocumentoFiscal = itemDeclaracaoServico.getValorDocumentoFiscal();
        item.detalhes = itemDeclaracaoServico.getDetalhes();
        item.setValorServico(itemDeclaracaoServico.getValorServico());
        return item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getDescontosIncondicionados() {
        return descontosIncondicionados;
    }

    public void setDescontosIncondicionados(BigDecimal descontosIncondicionados) {
        this.descontosIncondicionados = descontosIncondicionados;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public BigDecimal getDescontosCondicionados() {
        return descontosCondicionados;
    }

    public void setDescontosCondicionados(BigDecimal descontosCondicionados) {
        this.descontosCondicionados = descontosCondicionados;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getPrestadoNoPais() {
        return prestadoNoPais != null ? prestadoNoPais : Boolean.FALSE;
    }

    public void setPrestadoNoPais(Boolean prestadoNoPais) {
        this.prestadoNoPais = prestadoNoPais;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servicoPrestador) {
        this.servico = servicoPrestador;
    }

    public Cidade getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Cidade municipio) {
        this.municipio = municipio;
    }

    public DeclaracaoPrestacaoServico getDeclaracaoPrestacaoServico() {
        return declaracaoPrestacaoServico;
    }

    public void setDeclaracaoPrestacaoServico(DeclaracaoPrestacaoServico declaracaoPrestacaoServico) {
        this.declaracaoPrestacaoServico = declaracaoPrestacaoServico;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public BigDecimal getAliquotaServico() {
        return aliquotaServico;
    }

    public void setAliquotaServico(BigDecimal aliquotaServico) {
        this.aliquotaServico = aliquotaServico;
    }

    public PlanoGeralContasComentado getConta() {
        return conta;
    }

    public void setConta(PlanoGeralContasComentado conta) {
        this.conta = conta;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    @Override
    public String toString() {
        return "ItemDeclaracaoServico{" +
            "id=" + id +
            ", iss='" + iss + "'" +
            ", baseCalculo='" + baseCalculo + "'" +
            ", deducoes='" + deducoes + "'" +
            ", descontosIncondicionados='" + descontosIncondicionados + "'" +
            ", descontosCondicionados='" + descontosCondicionados + "'" +
            ", quantidade='" + quantidade + "'" +
            ", valorServico='" + valorServico + "'" +
            ", descricao='" + descricao + "'" +
            ", prestadoNoPais='" + prestadoNoPais + "'" +
            ", observacoes='" + observacoes + "'" +
            '}';
    }

    public BigDecimal getValorTotal() {
        if (quantidade != null) {
            valorServico.multiply(quantidade);
        }
        return valorServico;
    }

    public Boolean getDeducao() {
        return deducao != null ? deducao : false;
    }

    public void setDeducao(Boolean deducao) {
        this.deducao = deducao;
    }

    public TipoDeducaoNfse getTipoDeducao() {
        return tipoDeducao;
    }

    public void setTipoDeducao(TipoDeducaoNfse tipoDeducao) {
        this.tipoDeducao = tipoDeducao;
    }

    public TipoOperacaoNfse getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoNfse tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public String getNumeroDocumentoFiscal() {
        return numeroDocumentoFiscal;
    }

    public void setNumeroDocumentoFiscal(String numeroDocumentoFiscal) {
        this.numeroDocumentoFiscal = numeroDocumentoFiscal;
    }

    public String getCpfCnpjDeducao() {
        return cpfCnpjDeducao;
    }

    public void setCpfCnpjDeducao(String cpfCnpjDeducao) {
        this.cpfCnpjDeducao = cpfCnpjDeducao;
    }

    public BigDecimal getValorDocumentoFiscal() {
        return valorDocumentoFiscal;
    }

    public void setValorDocumentoFiscal(BigDecimal valorDocumentoFiscal) {
        this.valorDocumentoFiscal = valorDocumentoFiscal;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }

    public List<DetalheItemDeclaracaoServico> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(List<DetalheItemDeclaracaoServico> detalhes) {
        this.detalhes = detalhes;
    }

}
