/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.envers.Audited; import javax.persistence.Cacheable;

/**
 *
 * @author Fabio
 */
@Etiqueta(value = "Lotação do Usuário do Tributário")
@GrupoDiagrama(nome = "Segurança")
@Audited
@Entity
public class LotacaoTribUsuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VigenciaTribUsuario vigenciaTribUsuario;
    @ManyToOne
    private LotacaoVistoriadora lotacao;
    @Transient
    private Long criadoEm;

    public LotacaoTribUsuario() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LotacaoVistoriadora getLotacao() {
        return lotacao;
    }

    public void setLotacao(LotacaoVistoriadora lotacao) {
        this.lotacao = lotacao;
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
        return "br.com.webpublico.entidades.LotacaoTribUsuario[ id=" + id + " ]";
    }

}
