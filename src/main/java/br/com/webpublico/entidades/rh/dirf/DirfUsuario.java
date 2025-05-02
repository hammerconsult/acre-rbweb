package br.com.webpublico.entidades.rh.dirf;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class DirfUsuario extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    private ConfiguracaoRH configuracaoRH;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public ConfiguracaoRH getConfiguracaoRH() {
        return configuracaoRH;
    }

    public void setConfiguracaoRH(ConfiguracaoRH configuracaoRH) {
        this.configuracaoRH = configuracaoRH;
    }
}
