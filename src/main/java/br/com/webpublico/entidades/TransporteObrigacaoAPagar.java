package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Transporte de Obrigação a Pagar")
public class TransporteObrigacaoAPagar implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    @Tabelavel
    @Etiqueta("Órgão")
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Transient
    @Tabelavel
    @Etiqueta("Unidade")
    private HierarquiaOrganizacional unidade;
    @Etiqueta("Unidade Organizacional")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Função")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private Funcao funcao;
    @Etiqueta("SubFunção")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private SubFuncao subFuncao;
    @Etiqueta("Programa")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private ProgramaPPA programaPPA;
    @Etiqueta("Projeto/Atividade")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private AcaoPPA projetoAtividade;
    @Etiqueta("SubProjeto/Atividade")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private SubAcaoPPA subProjetoAtividade;
    @Etiqueta("Conta")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private Conta conta;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @Etiqueta("Conta de Destinação de Recurso")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    @Etiqueta("Obrigação a Pagar")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    private ObrigacaoAPagar obrigacaoAPagar;
    @Etiqueta("Valor")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Monetario
    private BigDecimal valor;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;

    public TransporteObrigacaoAPagar() {
    }

    public TransporteObrigacaoAPagar(HierarquiaOrganizacional orgao, HierarquiaOrganizacional unidade, Funcao funcao, SubFuncao subFuncao, ProgramaPPA programaPPA, AcaoPPA acaoPPA, SubAcaoPPA subAcaoPPA, Conta conta, FonteDeRecursos fonteDeRecursos, AberturaFechamentoExercicio selecionado, ObrigacaoAPagar obrigacaoAPagar, BigDecimal valor, ContaDeDestinacao contaDeDestinacao) {
        this.hierarquiaOrganizacional = orgao;
        this.unidade = unidade;
        this.unidadeOrganizacional = unidade.getSubordinada();
        this.funcao = funcao;
        this.subFuncao = subFuncao;
        this.programaPPA = programaPPA;
        this.projetoAtividade = acaoPPA;
        this.subProjetoAtividade = subAcaoPPA;
        this.conta = conta;
        this.fonteDeRecursos = fonteDeRecursos;
        this.valor = valor;
        this.aberturaFechamentoExercicio = selecionado;
        this.obrigacaoAPagar = obrigacaoAPagar;
        this.contaDeDestinacao = contaDeDestinacao;
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

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public AcaoPPA getProjetoAtividade() {
        return projetoAtividade;
    }

    public void setProjetoAtividade(AcaoPPA projetoAtividade) {
        this.projetoAtividade = projetoAtividade;
    }

    public SubAcaoPPA getSubProjetoAtividade() {
        return subProjetoAtividade;
    }

    public void setSubProjetoAtividade(SubAcaoPPA subProjetoAtividade) {
        this.subProjetoAtividade = subProjetoAtividade;
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

    public ObrigacaoAPagar getObrigacaoAPagar() {
        return obrigacaoAPagar;
    }

    public void setObrigacaoAPagar(ObrigacaoAPagar obrigacaoAPagar) {
        this.obrigacaoAPagar = obrigacaoAPagar;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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

    @Override
    public String toString() {
        return "Unidade: " + unidadeOrganizacional + "; Conta: " + conta.toString() + "; Conta de Destinação de Recurso: " + contaDeDestinacao + "; Valor: " + Util.formataValor(valor);
    }
}
