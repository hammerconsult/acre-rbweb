/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.hibernate.envers.Audited; import javax.persistence.Cacheable;

/**
 *
 * @author Fabio
 */
@Etiqueta(value = "Vigência do Usuário")
@GrupoDiagrama(nome = "Segurança")
@Audited
@Entity
public class VigenciaTribUsuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vigenciaInicial;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vigenciaFinal;
    @OneToMany(mappedBy = "vigenciaTribUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoUsuarioTribUsuario> tipoUsuarioTribUsuarios;
    @OneToMany(mappedBy = "vigenciaTribUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LotacaoTribUsuario> lotacaoTribUsuarios;
    @OneToMany(mappedBy = "vigenciaTribUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AutorizacaoTributarioUsuario> autorizacaoTributarioUsuarios;
    @Transient
    private Long criadoEm;

    public VigenciaTribUsuario() {
        this.criadoEm = System.nanoTime();
        this.tipoUsuarioTribUsuarios = new ArrayList<TipoUsuarioTribUsuario>();
        this.lotacaoTribUsuarios = new ArrayList<LotacaoTribUsuario>();
        this.autorizacaoTributarioUsuarios = new ArrayList<AutorizacaoTributarioUsuario>();
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

    public Date getVigenciaInicial() {
        return vigenciaInicial;
    }

    public void setVigenciaInicial(Date vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    public Date getVigenciaFinal() {
        return vigenciaFinal;
    }

    public void setVigenciaFinal(Date vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<TipoUsuarioTribUsuario> getTipoUsuarioTribUsuarios() {
        return tipoUsuarioTribUsuarios;
    }

    public void setTipoUsuarioTribUsuarios(List<TipoUsuarioTribUsuario> tipoUsuarioTribUsuarios) {
        this.tipoUsuarioTribUsuarios = tipoUsuarioTribUsuarios;
    }

    public List<LotacaoTribUsuario> getLotacaoTribUsuarios() {
        return lotacaoTribUsuarios;
    }

    public void setLotacaoTribUsuarios(List<LotacaoTribUsuario> lotacaoTribUsuarios) {
        this.lotacaoTribUsuarios = lotacaoTribUsuarios;
    }

    public List<AutorizacaoTributarioUsuario> getAutorizacaoTributarioUsuarios() {
        return autorizacaoTributarioUsuarios;
    }

    public void setAutorizacaoTributarioUsuarios(List<AutorizacaoTributarioUsuario> autorizacaoTributarioUsuarios) {
        this.autorizacaoTributarioUsuarios = autorizacaoTributarioUsuarios;
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
        return "br.com.webpublico.entidades.VigenciaTribUsuario[ id=" + id + " ]";
    }

}
