package br.com.webpublico.entidades.contabil.reinf;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;

@Entity
@Etiqueta("Evento Registro Efd-reinf")
public class EventoRegistroReinfRetencao extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private RegistroReinf evento;
    @ManyToOne
    private RegistroEventoRetencaoReinf registro;

    public EventoRegistroReinfRetencao() {
    }

    public EventoRegistroReinfRetencao(RegistroEventoRetencaoReinf registroEventoRetencaoReinf, RegistroReinf registroReinf) {
        this.evento = registroReinf;
        this.registro = registroEventoRetencaoReinf;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegistroReinf getEvento() {
        return evento;
    }

    public void setEvento(RegistroReinf evento) {
        this.evento = evento;
    }

    public RegistroEventoRetencaoReinf getRegistro() {
        return registro;
    }

    public void setRegistro(RegistroEventoRetencaoReinf registro) {
        this.registro = registro;
    }
}
