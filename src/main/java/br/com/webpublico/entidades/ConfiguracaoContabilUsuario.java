package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoUsuarioGestor;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@GrupoDiagrama(nome = "Contábil")
@Audited
@Table(name = "CONFIGCONTABILUSUARIO")
public class ConfiguracaoContabilUsuario extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoContabil configuracaoContabil;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Usuário Gestor")
    @Obrigatorio
    private TipoUsuarioGestor tipoUsuarioGestor;

    public ConfiguracaoContabilUsuario() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoContabil getConfiguracaoContabil() {
        return configuracaoContabil;
    }

    public void setConfiguracaoContabil(ConfiguracaoContabil configuracaoContabil) {
        this.configuracaoContabil = configuracaoContabil;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoUsuarioGestor getTipoUsuarioGestor() {
        return tipoUsuarioGestor;
    }

    public void setTipoUsuarioGestor(TipoUsuarioGestor tipoUsuarioGestor) {
        this.tipoUsuarioGestor = tipoUsuarioGestor;
    }
}
