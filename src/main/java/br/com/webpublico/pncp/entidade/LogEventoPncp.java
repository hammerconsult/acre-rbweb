package br.com.webpublico.pncp.entidade;

import br.com.webpublico.entidades.SuperEntidade;
import com.google.common.collect.ComparisonChain;

import javax.persistence.*;
import java.util.Date;

@Entity
public class LogEventoPncp extends SuperEntidade implements Comparable<LogEventoPncp> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    @ManyToOne
    private EventoPncp evento;
    private String descricao;
    private String log;

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

    public EventoPncp getEvento() {
        return evento;
    }

    public void setEvento(EventoPncp evento) {
        this.evento = evento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public int compareTo(LogEventoPncp logEventoPncp) {
        if (getData() != null && logEventoPncp != null && logEventoPncp.getData() != null) {
            return ComparisonChain.start().compare(logEventoPncp.getData(), getData()).result();
        }
        return 0;
    }
}
