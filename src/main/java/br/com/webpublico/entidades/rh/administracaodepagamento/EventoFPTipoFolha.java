package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author peixe on 11/10/2016  15:55.
 */
@Entity
@Audited
public class EventoFPTipoFolha extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EventoFP eventoFP;
    @Enumerated(EnumType.STRING)
    private TipoFolhaDePagamento tipoFolhaDePagamento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    @Override
    public String toString() {
        return tipoFolhaDePagamento.name();
    }
}
