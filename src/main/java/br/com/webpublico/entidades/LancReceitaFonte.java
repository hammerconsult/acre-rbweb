/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * @author major
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Etiqueta("Receita Realizada Fonte")
public class LancReceitaFonte extends SuperEntidade implements Serializable, EntidadeContabil, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Receita realizada")
    private LancamentoReceitaOrc lancReceitaOrc;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Fonte de Recurso")
    private ReceitaLOAFonte receitaLoaFonte;
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

    public LancReceitaFonte() {
        valor = BigDecimal.ZERO;
        this.item = 1l;
    }

    public LancReceitaFonte(BigDecimal valor, LancamentoReceitaOrc lancReceitaOrc, ReceitaLOAFonte receitaLoaFonte, Long item) {
        this.valor = valor;
        this.lancReceitaOrc = lancReceitaOrc;
        this.receitaLoaFonte = receitaLoaFonte;
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

    public LancamentoReceitaOrc getLancReceitaOrc() {
        return lancReceitaOrc;
    }

    public void setLancReceitaOrc(LancamentoReceitaOrc lancReceitaOrc) {
        this.lancReceitaOrc = lancReceitaOrc;
    }

    public ReceitaLOAFonte getReceitaLoaFonte() {
        return receitaLoaFonte;
    }

    public void setReceitaLoaFonte(ReceitaLOAFonte receitaLoaFonte) {
        this.receitaLoaFonte = receitaLoaFonte;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getItem() {
        return item;
    }

    public void setItem(Long item) {
        this.item = item;
    }

    public CodigoCO getCodigoCO() {
        return codigoCO;
    }

    public void setCodigoCO(CodigoCO codigoCO) {
        this.codigoCO = codigoCO;
    }

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getLancReceitaOrc().getNumero() != null) {
            historicoNota += "N°: " + this.getLancReceitaOrc().getNumero() + "/" + Util.getAnoDaData(this.getLancReceitaOrc().getDataLancamento()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getLancReceitaOrc().getSubConta() != null) {
            historicoNota += "Conta Financeira: " + this.getLancReceitaOrc().getSubConta().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getLancReceitaOrc().getReceitaLOA() != null) {
            historicoNota += "Conta de Receita: " + this.getLancReceitaOrc().getReceitaLOA().getContaDeReceita().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaLoaFonte() != null && this.getReceitaLoaFonte().getDestinacaoDeRecursos() != null) {
            historicoNota += "Fonte de Recursos: " + this.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }

        historicoNota = historicoNota + this.getLancReceitaOrc().getComplemento();
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil() != null) {
            if (this.getEventoContabil().getClpHistoricoContabil() != null) {
                historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString().trim();
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
        return "Receita Realizada: " + lancReceitaOrc + " - Fonte .: " + ((ContaDeDestinacao) this.getReceitaLoaFonte().getDestinacaoDeRecursos()).getFonteDeRecursos() + " - " + Util.formataValor(valor);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return ((EntidadeContabil) lancReceitaOrc).getReferenciaArquivoPrestacaoDeContas() + " - FR: " + ((ContaDeDestinacao) this.getReceitaLoaFonte().getDestinacaoDeRecursos()).getFonteDeRecursos().getCodigo() + " - " + ((ContaDeDestinacao) this.getReceitaLoaFonte().getDestinacaoDeRecursos()).getFonteDeRecursos().getDescricao();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }


    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        if (codigoCO != null && receitaLoaFonte.getDestinacaoDeRecursos() != null) {
            receitaLoaFonte.getDestinacaoDeRecursos().setCodigoCOEmenda(codigoCO.getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(lancReceitaOrc.getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(lancReceitaOrc.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(lancReceitaOrc.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    receitaLoaFonte.getDestinacaoDeRecursos(),
                    getLancReceitaOrc().getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(lancReceitaOrc.getUnidadeOrganizacional(),
                    receitaLoaFonte.getDestinacaoDeRecursos(),
                    getLancReceitaOrc().getExercicio());
            case "96":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada6(lancReceitaOrc.getUnidadeOrganizacional(),
                    receitaLoaFonte.getDestinacaoDeRecursos(),
                    lancReceitaOrc.getReceitaLOA().getContaDeReceita(),
                    getLancReceitaOrc().getExercicio());
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
        if (codigoCO != null && receitaLoaFonte.getDestinacaoDeRecursos() != null) {
            receitaLoaFonte.getDestinacaoDeRecursos().setCodigoCOEmenda(codigoCO.getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(lancReceitaOrc.getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(lancReceitaOrc.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(lancReceitaOrc.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    receitaLoaFonte.getDestinacaoDeRecursos());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(lancReceitaOrc.getUnidadeOrganizacional(),
                    receitaLoaFonte.getDestinacaoDeRecursos());
            case "96":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar6(lancReceitaOrc.getUnidadeOrganizacional(),
                    receitaLoaFonte.getDestinacaoDeRecursos(),
                    (!Strings.isNullOrEmpty(lancReceitaOrc.getReceitaLOA().getContaDeReceita().getCodigoSICONFI()) ?
                        lancReceitaOrc.getReceitaLOA().getContaDeReceita().getCodigoSICONFI() :
                        lancReceitaOrc.getReceitaLOA().getContaDeReceita().getCodigo()).replace(".", ""));
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
