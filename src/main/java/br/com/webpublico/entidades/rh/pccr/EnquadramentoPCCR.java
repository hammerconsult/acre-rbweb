package br.com.webpublico.entidades.rh.pccr;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 20/06/14
 * Time: 18:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class EnquadramentoPCCR implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @ManyToOne
    private ProgressaoPCCR progressaoPCCR;
    @ManyToOne
    private CategoriaPCCR categoriaPCCR;

    private BigDecimal vencimentoBase;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCadastro;

    public EnquadramentoPCCR() {
    }

    @Override
    public String toString() {
        return "EnquadramentoPCCR{" +
                "id=" + id +
                ", inicioVigencia=" + inicioVigencia +
                ", finalVigencia=" + finalVigencia +
                ", progressaoPCCR=" + progressaoPCCR +
                ", categoriaPCCR=" + categoriaPCCR +
                ", vencimentoBase=" + vencimentoBase +
                ", dataCadastro=" + dataCadastro +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public ProgressaoPCCR getProgressaoPCCR() {
        return progressaoPCCR;
    }

    public void setProgressaoPCCR(ProgressaoPCCR progressaoPCCR) {
        this.progressaoPCCR = progressaoPCCR;
    }

    public CategoriaPCCR getCategoriaPCCR() {
        return categoriaPCCR;
    }

    public void setCategoriaPCCR(CategoriaPCCR categoriaPCCR) {
        this.categoriaPCCR = categoriaPCCR;
    }

    public BigDecimal getVencimentoBase() {
        return vencimentoBase;
    }

    public void setVencimentoBase(BigDecimal vencimentoBase) {
        this.vencimentoBase = vencimentoBase;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
