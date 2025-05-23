package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 04/12/14
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Abertura de Inscrição de Resto a Pagar")
public class AberturaInscricaoResto implements Serializable, EntidadeContabil, IGeraContaAuxiliar {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Empenho")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Empenho empenho;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    @Etiqueta("Evento Contábil")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @ReprocessamentoContabil
    private EventoContabil eventoContabil;
    @Etiqueta("Tipo Restos")
    @Obrigatorio
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoRestosProcessado tipoRestos;
    @Etiqueta("Valor")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Monetario
    private BigDecimal valor;
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    @ReprocessamentoContabil
    private Date data;

    public AberturaInscricaoResto() {
    }

    public AberturaInscricaoResto(Empenho empenho, AberturaFechamentoExercicio selecionado, TipoRestosProcessado tipoRestosProcessado, BigDecimal valor) {
        this.empenho = empenho;
        this.aberturaFechamentoExercicio = selecionado;
        this.tipoRestos = tipoRestosProcessado;
        this.valor = valor;
        this.data = empenho.getDataEmpenho();
        this.unidadeOrganizacional = empenho.getUnidadeOrganizacional();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
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

    public TipoRestosProcessado getTipoRestos() {
        return tipoRestos;
    }

    public void setTipoRestos(TipoRestosProcessado tipoRestos) {
        this.tipoRestos = tipoRestos;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Boolean isProcessado() {
        if (this.getTipoRestos().equals(TipoRestosProcessado.PROCESSADOS)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Empenho: " + empenho.toString();
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return null;
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        String retorno = "";
        if (empenho.getEmpenho() != null) {
            retorno = "Restos a Pagar: nº " + empenho.getNumero() + " - " + DataUtil.getDataFormatada(empenho.getDataEmpenho()) + " - " +
                "Empenho: nº " + empenho.getEmpenho().getNumero() + " - " + DataUtil.getDataFormatada(empenho.getEmpenho().getDataEmpenho()) + " - ";
        } else {
            retorno = "Empenho: nº " + empenho.getNumero() + " - " + DataUtil.getDataFormatada(empenho.getDataEmpenho()) + " - ";
        }
        retorno += "Função " + empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getCodigo() + " - " + empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getDescricao() + " - " +
            "Subfunção " + empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getCodigo() + " - " + empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getDescricao() + " - " +
            "Conta de Despesa " + empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo() + " - " + empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " - " +
            "Fonte de Recurso " + empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo() + " - " + empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getDescricao() + " - " +
            "Pessoa " + empenho.getFornecedor().getNome() + " - " +
            "Valor " + Util.formataValor(valor);
        return retorno;
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        switch (tipoContaAuxiliar.getCodigo()) {
            case "99":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    empenho.getContaDeDestinacao(),
                    empenho.getContaDespesa(),
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
        AcaoPPA acaoPPA = empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        switch (tipoContaAuxiliar.getCodigo()) {
            case "99":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(empenho.getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    empenho.getContaDeDestinacao(),
                    empenho.getContaDespesa(),
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
