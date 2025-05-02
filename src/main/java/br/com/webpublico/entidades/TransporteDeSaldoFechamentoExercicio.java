package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 28/11/14
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Transporte de Saldo de Fechamento do Exercício")
@Table(name = "TRANSPORTEDESALDOFECHEXE")
public class TransporteDeSaldoFechamentoExercicio implements Serializable {

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
    @Etiqueta("Crédito")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Monetario
    private BigDecimal credito;
    @Etiqueta("Débito")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Monetario
    private BigDecimal debito;
    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    private Date data;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Balancete")
    private TipoBalancete tipoBalancete;

    public TransporteDeSaldoFechamentoExercicio() {
    }

    public TransporteDeSaldoFechamentoExercicio(HierarquiaOrganizacional orgao, HierarquiaOrganizacional unidade, UnidadeOrganizacional subordinada, Conta conta, BigDecimal credito, BigDecimal debito, AberturaFechamentoExercicio selecionado) {
        this.orgao = orgao;
        this.unidade = unidade;
        this.unidadeOrganizacional = subordinada;
        this.conta = conta;
        this.credito = credito;
        this.debito = debito;
        this.aberturaFechamentoExercicio = selecionado;
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

    public TipoBalancete getTipoBalancete() {
        return tipoBalancete;
    }

    public void setTipoBalancete(TipoBalancete tipoBalancete) {
        this.tipoBalancete = tipoBalancete;
    }

    @Override
    public String toString() {
        return "Unidade: " + unidadeOrganizacional + "; Conta: " + conta.toString() + ";  Valor: " + Util.formataValor(credito.subtract(debito));
    }
}
