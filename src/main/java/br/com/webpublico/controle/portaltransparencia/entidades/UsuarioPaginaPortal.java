package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by renato on 30/08/2016.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class UsuarioPaginaPortal extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @NotNull
    @Length(minimo = 1, maximo = 50)
    @Column(length = 50, unique = true, nullable = false)
    @Etiqueta("Usuário")
    private String login;

    @NotNull
    @Length(minimo = 5, maximo = 60)
    @Column(name = "senha", length = 60)
    @Etiqueta("Senha")
    private String senha;

    private String email;

    private Boolean ativo;

    public UsuarioPaginaPortal() {
        this.ativo = Boolean.TRUE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getAtivo() {
        if (ativo == null) {
            ativo = Boolean.FALSE;
        }
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
