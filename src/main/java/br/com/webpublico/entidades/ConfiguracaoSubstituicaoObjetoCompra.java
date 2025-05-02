package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;


@Entity
@Audited
@Table(name = "CONFIGSUBSTITUICAOOBJCOMP")
@GrupoDiagrama(nome = "Configuração Licitação")
@Etiqueta("Configurações de Substitução Objeto de Compra")
public class ConfiguracaoSubstituicaoObjetoCompra extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Configuração de Licitação")
    @ManyToOne
    private ConfiguracaoLicitacao configuracaoLicitacao;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Usuário Sistema")
    private UsuarioSistema usuarioSistema;

    public ConfiguracaoSubstituicaoObjetoCompra() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoLicitacao getConfiguracaoLicitacao() {
        return configuracaoLicitacao;
    }

    public void setConfiguracaoLicitacao(ConfiguracaoLicitacao configuracaoLicitacao) {
        this.configuracaoLicitacao = configuracaoLicitacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
