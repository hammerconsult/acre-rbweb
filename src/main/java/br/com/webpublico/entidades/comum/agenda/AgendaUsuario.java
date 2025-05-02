package br.com.webpublico.entidades.comum.agenda;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.geradores.GrupoDiagrama;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tributario on 20/08/2015.
 */
@Entity
@Audited
@Etiqueta("Agenda do Usuário")
@GrupoDiagrama(nome = "Agenda")
public class AgendaUsuario extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Proprietário")
    private UsuarioSistema proprietario;
    @OneToMany(mappedBy = "agendaUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompartilhamentoAgenda> compartilhamentos;
    @OneToMany(mappedBy = "agendaUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoAgenda> eventos;

    public AgendaUsuario() {
        compartilhamentos = new ArrayList<>();
        eventos = new ArrayList<>();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getProprietario() {
        return proprietario;
    }

    public void setProprietario(UsuarioSistema proprietario) {
        this.proprietario = proprietario;
    }

    public List<CompartilhamentoAgenda> getCompartilhamentos() {
        return Collections.unmodifiableList(compartilhamentos);
    }

    public void setCompartilhamentos(List<CompartilhamentoAgenda> compartilhamentos) {
        this.compartilhamentos = compartilhamentos;
    }

    public void addCompartilhamento(CompartilhamentoAgenda compartilhamentoAgenda) {
        compartilhamentoAgenda.setAgendaUsuario(this);
        this.compartilhamentos.add(compartilhamentoAgenda);
        System.out.println(this.compartilhamentos.size());
    }

    public void removeCompartilhamento(CompartilhamentoAgenda compartilhamentoAgenda) {
        compartilhamentos.remove(compartilhamentoAgenda);
    }

    public List<EventoAgenda> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

    public void setEventos(List<EventoAgenda> eventos) {
        this.eventos = eventos;
    }

    public void addEvento(EventoAgenda eventoAgenda) {
        eventoAgenda.setAgendaUsuario(this);
        this.eventos.add(eventoAgenda);
    }

    public void removeEvento(EventoAgenda eventoAgenda){
        this.eventos.remove(eventoAgenda);
    }

}
