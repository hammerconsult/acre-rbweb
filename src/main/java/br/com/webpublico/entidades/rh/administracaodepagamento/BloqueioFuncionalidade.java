package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.rh.TipoFuncionalidadeRH;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @Author peixe on 27/10/2016  16:31.
 */
@Entity
@Audited
public class BloqueioFuncionalidade extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EventoFP eventoFP;
    @Enumerated(EnumType.STRING)
    private TipoFuncionalidadeRH tipoFuncionalidadeRH;

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

    public TipoFuncionalidadeRH getTipoFuncionalidadeRH() {
        return tipoFuncionalidadeRH;
    }

    public void setTipoFuncionalidadeRH(TipoFuncionalidadeRH tipoFuncionalidadeRH) {
        this.tipoFuncionalidadeRH = tipoFuncionalidadeRH;
    }


}
