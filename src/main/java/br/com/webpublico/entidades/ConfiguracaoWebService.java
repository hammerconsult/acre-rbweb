package br.com.webpublico.entidades;

import br.com.webpublico.enums.tributario.TipoWebService;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 10/01/2017.
 */
@Entity
@Audited
public class ConfiguracaoWebService extends SuperEntidade {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoWebService tipo;
    private String chave;
    private String url;
    private String usuario;
    private String senha;
    private String detalhe;
    @ManyToOne
    private ConfiguracaoTributario configuracaoTributario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoWebService getTipo() {
        return tipo;
    }

    public void setTipo(TipoWebService tipo) {
        this.tipo = tipo;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public String getSenhaComAsteristicos() {
        if (senha != null) {
            String asteriscos = "";
            for (int i = 0; i < senha.length(); i++) {
                asteriscos += "*";
            }
            return asteriscos;
        }
        return "";
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return configuracaoTributario;
    }

    public void setConfiguracaoTributario(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }
}
