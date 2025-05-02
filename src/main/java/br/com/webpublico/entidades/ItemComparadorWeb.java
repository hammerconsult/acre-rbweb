package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 03/02/2015
 * Time: 18:05
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ItemComparadorWeb implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ComparadorWeb comparadorWeb;
    @ManyToOne
    private VinculoFP vinculoFP;
    private String matricula;
    private String contrato;
    private String comentario;
    private String critica;

    @Transient
    public Long criadoEm;

    public ItemComparadorWeb() {
        criadoEm = System.nanoTime();
    }

    public String getCritica() {
        return critica;
    }

    public void setCritica(String critica) {
        this.critica = critica;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComparadorWeb getComparadorWeb() {
        return comparadorWeb;
    }

    public void setComparadorWeb(ComparadorWeb comparadorWeb) {
        this.comparadorWeb = comparadorWeb;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matriucla) {
        this.matricula = matriucla;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
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
}
