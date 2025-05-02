package br.com.webpublico.entidades.comum;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SituacaoFechamentoMensal;
import br.com.webpublico.enums.TipoSituacaoAgendamento;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class FechamentoMensalMes extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Mês")
    private Mes mes;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Situação Contábil")
    private SituacaoFechamentoMensal situacaoContabil;
    @ManyToOne
    private FechamentoMensal fechamentoMensal;
    private String cron;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Data do Agendamento")
    private Date dataAgendamento;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private TipoSituacaoAgendamento tipoSituacaoAgendamento;
    @Transient
    private Date dataInicialReprocessamento;
    @Transient
    private Date dataFinalReprocessamento;

    public FechamentoMensalMes() {
    }

    public FechamentoMensalMes(Mes mes, SituacaoFechamentoMensal situacaoContabil, FechamentoMensal fechamentoMensal) {
        this.mes = mes;
        this.situacaoContabil = situacaoContabil;
        this.fechamentoMensal = fechamentoMensal;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
        atualizarDatasReprocessamentoComMes();
    }

    public SituacaoFechamentoMensal getSituacaoContabil() {
        return situacaoContabil;
    }

    public void setSituacaoContabil(SituacaoFechamentoMensal situacaoContabil) {
        this.situacaoContabil = situacaoContabil;
    }

    public FechamentoMensal getFechamentoMensal() {
        return fechamentoMensal;
    }

    public void setFechamentoMensal(FechamentoMensal fechamentoMensal) {
        this.fechamentoMensal = fechamentoMensal;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Date getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(Date dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public TipoSituacaoAgendamento getTipoSituacaoAgendamento() {
        return tipoSituacaoAgendamento;
    }

    public void setTipoSituacaoAgendamento(TipoSituacaoAgendamento tipoSituacaoAgendamento) {
        this.tipoSituacaoAgendamento = tipoSituacaoAgendamento;
    }

    public Date getDataInicialReprocessamento() {
        return dataInicialReprocessamento;
    }

    public void setDataInicialReprocessamento(Date dataInicialReprocessamento) {
        this.dataInicialReprocessamento = dataInicialReprocessamento;
    }

    public Date getDataFinalReprocessamento() {
        return dataFinalReprocessamento;
    }

    public void setDataFinalReprocessamento(Date dataFinalReprocessamento) {
        this.dataFinalReprocessamento = dataFinalReprocessamento;
    }

    private void atualizarDatasReprocessamentoComMes() {
        if (fechamentoMensal != null && fechamentoMensal.getExercicio() != null && mes != null) {
            if (dataInicialReprocessamento == null || DataUtil.getMes(dataInicialReprocessamento) != mes.getNumeroMes()) {
                dataInicialReprocessamento = DataUtil.criarDataComMesEAno(mes.getNumeroMes(), fechamentoMensal.getExercicio().getAno()).toDate();
            }
            if (dataFinalReprocessamento == null || DataUtil.getMes(dataFinalReprocessamento) != mes.getNumeroMes()) {
                dataFinalReprocessamento = DataUtil.criarDataUltimoDiaMes(mes.getNumeroMes(), fechamentoMensal.getExercicio().getAno()).toDate();
            }
        }
    }
}
