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
import br.com.webpublico.util.anotacoes.ErroReprocessamentoContabil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.ReprocessamentoContabil;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Claudio
 */
@Etiqueta("Estorno da Receita Orçamentária")
@GrupoDiagrama(nome = "Orcamentario")
@Audited

@Entity
public class EstornoReceitaAlteracaoOrc extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable, EntidadeContabil {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ReceitaAlteracaoORC receitaAlteracaoORC;
    @ManyToOne
    private EstornoAlteracaoOrc estornoAlteracaoOrc;
    private BigDecimal valor;
    @ManyToOne
    @Obrigatorio
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;
    private String historicoNota;
    private String historicoRazao;

    public EstornoReceitaAlteracaoOrc() {
        criadoEm = System.nanoTime();
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

    public ReceitaAlteracaoORC getReceitaAlteracaoORC() {
        return receitaAlteracaoORC;
    }

    public void setReceitaAlteracaoORC(ReceitaAlteracaoORC receitaAlteracaoORC) {
        this.receitaAlteracaoORC = receitaAlteracaoORC;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getEstornoAlteracaoOrc().getAlteracaoORC().getCodigo() != null) {
            historicoNota += "Alteração N°" + this.getEstornoAlteracaoOrc().getAlteracaoORC().getCodigo() + "/" + Util.getAnoDaData(this.getEstornoAlteracaoOrc().getAlteracaoORC().getDataAlteracao()) + ",";
        }
        if (this.getEstornoAlteracaoOrc().getCodigo() != null) {
            historicoNota += "N°" + this.getEstornoAlteracaoOrc().getCodigo() + "/" + Util.getAnoDaData(this.getEstornoAlteracaoOrc().getDataEstorno()) + ",";
        }
        if (this.getReceitaAlteracaoORC().getTipoAlteracaoORC() != null) {
            historicoNota += " Tipo de Alteração: " + this.getReceitaAlteracaoORC().getTipoAlteracaoORC().getDescricao() + ",";
        }
        if (this.getReceitaAlteracaoORC().getReceitaLOA() != null) {
            historicoNota += " Conta de Receita: " + this.getReceitaAlteracaoORC().getReceitaLOA() + ",";
        }
        if (this.getReceitaAlteracaoORC().getFonteDeRecurso() != null) {
            historicoNota += " Fonte de Recursos: " + this.getReceitaAlteracaoORC().getFonteDeRecurso() + ",";
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


    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(receitaAlteracaoORC.getReceitaLOA().getEntidade(),
            receitaAlteracaoORC.getContaDeDestinacao(),
            receitaAlteracaoORC.getReceitaLOA().getContaDeReceita(),
            receitaAlteracaoORC.getReceitaLOA().getContaDeReceita().getCodigoContaSiconf(),
            getEstornoAlteracaoOrc().getExercicio());
    }
}
