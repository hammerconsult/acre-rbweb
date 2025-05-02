package br.com.webpublico.entidades.contabil.conciliacaocontabil;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.contabil.SituacaoMovimentoContabil;
import br.com.webpublico.nfse.domain.UserNfseCadastroEconomico;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class AnaliseMovimentoContabil extends SuperEntidade implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataInicial;
    @Temporal(TemporalType.DATE)
    private Date dataFinal;
    @Enumerated(EnumType.STRING)
    private SituacaoMovimentoContabil situacao;
    private String log;

    public AnaliseMovimentoContabil() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public SituacaoMovimentoContabil getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMovimentoContabil situacao) {
        this.situacao = situacao;
    }
}
