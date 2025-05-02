package br.com.webpublico.entidades;

import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 21/02/14
 * Time: 18:56
 * To change this template use File | Settings | File Templates.
 */

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Etiqueta("Opção Salarial do Vinculo")
public class OpcaoSalarialVinculoFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @ManyToOne
    private OpcaoSalarialCC opcaoSalarialCC;
    @ManyToOne
    private VinculoFP vinculoFP;
    private Double percentual;
    @Transient
    @Invisivel
    private Long criadoEm = System.nanoTime();

    public OpcaoSalarialVinculoFP() {
        criadoEm = System.nanoTime();
        percentual = 100.0;
    }

    public Double getPercentual() {
        return percentual;
    }

    public void setPercentual(Double percentual) {
        this.percentual = percentual;
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

    public OpcaoSalarialCC getOpcaoSalarialCC() {
        return opcaoSalarialCC;
    }

    public void setOpcaoSalarialCC(OpcaoSalarialCC opcaoSalarialCC) {
        this.opcaoSalarialCC = opcaoSalarialCC;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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
        return opcaoSalarialCC + "";    //To change body of overridden methods use File | Settings | File Templates.
    }
}
