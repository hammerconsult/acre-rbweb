/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoUsuarioTributario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Fabio
 */
@Etiqueta(value = "Tipo de Usuário do Tributário")
@GrupoDiagrama(nome = "Segurança")
@Audited
@Entity

public class TipoUsuarioTribUsuario extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VigenciaTribUsuario vigenciaTribUsuario;
    @Enumerated(EnumType.STRING)
    private TipoUsuarioTributario tipoUsuarioTributario;
    private Boolean supervisor;

    public TipoUsuarioTribUsuario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getSupervisor() {
        return supervisor != null ? supervisor : Boolean.FALSE;
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

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.TipoUsuarioTribUsuario[ id=" + id + " ]";
    }
}
