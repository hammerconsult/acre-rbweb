package br.com.webpublico.entidades;

import br.com.webpublico.entidades.usertype.DireitosUserType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity

@Audited
@TypeDef(name = "direitos", typeClass = DireitosUserType.class)
public class ItemGrupoUsuario extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private GrupoUsuario grupoUsuario;

    @ManyToOne
    private GrupoRecurso grupoRecurso;

    private Boolean leitura;
    private Boolean escrita;
    private Boolean exclusao;

    public ItemGrupoUsuario() {
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
