package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.*;


@Entity
public class LoteRPSLog extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private LoteRPS loteRps;
    private String log;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteRPS getLoteRps() {
        return loteRps;
    }

    public void setLoteRps(LoteRPS loteRps) {
        this.loteRps = loteRps;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
