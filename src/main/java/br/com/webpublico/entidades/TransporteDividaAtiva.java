package br.com.webpublico.entidades;

import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.NaturezaDividaAtivaCreditoReceber;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by renat on 21/06/2016.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Transporte de Saldo de Fechamento do Exercício")
public class TransporteDividaAtiva implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    @Tabelavel
    @Etiqueta("Órgão")
    private HierarquiaOrganizacional orgao;
    @Transient
    @Tabelavel
    @Etiqueta("Unidade")
    private HierarquiaOrganizacional unidade;
    @Etiqueta("Unidade Organizacional")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Conta")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private Conta conta;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Conta de Destinação")
    private ContaDeDestinacao contaDeDestinacao;
    @Etiqueta("Inscrição")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Tabelavel
    private BigDecimal inscricao;
    @Etiqueta("Atualização")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Tabelavel
    private BigDecimal atualizacao;
    @Etiqueta("Aumentativo")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Tabelavel
    private BigDecimal aumentativo;
    @Etiqueta("Recebimento")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Tabelavel
    private BigDecimal recebimento;
    @Etiqueta("Baixa")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Tabelavel
    private BigDecimal baixa;
    @Etiqueta("Diminutivo")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Tabelavel
    private BigDecimal diminutivo;
    @Transient
    @Monetario
    @Tabelavel
    @Etiqueta("Saldo")
    private BigDecimal saldo;
    @Etiqueta("Provisão")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    private BigDecimal provisao;
    @Etiqueta("Reversão")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    private BigDecimal reversao;
    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    private Date data;
    @Enumerated(EnumType.STRING)
    private NaturezaDividaAtivaCreditoReceber naturezaDividaAtiva;
    @Enumerated(EnumType.STRING)
    private Intervalo intervalo;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;

    public TransporteDividaAtiva() {
    }

    public TransporteDividaAtiva(HierarquiaOrganizacional orgao, HierarquiaOrganizacional unidade, UnidadeOrganizacional unidadeOrganizacional,
                                 Conta conta, BigDecimal inscricao, BigDecimal atualizacao, BigDecimal provisao,
                                 BigDecimal reversao, BigDecimal recebimento, BigDecimal baixa, BigDecimal aumentativo,
                                 BigDecimal diminutivo, Date data, AberturaFechamentoExercicio aberturaFechamentoExercicio,
                                 ContaDeDestinacao contaDeDestinacao, NaturezaDividaAtivaCreditoReceber naturezaDividaAtiva, Intervalo intervalo, BigDecimal saldo) {
        this.orgao = orgao;
        this.unidade = unidade;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.conta = conta;
        this.inscricao = inscricao;
        this.atualizacao = atualizacao;
        this.provisao = provisao;
        this.reversao = reversao;
        this.recebimento = recebimento;
        this.baixa = baixa;
        this.aumentativo = aumentativo;
        this.diminutivo = diminutivo;
        this.data = data;
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
        this.contaDeDestinacao = contaDeDestinacao;
        this.naturezaDividaAtiva = naturezaDividaAtiva;
        this.intervalo = intervalo;
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HierarquiaOrganizacional getOrgao() {
        return orgao;
    }

    public void setOrgao(HierarquiaOrganizacional orgao) {
        this.orgao = orgao;
    }

    public HierarquiaOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(HierarquiaOrganizacional unidade) {
        this.unidade = unidade;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public BigDecimal getInscricao() {
        return inscricao;
    }

    public void setInscricao(BigDecimal inscricao) {
        this.inscricao = inscricao;
    }

    public BigDecimal getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(BigDecimal atualizacao) {
        this.atualizacao = atualizacao;
    }

    public BigDecimal getProvisao() {
        return provisao;
    }

    public void setProvisao(BigDecimal provisao) {
        this.provisao = provisao;
    }

    public BigDecimal getReversao() {
        return reversao;
    }

    public void setReversao(BigDecimal reversao) {
        this.reversao = reversao;
    }

    public BigDecimal getRecebimento() {
        return recebimento;
    }

    public void setRecebimento(BigDecimal recebimento) {
        this.recebimento = recebimento;
    }

    public BigDecimal getBaixa() {
        return baixa;
    }

    public void setBaixa(BigDecimal baixa) {
        this.baixa = baixa;
    }

    public BigDecimal getAumentativo() {
        return aumentativo;
    }

    public void setAumentativo(BigDecimal aumentativo) {
        this.aumentativo = aumentativo;
    }

    public BigDecimal getDiminutivo() {
        return diminutivo;
    }

    public void setDiminutivo(BigDecimal diminutivo) {
        this.diminutivo = diminutivo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public AberturaFechamentoExercicio getAberturaFechamentoExercicio() {
        return aberturaFechamentoExercicio;
    }

    public void setAberturaFechamentoExercicio(AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public NaturezaDividaAtivaCreditoReceber getNaturezaDividaAtiva() {
        return naturezaDividaAtiva;
    }

    public void setNaturezaDividaAtiva(NaturezaDividaAtivaCreditoReceber naturezaDividaAtiva) {
        this.naturezaDividaAtiva = naturezaDividaAtiva;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
