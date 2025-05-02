package br.com.webpublico.pncp.entidade;

import br.com.webpublico.entidades.SuperEntidade;
import com.google.common.collect.ComparisonChain;

import javax.persistence.*;
import java.util.Date;

@Entity
public class LogEnvioEventoPncp extends SuperEntidade implements Comparable<LogEnvioEventoPncp> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    @ManyToOne
    private EventoPncp evento;
    private String json;

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

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public int compareTo(LogEnvioEventoPncp logEnvio) {
        if (getData() != null && logEnvio != null && logEnvio.getData() != null) {
            return ComparisonChain.start().compare(logEnvio.getData(), getData()).result();
        }
        return 0;
    }
}
