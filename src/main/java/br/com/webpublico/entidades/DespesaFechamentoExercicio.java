package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDespesaFechamentoExercicio;
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
 * Date: 10/11/14
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Dotação de Fechamento do Exercício")
public class DespesaFechamentoExercicio implements Serializable, IGeraContaAuxiliar {

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
    @Etiqueta("Empenho")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    private Empenho empenho;
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
    private TipoDespesaFechamentoExercicio tipo;

    public DespesaFechamentoExercicio() {
    }

    public DespesaFechamentoExercicio(HierarquiaOrganizacional orgao, HierarquiaOrganizacional unidade, Funcao funcao, SubFuncao subFuncao, ProgramaPPA programaPPA, AcaoPPA acaoPPA, SubAcaoPPA subAcaoPPA, Conta conta, FonteDeRecursos fonteDeRecursos, ContaDeDestinacao contaDeDestinacao, AberturaFechamentoExercicio selecionado, Empenho empenho, BigDecimal valor, TipoDespesaFechamentoExercicio tipo) {
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
        this.empenho = empenho;
        this.tipo = tipo;
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

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public TipoDespesaFechamentoExercicio getTipo() {
        return tipo;
    }

    public void setTipo(TipoDespesaFechamentoExercicio tipo) {
        this.tipo = tipo;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    @Override
    public String toString() {
        return "Unidade: " + unidadeOrganizacional + "; Conta: " + conta.toString() + "; Fonte de Recursos: " + fonteDeRecursos + "; Valor: " + Util.formataValor(valor);
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
                    (empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(unidadeOrganizacional,
                    funcao.getCodigo() + subFuncao.getCodigo(),
                    getEmpenho().getContaDeDestinacao(),
                    getEmpenho().getContaDespesa(),
                    (empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                    empenho.getEmpenho().getExercicio().getAno(),
                    empenho.getExercicio(),
                    empenho.getEmpenho().getExercicio());
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
                    (empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(unidadeOrganizacional,
                    funcao.getCodigo() + subFuncao.getCodigo(),
                    getEmpenho().getContaDeDestinacao(),
                    getEmpenho().getContaDespesa(),
                    (empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                    empenho.getEmpenho().getExercicio().getAno(),
                    empenho.getExercicio());
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
