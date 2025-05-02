package br.com.webpublico.entidades.tributario.procuradoria;


import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.ProcessoJudicial;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by tributario on 19/08/2015.
 */
@Audited
@Entity
@GrupoDiagrama(nome = "Procuradoria")
@Etiqueta("Trâmite")
public class TramiteProcessoJudicial extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    @ManyToOne
    private ProcessoJudicial processoJudicial;
    @ManyToOne
    private PessoaFisica juizResponsavel;
    @ManyToOne
    private Vara vara;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    private SituacaoTramiteJudicial situacaoTramiteJudicial;
    private BigDecimal valor;

    public TramiteProcessoJudicial() {
        this.valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public ProcessoJudicial getProcessoJudicial() {
        return processoJudicial;
    }

    public void setProcessoJudicial(ProcessoJudicial processoJudicial) {
        this.processoJudicial = processoJudicial;
    }

    public PessoaFisica getJuizResponsavel() {
        return juizResponsavel;
    }

    public void setJuizResponsavel(PessoaFisica juizResponsavel) {
        this.juizResponsavel = juizResponsavel;
    }

    public Vara getVara() {
        return vara;
    }

    public void setVara(Vara vara) {
        this.vara = vara;
    }

    public SituacaoTramiteJudicial getSituacaoTramiteJudicial() {
        return situacaoTramiteJudicial;
    }

    public void setSituacaoTramiteJudicial(SituacaoTramiteJudicial situacaoTramiteJudicial) {
        this.situacaoTramiteJudicial = situacaoTramiteJudicial;
    }
}
