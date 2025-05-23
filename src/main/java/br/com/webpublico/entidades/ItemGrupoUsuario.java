package br.com.webpublico.entidades;

import br.com.webpublico.entidades.usertype.DireitosUserType;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity

@Audited
@TypeDef(name = "direitos", typeClass = DireitosUserType.class)
public class ItemGrupoUsuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private GrupoUsuario grupoUsuario;
    @ManyToOne
    private GrupoRecurso grupoRecurso;
    //@Type(type = "direitos")
    //private Set<Direito> direitos = Sets.newHashSet();
    private Boolean leitura;
    private Boolean escrita;
    private Boolean exclusao;
    @Transient
    private Long criadoEm;

    public ItemGrupoUsuario() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoUsuario getGrupoUsuario() {
        return grupoUsuario;
    }

    public void setGrupoUsuario(GrupoUsuario grupoUsuario) {
        this.grupoUsuario = grupoUsuario;
    }

    public GrupoRecurso getGrupoRecurso() {
        return grupoRecurso;
    }

    public void setGrupoRecurso(GrupoRecurso grupoRecursoSistema) {
        this.grupoRecurso = grupoRecursoSistema;
    }

    //public Set<Direito> getDireitos() {
    //    return direitos;
    //}

    //public void setDireitos(Set<Direito> direitos) {
    //    this.direitos = direitos;
    //}

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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
