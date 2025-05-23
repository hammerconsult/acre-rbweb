package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@GrupoDiagrama(nome = "Segurança")
@Entity
@Audited
@Etiqueta("Recursos do Usuário do Sistema")

public class RecursosUsuario implements Serializable {

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
    @Transient
    private Long criadoEm;

    public RecursosUsuario() {
        criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
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
            ", criadoEm=" + criadoEm +
            '}';
    }
}
