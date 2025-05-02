/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Wellington
 */
@Entity

@Audited
@GrupoDiagrama(nome = "DividaDiversa")
@Etiqueta("Dívida Diversa")
public class CancelamentoDividaDiversa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private Long criadoEm;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCancelamento;
    @ManyToOne
    private UsuarioSistema usuarioCancelamento;
    private String motivo;

    public CancelamentoDividaDiversa() {
        criadoEm = System.nanoTime();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public UsuarioSistema getUsuarioCancelamento() {
        return usuarioCancelamento;
    }

    public void setUsuarioCancelamento(UsuarioSistema usuarioCancelamento) {
        this.usuarioCancelamento = usuarioCancelamento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

}
