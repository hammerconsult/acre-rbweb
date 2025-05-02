package br.com.webpublico.entidades;

import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Transporte de Saldo da Dívida Pública")
@Table(name = "TRANSPORTESALDODIVIDAPUB")
public class TransporteSaldoDividaPublica implements Serializable {

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
    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    private Date data;
    @Etiqueta("Dívida Pública")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private DividaPublica dividapublica;
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
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Intervalo")
    private Intervalo intervalo;
    @Pesquisavel
    @Tabelavel
    @Monetario
    @Etiqueta("Receita")
    private BigDecimal receita;
    @Pesquisavel
    @Tabelavel
    @Monetario
    @Etiqueta("Inscrição")
    private BigDecimal inscricao;
    @Pesquisavel
    @Tabelavel
    @Monetario
    @Etiqueta("Atualziação")
    private BigDecimal atualizacao;
    @Tabelavel
    @Monetario
    @Etiqueta("Transferência-CR")
    private BigDecimal transferenciaCredito;
    @Pesquisavel
    @Tabelavel
    @Monetario
    @Etiqueta("Pagamento")
    private BigDecimal pagamento;
    @Pesquisavel
    @Tabelavel
    @Monetario
    @Etiqueta("Apropriação")
    private BigDecimal apropriacao;
    @Pesquisavel
    @Tabelavel
    @Monetario
    @Etiqueta("Cancelamento")
    private BigDecimal cancelamento;
    @Tabelavel
    @Monetario
    @Etiqueta("Transferência-DB")
    private BigDecimal transferenciaDebito;
    @Transient
    @Monetario
    @Tabelavel
    @Etiqueta("Saldo Atual")
    private BigDecimal saldo;
    private BigDecimal empenhado;

    public TransporteSaldoDividaPublica() {
        inscricao = BigDecimal.ZERO;
        atualizacao = BigDecimal.ZERO;
        apropriacao = BigDecimal.ZERO;
        pagamento = BigDecimal.ZERO;
        receita = BigDecimal.ZERO;
        cancelamento = BigDecimal.ZERO;
        transferenciaCredito = BigDecimal.ZERO;
        transferenciaDebito = BigDecimal.ZERO;
        empenhado = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
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

    public DividaPublica getDividapublica() {
        return dividapublica;
    }

    public void setDividapublica(DividaPublica dividapublica) {
        this.dividapublica = dividapublica;
    }

    public FonteDeRecursos getFonte() {
        return fonte;
    }

    public void setFonte(FonteDeRecursos fonte) {
        this.fonte = fonte;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
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

    public BigDecimal getApropriacao() {
        return apropriacao;
    }

    public void setApropriacao(BigDecimal apropriacao) {
        this.apropriacao = apropriacao;
    }

    public BigDecimal getPagamento() {
        return pagamento;
    }

    public void setPagamento(BigDecimal pagamento) {
        this.pagamento = pagamento;
    }

    public BigDecimal getReceita() {
        return receita;
    }

    public void setReceita(BigDecimal receita) {
        this.receita = receita;
    }

    public BigDecimal getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(BigDecimal cancelamento) {
        this.cancelamento = cancelamento;
    }

    public BigDecimal getTransferenciaCredito() {
        return transferenciaCredito;
    }

    public void setTransferenciaCredito(BigDecimal transferenciaCredito) {
        this.transferenciaCredito = transferenciaCredito;
    }

    public BigDecimal getTransferenciaDebito() {
        return transferenciaDebito;
    }

    public void setTransferenciaDebito(BigDecimal transferenciaDebito) {
        this.transferenciaDebito = transferenciaDebito;
    }

    public BigDecimal getEmpenhado() {
        return empenhado;
    }

    public void setEmpenhado(BigDecimal empenhado) {
        this.empenhado = empenhado;
    }

    public AberturaFechamentoExercicio getAberturaFechamentoExercicio() {
        return aberturaFechamentoExercicio;
    }

    public void setAberturaFechamentoExercicio(AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
