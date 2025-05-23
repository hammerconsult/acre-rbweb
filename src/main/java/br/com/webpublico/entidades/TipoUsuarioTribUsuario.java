/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoUsuarioTributario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@Etiqueta(value = "Tipo de Usuário do Tributário")
@GrupoDiagrama(nome = "Segurança")
@Audited
@Entity
public class TipoUsuarioTribUsuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VigenciaTribUsuario vigenciaTribUsuario;
    @Enumerated(EnumType.STRING)
    private TipoUsuarioTributario tipoUsuarioTributario;
    private Boolean supervisor;
    @Transient
    private Long criadoEm;

    public TipoUsuarioTribUsuario() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Boolean supervisor) {
        this.supervisor = supervisor;
    }

    public TipoUsuarioTributario getTipoUsuarioTributario() {
        return tipoUsuarioTributario;
    }

    public void setTipoUsuarioTributario(TipoUsuarioTributario tipoUsuarioTributario) {
        this.tipoUsuarioTributario = tipoUsuarioTributario;
    }

    public VigenciaTribUsuario getVigenciaTribUsuario() {
        return vigenciaTribUsuario;
    }

    public void setVigenciaTribUsuario(VigenciaTribUsuario vigenciaTribUsuario) {
        this.vigenciaTribUsuario = vigenciaTribUsuario;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.TipoUsuarioTribUsuario[ id=" + id + " ]";
    }

}
