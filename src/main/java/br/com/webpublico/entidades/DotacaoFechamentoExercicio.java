package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDotacaoFechamentoExercicio;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 06/11/14
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Dotação de Fechamento do Exercício")
public class DotacaoFechamentoExercicio implements Serializable, IGeraContaAuxiliar {

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
    @Tabelavel
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
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Etiqueta("Extensão da Fonte de Recurso")
    private ExtensaoFonteRecurso extensaoFonteRecurso;
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
    @Etiqueta("Evento Contábil")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    private EventoContabil eventoContabil;
    @Enumerated(EnumType.STRING)
    private TipoDotacaoFechamentoExercicio tipo;

    public DotacaoFechamentoExercicio() {
    }

    public DotacaoFechamentoExercicio(HierarquiaOrganizacional hierarquiaOrganizacional, HierarquiaOrganizacional unidade, Funcao funcao, SubFuncao subFuncao, ProgramaPPA programaPPA, AcaoPPA projetoAtividade, SubAcaoPPA subProjetoAtividade, Conta conta, FonteDeRecursos fonteDeRecursos, ContaDeDestinacao contaDeDestinacao, BigDecimal valor, AberturaFechamentoExercicio aberturaFechamentoExercicio, TipoDotacaoFechamentoExercicio tipo, ExtensaoFonteRecurso extensaoFonteRecurso) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        this.unidade = unidade;
        this.unidadeOrganizacional = unidade.getSubordinada();
        this.funcao = funcao;
        this.subFuncao = subFuncao;
        this.programaPPA = programaPPA;
        this.projetoAtividade = projetoAtividade;
        this.subProjetoAtividade = subProjetoAtividade;
        this.conta = conta;
        this.fonteDeRecursos = fonteDeRecursos;
        this.valor = valor;
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
        this.tipo = tipo;
        this.extensaoFonteRecurso = extensaoFonteRecurso;
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

    public HierarquiaOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(HierarquiaOrganizacional unidade) {
        this.unidade = unidade;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public TipoDotacaoFechamentoExercicio getTipo() {
        return tipo;
    }

    public void setTipo(TipoDotacaoFechamentoExercicio tipo) {
        this.tipo = tipo;
    }

    public ExtensaoFonteRecurso getExtensaoFonteRecurso() {
        return extensaoFonteRecurso;
    }

    public void setExtensaoFonteRecurso(ExtensaoFonteRecurso extensaoFonteRecurso) {
        this.extensaoFonteRecurso = extensaoFonteRecurso;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public String getCodigoExtensaoFonteRecursoAsString() {
        return getExtensaoFonteRecurso().getCodigo().toString();
    }

    @Override
    public String toString() {
        return "Unidade: " + unidadeOrganizacional + "; Conta: " + conta.toString() + "; Conta de Destinação de Recurso: " + contaDeDestinacao + "; Valor: " + Util.formataValor(valor);
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(getUnidadeOrganizacional(),
                    funcao.getCodigo() + subFuncao.getCodigo(),
                    contaDeDestinacao,
                    conta,
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(getUnidadeOrganizacional(),
                    funcao.getCodigo() + subFuncao.getCodigo(),
                    contaDeDestinacao,
                    (conta.getCodigoSICONFI() != null ?
                        conta.getCodigoSICONFI() :
                        conta.getCodigo().replace(".", "")),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }
}
