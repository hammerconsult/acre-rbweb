package br.com.webpublico.entidades.comum.agenda;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by tributario on 20/08/2015.
 */
@Audited
@Entity
@GrupoDiagrama(nome = "Agenda")
public class CompartilhamentoAgenda extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AgendaUsuario agendaUsuario;
    @ManyToOne
    private UsuarioSistema usuarioSistema;


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AgendaUsuario getAgendaUsuario() {
        return this.agendaUsuario;
    }

    public void setAgendaUsuario(AgendaUsuario agendaUsuario) {
        this.agendaUsuario = agendaUsuario;
    }

    public UsuarioSistema getUsuarioSistema() {
        return this.usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
