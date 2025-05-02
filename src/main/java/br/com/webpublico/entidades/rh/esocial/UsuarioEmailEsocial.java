package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class UsuarioEmailEsocial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    private NotificacaoEmailEsocial notificacaoEmailEsocial;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificacaoEmailEsocial getNotificacaoEmailEsocial() {
        return notificacaoEmailEsocial;
    }

    public void setNotificacaoEmailEsocial(NotificacaoEmailEsocial notificacaoEmailEsocial) {
        this.notificacaoEmailEsocial = notificacaoEmailEsocial;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
