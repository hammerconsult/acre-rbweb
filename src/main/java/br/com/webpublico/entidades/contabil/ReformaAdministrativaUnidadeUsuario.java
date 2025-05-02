package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Audited
@Entity
@Table(name = "REFORMAADMUNIDADEUSUARIO")
public class ReformaAdministrativaUnidadeUsuario extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ReformaAdministrativaUnidade reformaUnidade;
    @ManyToOne
    @Obrigatorio
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    @Obrigatorio
    private UnidadeOrganizacional unidadeDestino;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Transient
    private Boolean marcado;

    public ReformaAdministrativaUnidadeUsuario() {
        this.marcado = Boolean.FALSE;
    }

    public ReformaAdministrativaUnidadeUsuario(ReformaAdministrativaUnidade reformaUnidade, UsuarioSistema usuarioSistema, UnidadeOrganizacional unidadeDestino) {
        this.reformaUnidade = reformaUnidade;
        this.usuarioSistema = usuarioSistema;
        this.unidadeDestino = unidadeDestino;
        this.marcado = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReformaAdministrativaUnidade getReformaUnidade() {
        return reformaUnidade;
    }

    public void setReformaUnidade(ReformaAdministrativaUnidade reformaUnidade) {
        this.reformaUnidade = reformaUnidade;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public UnidadeOrganizacional getUnidadeDestino() {
        return unidadeDestino;
    }

    public void setUnidadeDestino(UnidadeOrganizacional unidadeDestino) {
        this.unidadeDestino = unidadeDestino;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getMarcado() {
        return marcado;
    }

    public void setMarcado(Boolean marcado) {
        this.marcado = marcado;
    }

    @Override
    public String toString() {
        return usuarioSistema.getNome();
    }
}
