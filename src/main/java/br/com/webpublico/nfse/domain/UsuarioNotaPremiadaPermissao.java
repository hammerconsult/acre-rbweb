package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * A user.
 */
@Entity
@Audited
@Table(name = "USUARIONOTAPREMPERMISSAO")
public class UsuarioNotaPremiadaPermissao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private UsuarioNotaPremiada usuario;

    @Enumerated(EnumType.STRING)
    private NotaPremiadaPermissao permissao;

    public UsuarioNotaPremiadaPermissao() {
        super();
    }

    public UsuarioNotaPremiadaPermissao(UsuarioNotaPremiada usuario,
                                        NotaPremiadaPermissao permissao) {
        this();
        this.usuario = usuario;
        this.permissao = permissao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioNotaPremiada getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioNotaPremiada usuario) {
        this.usuario = usuario;
    }

    public NotaPremiadaPermissao getPermissao() {
        return permissao;
    }

    public void setPermissao(NotaPremiadaPermissao permissao) {
        this.permissao = permissao;
    }
}
