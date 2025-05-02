package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoMonitoramentoFiscal;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "Fiscalização")
@Table(name = "HISTORICOSITUACAOMONFISCAL")
@Entity
@Audited
public class HistoricoSituacaoMonitoramentoFiscal implements Serializable, Comparable<HistoricoSituacaoMonitoramentoFiscal> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private MonitoramentoFiscal monitoramentoFiscal;
    @Enumerated(EnumType.STRING)
    private SituacaoMonitoramentoFiscal situacaoMonitoramentoFiscal;
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;

    public HistoricoSituacaoMonitoramentoFiscal() {
    }

    public HistoricoSituacaoMonitoramentoFiscal(MonitoramentoFiscal monitoramentoFiscal, SituacaoMonitoramentoFiscal situacaoMonitoramentoFiscal, Date data) {
        this.monitoramentoFiscal = monitoramentoFiscal;
        this.situacaoMonitoramentoFiscal = situacaoMonitoramentoFiscal;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MonitoramentoFiscal getMonitoramentoFiscal() {
        return monitoramentoFiscal;
    }

    public void setMonitoramentoFiscal(MonitoramentoFiscal monitoramentoFiscal) {
        this.monitoramentoFiscal = monitoramentoFiscal;
    }

    public SituacaoMonitoramentoFiscal getSituacaoMonitoramentoFiscal() {
        return situacaoMonitoramentoFiscal;
    }

    public void setSituacaoMonitoramentoFiscal(SituacaoMonitoramentoFiscal situacaoMonitoramentoFiscal) {
        this.situacaoMonitoramentoFiscal = situacaoMonitoramentoFiscal;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public int compareTo(HistoricoSituacaoMonitoramentoFiscal o) {
        return this.data.compareTo(o.getData());
    }
}
