package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by renat on 22/06/2016.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Transporte de Saldo de Fechamento do Exercício")
public class TransporteExtra  implements Serializable {

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
    @Etiqueta("Valor")
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Tabelavel
    private BigDecimal valor;
    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    private Date data;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;

    public TransporteExtra() {
    }
    public TransporteExtra(HierarquiaOrganizacional orgao, HierarquiaOrganizacional unidade, UnidadeOrganizacional unidadeOrganizacional, Conta conta, FonteDeRecursos fonte, ContaDeDestinacao contaDeDestinacao, BigDecimal valor, Date data, AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.orgao = orgao;
        this.unidade = unidade;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.conta = conta;
        this.fonte = fonte;
        this.contaDeDestinacao = contaDeDestinacao;
        this.valor = valor;
        this.data = data;
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
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

    public FonteDeRecursos getFonte() {
        return fonte;
    }

    public void setFonte(FonteDeRecursos fonte) {
        this.fonte = fonte;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
}
