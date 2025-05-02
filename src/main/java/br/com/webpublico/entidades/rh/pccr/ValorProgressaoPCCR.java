package br.com.webpublico.entidades.rh.pccr;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 26/06/14
 * Time: 17:36
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ValorProgressaoPCCR implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    private BigDecimal valor;
    @ManyToOne
    private ProgressaoPCCR progressaoPCCR;
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    private ReferenciaProgressaoPCCR referenciaProgressaoPCCR;
    @Transient
    private Long criadoEm = System.nanoTime();

    public Long getId() {
        return id;
    }

    public Long getCriadoEm() {
        return criadoEm;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ProgressaoPCCR getProgressaoPCCR() {
        return progressaoPCCR;
    }

    public void setProgressaoPCCR(ProgressaoPCCR progressaoPCCR) {
        this.progressaoPCCR = progressaoPCCR;
    }

    public ReferenciaProgressaoPCCR getReferenciaProgressaoPCCR() {
        return referenciaProgressaoPCCR;
    }

    public void setReferenciaProgressaoPCCR(ReferenciaProgressaoPCCR referenciaProgressaoPCCR) {
        this.referenciaProgressaoPCCR = referenciaProgressaoPCCR;
    }

    public ValorProgressaoPCCR() {
    }

    @Override
    public boolean equals(Object o) {
       return IdentidadeDaEntidade.calcularEquals(this,o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return "ValorProgressaoPCCR{" +
                "id=" + id +
                ", inicioVigencia=" + inicioVigencia +
                ", finalVigencia=" + finalVigencia +
                ", valor=" + valor +
                ", progressaoPCCR=" + progressaoPCCR +
                ", referenciaProgressaoPCCR=" + referenciaProgressaoPCCR +
                '}';
    }
}
