package br.com.webpublico.entidades.comum.agenda;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tributario on 20/08/2015.
 */
@Audited
@Entity
@Etiqueta("Evento da Agenda")
@GrupoDiagrama(nome = "Agenda")
public class EventoAgenda extends SuperEntidade implements Comparable<EventoAgenda> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Início")
    private Date dataHoraInicio;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Término")
    private Date dataHoraFim;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Assunto")
    private String assunto;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Observação")
    private String observacao;
    private String corEvento;
    @ManyToOne
    private AgendaUsuario agendaUsuario;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(Date dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public Date getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(Date dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getCorEvento() {
        return corEvento;
    }

    public void setCorEvento(String corEvento) {
        this.corEvento = corEvento;
    }

    public AgendaUsuario getAgendaUsuario() {
        return agendaUsuario;
    }

    public void setAgendaUsuario(AgendaUsuario agendaUsuario) {
        this.agendaUsuario = agendaUsuario;
    }

    @Override
    public int compareTo(EventoAgenda o) {
        return this.dataHoraInicio.compareTo(o.dataHoraInicio);
    }
}
