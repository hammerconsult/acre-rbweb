/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * @author Claudio
 */
@Etiqueta("Estorno da Suplementação Orçamentária")
@GrupoDiagrama(nome = "Orcamentario")
@Audited

@Entity
public class EstornoSuplementacaoOrc extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabil {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private SuplementacaoORC suplementacaoORC;

    @ManyToOne
    private EstornoAlteracaoOrc estornoAlteracaoOrc;

    @Monetario
    @Etiqueta("Valor")
    private BigDecimal valor;

    @ManyToOne
    @Obrigatorio
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;

    private String historicoNota;
    private String historicoRazao;

    public EstornoSuplementacaoOrc() {
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

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public EstornoAlteracaoOrc getEstornoAlteracaoOrc() {
        return estornoAlteracaoOrc;
    }

    public void setEstornoAlteracaoOrc(EstornoAlteracaoOrc estornoAlteracaoOrc) {
        this.estornoAlteracaoOrc = estornoAlteracaoOrc;
    }

    public SuplementacaoORC getSuplementacaoORC() {
        return suplementacaoORC;
    }

    public void setSuplementacaoORC(SuplementacaoORC suplementacaoORC) {
        this.suplementacaoORC = suplementacaoORC;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getEstornoAlteracaoOrc().getAlteracaoORC().getCodigo() != null) {
            historicoNota += "Alteração N°" + this.getEstornoAlteracaoOrc().getAlteracaoORC().getCodigo() + "/" + Util.getAnoDaData(this.getEstornoAlteracaoOrc().getDataEstorno()) + ",";
        }
        if (this.getEstornoAlteracaoOrc().getCodigo() != null) {
            historicoNota += "N°" + this.getEstornoAlteracaoOrc().getCodigo() + "/" + Util.getAnoDaData(this.getEstornoAlteracaoOrc().getDataEstorno()) + ",";
        }
        if (this.getSuplementacaoORC().getFonteDespesaORC() != null) {
            historicoNota += " Elemento de Despesa: " + this.getSuplementacaoORC().getFonteDespesaORC() + ",";
        }
        if (this.getSuplementacaoORC().getTipoDespesaORC() != null) {
            historicoNota += " Tipo de Despesa: " + this.getSuplementacaoORC().getTipoDespesaORC().getDescricao() + ",";
        }
        if (this.getSuplementacaoORC().getOrigemSuplemtacao() != null) {
            historicoNota += " Tipo de Suplementação: " + this.getSuplementacaoORC().getOrigemSuplemtacao().getDescricao() + ",";
        }
        historicoNota = historicoNota + this.getEstornoAlteracaoOrc().getHistorico();
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
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
        return estornoAlteracaoOrc + " Valor: " + Util.formataValor(valor);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return estornoAlteracaoOrc.getCodigo();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    public String getCodigoExtensaoFonteRecursoAsString() {
        return suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getExtensaoFonteRecurso().getCodigo().toString();
    }

    public Integer getCodigoEs() {
        return suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getExtensaoFonteRecurso().getCodigoEs();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        AcaoPPA acaoPPA = suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        return new GeradorContaAuxiliarDTO(suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional(),
            acaoPPA.getCodigoFuncaoSubFuncao(),
            suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao(),
            suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa(),
            getCodigoEs(), null,
            null, getEstornoAlteracaoOrc().getExercicio(), null, null, null, null,
            suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa().getCodigoContaSiconf());
    }
}
