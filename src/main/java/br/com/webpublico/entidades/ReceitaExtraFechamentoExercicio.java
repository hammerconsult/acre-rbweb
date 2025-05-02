package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoReceitaExtra;
import br.com.webpublico.enums.TipoConsignacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by mateus on 27/03/18.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Receita Extraorçamentária de Fechamento do Exercício")
@Table(name = "RECEITAEXTFECHAMENTOEXERC")
public class ReceitaExtraFechamentoExercicio extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    @Tabelavel
    @Etiqueta("Unidade")
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Etiqueta("Número Original")
    @Tabelavel
    private String numeroOriginal;
    @Etiqueta("Unidade Organizacional")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Conta")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Conta conta;
    @Etiqueta("Fonte de Recursos")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @Etiqueta("Conta de Destinação de Recurso")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    @Etiqueta("Valor")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Monetario
    private BigDecimal valor;
    @Etiqueta("Saldo")
    @Obrigatorio
    @Monetario
    private BigDecimal saldo;
    @Etiqueta("Valor Estornado")
    @Obrigatorio
    @Monetario
    private BigDecimal valorEstornado;
    @Etiqueta("Tipo Consignação")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoConsignacao tipoConsignacao;
    @Obrigatorio
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoReceitaExtra situacaoReceitaExtra;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe")
    private ClasseCredor classeCredor;
    @Obrigatorio
    @Etiqueta("Conta Financeira")
    @ManyToOne
    private SubConta subConta;
    private String complementoHistorico;
    private String historicoNota;
    private String historicoRazao;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;

    public ReceitaExtraFechamentoExercicio() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
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

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getValorEstornado() {
        return valorEstornado;
    }

    public void setValorEstornado(BigDecimal valorEstornado) {
        this.valorEstornado = valorEstornado;
    }

    public TipoConsignacao getTipoConsignacao() {
        return tipoConsignacao;
    }

    public void setTipoConsignacao(TipoConsignacao tipoConsignacao) {
        this.tipoConsignacao = tipoConsignacao;
    }

    public SituacaoReceitaExtra getSituacaoReceitaExtra() {
        return situacaoReceitaExtra;
    }

    public void setSituacaoReceitaExtra(SituacaoReceitaExtra situacaoReceitaExtra) {
        this.situacaoReceitaExtra = situacaoReceitaExtra;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public AberturaFechamentoExercicio getAberturaFechamentoExercicio() {
        return aberturaFechamentoExercicio;
    }

    public void setAberturaFechamentoExercicio(AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public String getNumeroOriginal() {
        return numeroOriginal;
    }

    public void setNumeroOriginal(String numeroOriginal) {
        this.numeroOriginal = numeroOriginal;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
