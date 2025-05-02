package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDocPagto;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoPrestacaoDeContas;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Etiqueta("Lançamento Contábil Manual")
public class LancamentoContabilManual extends SuperEntidade implements EntidadeContabil {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data")
    @Obrigatorio
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private Date data;
    @Etiqueta("Exercicio")
    @Obrigatorio
    @ManyToOne
    private Exercicio exercicio;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    private String numero;
    @Obrigatorio
    @Etiqueta(value = "Tipo de Lançamento")
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipoLancamento;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Evento Contábil")
    private TipoEventoContabil tipoEventoContabil;
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Unidade Organizacional")
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Histórico")
    private String complementoHistorico;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lancamento")
    private List<ContaLancamentoManual> contas;
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    private SubConta subConta;
    private String numeroDocumentoFinanceiro;
    @Enumerated(EnumType.STRING)
    private TipoDocPagto tipoDocPagto;
    @ManyToOne
    private ContaReceita contaReceita;
    @ManyToOne
    private ContaDespesa contaDespesa;
    @ManyToOne
    private ProgramaPPA programaPPA;
    @ManyToOne
    private AcaoPPA acaoPPA;
    @ManyToOne
    private Funcao funcao;
    @ManyToOne
    private SubFuncao subFuncao;
    @ManyToOne
    private ExtensaoFonteRecurso extensaoFonteRecurso;
    @ManyToOne
    private Exercicio exercicioResto;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lancamento")
    private List<TipoLancamentoManual> tiposDePrestacoesDeContas;

    public LancamentoContabilManual() {
        this.contas = Lists.newArrayList();
        this.tiposDePrestacoesDeContas = Lists.newArrayList();
        this.valor = BigDecimal.ZERO;
        this.tipoEventoContabil = TipoEventoContabil.AJUSTE_CONTABIL_MANUAL;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public TipoEventoContabil getTipoEventoContabil() {
        return tipoEventoContabil;
    }

    public void setTipoEventoContabil(TipoEventoContabil tipoEventoContabil) {
        this.tipoEventoContabil = tipoEventoContabil;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public List<ContaLancamentoManual> getContas() {
        return contas;
    }

    public void setContas(List<ContaLancamentoManual> contas) {
        this.contas = contas;
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

    public String getNumeroDocumentoFinanceiro() {
        return numeroDocumentoFinanceiro;
    }

    public void setNumeroDocumentoFinanceiro(String numeroDocumentoFinanceiro) {
        this.numeroDocumentoFinanceiro = numeroDocumentoFinanceiro;
    }

    public TipoDocPagto getTipoDocPagto() {
        return tipoDocPagto;
    }

    public void setTipoDocPagto(TipoDocPagto tipoDocPagto) {
        this.tipoDocPagto = tipoDocPagto;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public ContaDespesa getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(ContaDespesa contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
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

    public ExtensaoFonteRecurso getExtensaoFonteRecurso() {
        return extensaoFonteRecurso;
    }

    public void setExtensaoFonteRecurso(ExtensaoFonteRecurso extensaoFonteRecurso) {
        this.extensaoFonteRecurso = extensaoFonteRecurso;
    }

    public Exercicio getExercicioResto() {
        return exercicioResto;
    }

    public void setExercicioResto(Exercicio exercicioResto) {
        this.exercicioResto = exercicioResto;
    }

    public List<TipoLancamentoManual> getTiposDePrestacoesDeContas() {
        return tiposDePrestacoesDeContas;
    }

    public void setTiposDePrestacoesDeContas(List<TipoLancamentoManual> tiposDePrestacoesDeContas) {
        this.tiposDePrestacoesDeContas = tiposDePrestacoesDeContas;
    }

    @Override
    public String toString() {
        return numero + "/" + exercicio.getAno();
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + "/" + exercicio.getAno();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return "";
    }

    @Override
    public void realizarValidacoes() throws ValidacaoException {
        super.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (getValor() == null || getValor().compareTo(BigDecimal.ZERO) == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor não pode ser menor ou igual a zero(0).");
        }
        ve.lancarException();
    }

    public boolean hasTipoCadastrado(List<TipoPrestacaoDeContas> tiposParaVerificacao) {
        if (this.getTiposDePrestacoesDeContas() != null && !this.getTiposDePrestacoesDeContas().isEmpty()) {
            for (TipoLancamentoManual tipoLancamentoManual : this.getTiposDePrestacoesDeContas()) {
                if (tiposParaVerificacao.contains(tipoLancamentoManual.getTipoPrestacaoDeContas())) {
                    return true;
                }
            }
        }
        return false;
    }
}
