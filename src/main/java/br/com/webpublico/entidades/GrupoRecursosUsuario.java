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
@Etiqueta("Grupos de Recursos do Usuário do Sistema")

public class GrupoRecursosUsuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private GrupoRecurso grupoRecurso;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    private Boolean leitura;
    private Boolean escrita;
    private Boolean exclusao;
    @Transient
    private Long criadoEm;

    public GrupoRecursosUsuario() {
        criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public GrupoRecurso getGrupoRecurso() {
        return grupoRecurso;
    }

    public void setGrupoRecurso(GrupoRecurso grupoRecurso) {
        this.grupoRecurso = grupoRecurso;
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

}
