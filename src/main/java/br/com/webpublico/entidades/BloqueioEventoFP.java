package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 11/01/14
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Bloquear Evento FP por Servidor")
@GrupoDiagrama(nome = "RecursosHumanos")
public class BloqueioEventoFP implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Servidor")
    @ManyToOne
    private VinculoFP vinculoFP;
    @ManyToOne
    @Etiqueta("Evento FP")
    @Tabelavel
    @Pesquisavel
    private EventoFP eventoFP;
    @Tabelavel
    @Etiqueta("Data inicial")
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataInicial;
    @Tabelavel
    @Etiqueta("Data final")
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataFinal;
    private String observacao;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
