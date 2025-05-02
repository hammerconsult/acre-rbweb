package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.ConfiguracaoTransporteSaldoSubContaDestino;
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
public class TransporteSaldoFinanceiro implements Serializable {

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
    private SubConta conta;
    @Etiqueta("Fonte")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private FonteDeRecursos fonte;
    @Etiqueta("Conta de Destinação de Recurso")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    @Etiqueta("Crédito")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    private BigDecimal credito;
    @Etiqueta("Débito")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    private BigDecimal debito;
    @Transient
    @Monetario
    @Tabelavel
    @Etiqueta("Saldo")
    private BigDecimal saldo;
    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    private Date data;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    @ManyToOne
    private ConfiguracaoTransporteSaldoSubContaDestino configuracaoDestino;
    @Transient
    private Boolean erroDuranteProcessamento;
    @Transient
    private String mensagemDeErro;

    public TransporteSaldoFinanceiro() {
        erroDuranteProcessamento = false;
    }

    public TransporteSaldoFinanceiro(HierarquiaOrganizacional orgao, HierarquiaOrganizacional unidade, UnidadeOrganizacional unidadeOrganizacional, SubConta conta, FonteDeRecursos fonte, ContaDeDestinacao contaDeDestinacao, BigDecimal credito, BigDecimal debito, Date data, AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.orgao = orgao;
        this.unidade = unidade;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.conta = conta;
        this.fonte = fonte;
        this.credito = credito;
        this.debito = debito;
        this.data = data;
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
        this.saldo = credito.subtract(debito);
        this.contaDeDestinacao = contaDeDestinacao;
        erroDuranteProcessamento = false;
    }

    public TransporteSaldoFinanceiro(HierarquiaOrganizacional orgao, HierarquiaOrganizacional unidade, UnidadeOrganizacional unidadeOrganizacional, SubConta conta, FonteDeRecursos fonte, ContaDeDestinacao contaDeDestinacao, BigDecimal credito, BigDecimal debito, Date data, AberturaFechamentoExercicio aberturaFechamentoExercicio, Boolean erroDuranteProcessamento, String mensagemDeErro) {
        this.orgao = orgao;
        this.unidade = unidade;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.conta = conta;
        this.fonte = fonte;
        this.credito = credito;
        this.debito = debito;
        this.data = data;
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
        this.saldo = credito.subtract(debito);
        this.erroDuranteProcessamento = erroDuranteProcessamento;
        this.contaDeDestinacao = contaDeDestinacao;
        this.mensagemDeErro = mensagemDeErro;
    }

    public TransporteSaldoFinanceiro(ConfiguracaoTransporteSaldoSubContaDestino configuracaoDestino, Date data, AberturaFechamentoExercicio aberturaFechamentoExercicio, HierarquiaOrganizacional orgao, BigDecimal credito, BigDecimal debito) {
        this.orgao = orgao;
        this.configuracaoDestino = configuracaoDestino;
        this.unidade = configuracaoDestino.getHierarquiaOrganizacional();
        this.unidadeOrganizacional = configuracaoDestino.getUnidadeOrganizacional();
        this.conta = configuracaoDestino.getSubConta();
        this.fonte = configuracaoDestino.getContaDeDestinacao().getFonteDeRecursos();
        this.credito = credito;
        this.debito = debito;
        this.data = data;
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
        this.saldo = credito.subtract(debito);
        this.erroDuranteProcessamento = false;
        this.contaDeDestinacao = configuracaoDestino.getContaDeDestinacao();
        this.mensagemDeErro = "";
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

    public SubConta getConta() {
        return conta;
    }

    public void setConta(SubConta conta) {
        this.conta = conta;
    }

    public FonteDeRecursos getFonte() {
        return fonte;
    }

    public void setFonte(FonteDeRecursos fonte) {
        this.fonte = fonte;
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

    public ConfiguracaoTransporteSaldoSubContaDestino getConfiguracaoDestino() {
        return configuracaoDestino;
    }

    public void setConfiguracaoDestino(ConfiguracaoTransporteSaldoSubContaDestino configuracaoDestino) {
        this.configuracaoDestino = configuracaoDestino;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Boolean getErroDuranteProcessamento() {
        return erroDuranteProcessamento != null ? erroDuranteProcessamento : Boolean.FALSE;
    }

    public void setErroDuranteProcessamento(Boolean erroDuranteProcessamento) {
        this.erroDuranteProcessamento = erroDuranteProcessamento;
    }

    public String getMensagemDeErro() {
        return mensagemDeErro;
    }

    public void setMensagemDeErro(String mensagemDeErro) {
        this.mensagemDeErro = mensagemDeErro;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    @Override
    public String toString() {
        return "Unidade: " + unidadeOrganizacional + "; Conta: " + conta.toString() + "; Conta de Destinação de Recurso: " + contaDeDestinacao.toString() + ";  Valor: " + Util.formataValor(credito.subtract(debito));
    }
}
