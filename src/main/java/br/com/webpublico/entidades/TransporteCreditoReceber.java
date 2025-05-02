package br.com.webpublico.entidades;

import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.NaturezaDividaAtivaCreditoReceber;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by renat on 20/06/2016.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Transporte de Saldo de Fechamento do Exercício")
public class TransporteCreditoReceber implements Serializable {

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
    @Enumerated(EnumType.STRING)
    @Etiqueta("Intervalo")
    @Tabelavel
    @Pesquisavel
    private Intervalo intervalo;
    @Etiqueta("Rec. Crédito")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    private BigDecimal reconhecimentoCredito;
    @Etiqueta("Dedução Rec.")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    private BigDecimal deducaoReconhecimento;
    @Etiqueta("Provisão")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    private BigDecimal provisaoPerdaCredito;
    @Etiqueta("Reversão")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    private BigDecimal reversaoProvisaoPerdaCredito;
    @Etiqueta("Baixa")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    private BigDecimal baixaReconhecimento;
    @Etiqueta("Baixa Dedução")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    private BigDecimal baixaDeducao;
    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    private Date data;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    @Etiqueta("Inscrição")
    @Tabelavel
    @Monetario
    @Transient
    private BigDecimal inscricao;
    @Etiqueta("Atualização")
    @Tabelavel
    @Monetario
    @Transient
    private BigDecimal atualizacao;
    @Etiqueta("Recebimento")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Tabelavel
    private BigDecimal recebimento;
    @Etiqueta("Baixa")
    @Tabelavel
    @Monetario
    @Transient
    private BigDecimal baixa;
    @Etiqueta("Aumentativo")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Tabelavel
    private BigDecimal aumentativo;
    @Etiqueta("Diminutivo")
    @Tabelavel
    @Monetario
    @Transient
    private BigDecimal diminutivo;
    @Transient
    @Monetario
    @Tabelavel
    @Etiqueta("Saldo")
    private BigDecimal saldo;
    @Enumerated(EnumType.STRING)
    private NaturezaDividaAtivaCreditoReceber naturezaCreditoReceber;



    public TransporteCreditoReceber() {
    }

    public TransporteCreditoReceber(HierarquiaOrganizacional orgao, HierarquiaOrganizacional unidade, UnidadeOrganizacional unidadeOrganizacional, Conta conta, BigDecimal reconhecimentoCredito, BigDecimal deducaoReconhecimento, BigDecimal provisaoPerdaCredito, BigDecimal reversaoProvisaoPerdaCredito, BigDecimal baixaReconhecimento, BigDecimal baixaDeducao, BigDecimal recebimento, BigDecimal atualizacao, Date data, AberturaFechamentoExercicio aberturaFechamentoExercicio, ContaDeDestinacao contaDeDestinacao, NaturezaDividaAtivaCreditoReceber naturezaCreditoReceber, Intervalo intervalo, BigDecimal saldo, BigDecimal aumentativo, BigDecimal diminutivo) {
        this.orgao = orgao;
        this.unidade = unidade;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.conta = conta;
        this.reconhecimentoCredito = reconhecimentoCredito;
        this.deducaoReconhecimento = deducaoReconhecimento;
        this.provisaoPerdaCredito = provisaoPerdaCredito;
        this.reversaoProvisaoPerdaCredito = reversaoProvisaoPerdaCredito;
        this.baixaReconhecimento = baixaReconhecimento;
        this.baixaDeducao = baixaDeducao;
        this.recebimento = recebimento;
        this.atualizacao = atualizacao;
        this.data = data;
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
        if (reconhecimentoCredito != null && deducaoReconhecimento != null) {
            inscricao = reconhecimentoCredito.subtract(deducaoReconhecimento);
        }
        if (provisaoPerdaCredito != null && reversaoProvisaoPerdaCredito != null) {
            this.atualizacao = this.atualizacao.add(provisaoPerdaCredito.subtract(reversaoProvisaoPerdaCredito));
        }
        if (baixaReconhecimento != null && baixaDeducao != null) {
            baixa = baixaReconhecimento.subtract(baixaDeducao);
        }
        this.saldo = saldo;
        this.aumentativo = aumentativo;
        this.diminutivo = diminutivo;
        this.contaDeDestinacao = contaDeDestinacao;
        this.naturezaCreditoReceber = naturezaCreditoReceber;
        this.intervalo = intervalo;
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

    public BigDecimal getReconhecimentoCredito() {
        return reconhecimentoCredito;
    }

    public void setReconhecimentoCredito(BigDecimal reconhecimentoCredito) {
        this.reconhecimentoCredito = reconhecimentoCredito;
    }

    public BigDecimal getDeducaoReconhecimento() {
        return deducaoReconhecimento;
    }

    public void setDeducaoReconhecimento(BigDecimal deducaoReconhecimento) {
        this.deducaoReconhecimento = deducaoReconhecimento;
    }

    public BigDecimal getProvisaoPerdaCredito() {
        return provisaoPerdaCredito;
    }

    public void setProvisaoPerdaCredito(BigDecimal provisaoPerdaCredito) {
        this.provisaoPerdaCredito = provisaoPerdaCredito;
    }

    public BigDecimal getReversaoProvisaoPerdaCredito() {
        return reversaoProvisaoPerdaCredito;
    }

    public void setReversaoProvisaoPerdaCredito(BigDecimal reversaoProvisaoPerdaCredito) {
        this.reversaoProvisaoPerdaCredito = reversaoProvisaoPerdaCredito;
    }

    public BigDecimal getBaixaReconhecimento() {
        return baixaReconhecimento;
    }

    public void setBaixaReconhecimento(BigDecimal baixaReconhecimento) {
        this.baixaReconhecimento = baixaReconhecimento;
    }

    public BigDecimal getBaixaDeducao() {
        return baixaDeducao;
    }

    public void setBaixaDeducao(BigDecimal baixaDeducao) {
        this.baixaDeducao = baixaDeducao;
    }

    public BigDecimal getRecebimento() {
        return recebimento;
    }

    public void setRecebimento(BigDecimal recebimento) {
        this.recebimento = recebimento;
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

    public BigDecimal getBaixa() {
        return baixa;
    }

    public void setBaixa(BigDecimal baixa) {
        this.baixa = baixa;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
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

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public NaturezaDividaAtivaCreditoReceber getNaturezaCreditoReceber() {
        return naturezaCreditoReceber;
    }

    public void setNaturezaCreditoReceber(NaturezaDividaAtivaCreditoReceber naturezaCreditoReceber) {
        this.naturezaCreditoReceber = naturezaCreditoReceber;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
    }

    @Override
    public String toString() {
        return "Unidade: " + unidadeOrganizacional + "; Conta: " + conta.toString();
    }
}
