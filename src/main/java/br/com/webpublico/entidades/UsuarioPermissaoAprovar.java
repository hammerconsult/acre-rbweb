package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Configurações de Aprovação ou Rejeição das Solicitações de Credor do Portal do Contribuinte")
public class UsuarioPermissaoAprovar extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @ManyToOne
    private ConfiguracaoPortalContribuinte configuracao;
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    public UsuarioPermissaoAprovar() {
    }

    public UsuarioPermissaoAprovar(ConfiguracaoPortalContribuinte configuracao, UsuarioSistema usuarioSistema) {
        this.configuracao = configuracao;
        this.usuarioSistema = usuarioSistema;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoPortalContribuinte getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(ConfiguracaoPortalContribuinte configuracao) {
        this.configuracao = configuracao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
