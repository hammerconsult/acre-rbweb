package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 20/01/14
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class ReceitaORCFonteEstorno extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable, EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Receita Estorno")
    private ReceitaORCEstorno receitaORCEstorno;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Fonte de Recurso")
    private ReceitaLOAFonte receitaLoaFonte;
    @Invisivel
    @Transient
    private Long criadoEm;
    private String historicoNota;
    private String historicoRazao;
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private EventoContabil eventoContabil;
    private Long item;
    @Tabelavel
    @Etiqueta("Valor (R$)")
    @Monetario
    private BigDecimal valor;
    @ManyToOne
    private CodigoCO codigoCO;

    public ReceitaORCFonteEstorno() {
        criadoEm = System.nanoTime();
        valor = BigDecimal.ZERO;
    }

    public ReceitaORCFonteEstorno(BigDecimal valor, ReceitaORCEstorno receitaORCEstorno, ReceitaLOAFonte receitaLoaFonte) {
        this.valor = valor;
        this.receitaORCEstorno = receitaORCEstorno;
        this.receitaLoaFonte = receitaLoaFonte;
    }

    public Long getItem() {
        return item;
    }

    public void setItem(Long item) {
        this.item = item;
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

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ReceitaORCEstorno getReceitaORCEstorno() {
        return receitaORCEstorno;
    }

    public void setReceitaORCEstorno(ReceitaORCEstorno receitaORCEstorno) {
        this.receitaORCEstorno = receitaORCEstorno;
    }

    public ReceitaLOAFonte getReceitaLoaFonte() {
        return receitaLoaFonte;
    }

    public void setReceitaLoaFonte(ReceitaLOAFonte receitaLoaFonte) {
        this.receitaLoaFonte = receitaLoaFonte;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CodigoCO getCodigoCO() {
        return codigoCO;
    }

    public void setCodigoCO(CodigoCO codigoCO) {
        this.codigoCO = codigoCO;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getReceitaORCEstorno().getNumero() != null) {
            historicoNota += "N°: " + this.getReceitaORCEstorno().getNumero() + "/" + Util.getAnoDaData(this.getReceitaORCEstorno().getDataEstorno()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaORCEstorno().getLancamentoReceitaOrc() != null) {
            historicoNota += "Receita Realizada: " + this.getReceitaORCEstorno().getLancamentoReceitaOrc().getNumero() + "/" + Util.getAnoDaData(this.getReceitaORCEstorno().getLancamentoReceitaOrc().getDataLancamento()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaORCEstorno().getContaFinanceira() != null) {
            historicoNota += "Conta Financeira: " + this.getReceitaORCEstorno().getContaFinanceira() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaORCEstorno().getReceitaLOA() != null) {
            historicoNota += "Conta de Receita: " + this.getReceitaORCEstorno().getReceitaLOA().getContaDeReceita().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaORCEstorno().getReceitaLOA().getReceitaLoaFontes() != null) {
            historicoNota += "Fonte de Recursos: " + this.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }

        historicoNota = historicoNota + " " + this.getReceitaORCEstorno().getComplementoHistorico();
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil() != null) {
            if (this.getEventoContabil().getClpHistoricoContabil() != null) {
                historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
            }
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String toString() {
        return "Receita Realizada Estorno: " + receitaORCEstorno + " - Fonte .: " + ((ContaDeDestinacao) this.getReceitaLoaFonte().getDestinacaoDeRecursos()).getFonteDeRecursos() + " - " + Util.formataValor(valor);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return ((EntidadeContabil) receitaORCEstorno).getReferenciaArquivoPrestacaoDeContas();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        if (codigoCO != null && receitaLoaFonte.getDestinacaoDeRecursos() != null) {
            receitaLoaFonte.getDestinacaoDeRecursos().setCodigoCOEmenda(codigoCO.getCodigo());
        }
        if (receitaORCEstorno.getLancamentoReceitaOrc() != null) {
            return new GeradorContaAuxiliarDTO(receitaORCEstorno.getUnidadeOrganizacionalOrc(),
                receitaLoaFonte.getDestinacaoDeRecursos(),
                receitaORCEstorno.getLancamentoReceitaOrc().getReceitaLOA().getContaDeReceita(),
                receitaORCEstorno.getLancamentoReceitaOrc().getReceitaLOA().getContaDeReceita().getCodigoContaSiconf(),
                getReceitaORCEstorno().getExercicio());
        }
        return new GeradorContaAuxiliarDTO(receitaORCEstorno.getUnidadeOrganizacionalOrc(),
            receitaLoaFonte.getDestinacaoDeRecursos(),
            receitaLoaFonte.getReceitaLOA().getContaDeReceita(),
            receitaLoaFonte.getReceitaLOA().getContaDeReceita().getCodigoContaSiconf(),
            getReceitaORCEstorno().getExercicio());
    }
}

