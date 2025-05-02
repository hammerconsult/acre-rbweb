/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.CamposAutoInfracao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.envers.Audited;

/**
 *
 * @author André Gustavo
 */
@Entity
@Audited
@Table(name = "INCONSISAUTOINFRARBTRANS")
public class CampoInconsistenciaAutoInfracaoRBTrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AnaliseAutuacaoRBTrans analiseAutuacaoRBTrans;
    @Enumerated(EnumType.STRING)
    private CamposAutoInfracao camposAutoInfracao;
    @Transient
    private Long criadoEm;

    public CampoInconsistenciaAutoInfracaoRBTrans() {
        criadoEm = System.nanoTime();
    }

    public CamposAutoInfracao getCamposAutoInfracao() {
        return camposAutoInfracao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setCamposAutoInfracao(CamposAutoInfracao camposAutoInfracao) {
        this.camposAutoInfracao = camposAutoInfracao;
    }

    public AnaliseAutuacaoRBTrans getAnaliseAutuacaoRBTrans() {
        return analiseAutuacaoRBTrans;
    }

    public void setAnaliseAutuacaoRBTrans(AnaliseAutuacaoRBTrans analiseAutuacaoRBTrans) {
        this.analiseAutuacaoRBTrans = analiseAutuacaoRBTrans;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.CampoInconsistenciaAutoInfracaoRBTrans[ id=" + id + " ]";
    }
}
