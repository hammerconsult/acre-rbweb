package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.*;

@Entity
public class DetalheLogErroEnvioEvento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LogErroEnvioEvento logErroEnvioEvento;

    private String log;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LogErroEnvioEvento getLogErroEnvioEvento() {
        return logErroEnvioEvento;
    }

    public void setLogErroEnvioEvento(LogErroEnvioEvento logErroEnvioEvento) {
        this.logErroEnvioEvento = logErroEnvioEvento;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
