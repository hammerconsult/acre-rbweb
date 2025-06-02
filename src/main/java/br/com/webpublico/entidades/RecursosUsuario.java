package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Fabio
 */
@GrupoDiagrama(nome = "Segurança")
@Entity
@Audited
@Etiqueta("Recursos do Usuário do Sistema")

public class RecursosUsuario extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private RecursoSistema recursoSistema;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    private Boolean leitura;
    private Boolean escrita;
    private Boolean exclusao;

    public RecursosUsuario() {
    }

    public RecursoSistema getRecursoSistema() {
        return recursoSistema;
    }

    public void setRecursoSistema(RecursoSistema recursoSistema) {
        this.recursoSistema = recursoSistema;
    }

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

    public Boolean getEscrita() {
        return escrita;
    }

    public void setEscrita(Boolean escrita) {
        this.escrita = escrita;
    }

    public Boolean getExclusao() {
        return exclusao;
    }

    public void setExclusao(Boolean exclusao) {
        this.exclusao = exclusao;
    }

    public Boolean getLeitura() {
        return leitura;
    }

    public void setLeitura(Boolean leitura) {
        this.leitura = leitura;
    }

    @Override
    public String toString() {
        return "RecursosUsuario{" +
            ", recursoSistema=" + recursoSistema +
            ", leitura=" + leitura +
            ", escrita=" + escrita +
            ", exclusao=" + exclusao +
            '}';
    }
}
