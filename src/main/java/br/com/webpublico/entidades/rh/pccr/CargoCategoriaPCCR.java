package br.com.webpublico.entidades.rh.pccr;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 20/06/14
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class CargoCategoriaPCCR implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    private Date finalVigencia;

    @ManyToOne
    private Cargo cargo;

    @ManyToOne
    private CategoriaPCCR categoriaPCCR;
    @ManyToOne
    private ProgressaoPCCR progressaoPCCR;
    @Transient
    private Long criadoEm = System.nanoTime();

    public Long getCriadoEm() {
        return criadoEm;
    }

    public ProgressaoPCCR getProgressaoPCCR() {
        return progressaoPCCR;
    }

    public void setProgressaoPCCR(ProgressaoPCCR progressaoPCCR) {
        this.progressaoPCCR = progressaoPCCR;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public CategoriaPCCR getCategoriaPCCR() {
        return categoriaPCCR;
    }

    public void setCategoriaPCCR(CategoriaPCCR categoriaPCCR) {
        this.categoriaPCCR = categoriaPCCR;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
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

    public CargoCategoriaPCCR(Date inicioVigencia, Date finalVigencia, Cargo cargo, CategoriaPCCR categoriaPCCR) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.cargo = cargo;
        this.categoriaPCCR = categoriaPCCR;
    }

    public CargoCategoriaPCCR() {
    }


    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return getCargo().getDescricao();
    }


}
