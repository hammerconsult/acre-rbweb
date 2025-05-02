package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Tributário")
@Entity
@Audited
public class BiAgrupadorTaxaTributo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private BiAgrupadorTaxa biAgrupadorTaxa;
    @ManyToOne
    private Tributo tributo;
    @Transient
    private Long criadoEm;

    public BiAgrupadorTaxaTributo() {
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

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public BiAgrupadorTaxa getBiAgrupadorTaxa() {
        return biAgrupadorTaxa;
    }

    public void setBiAgrupadorTaxa(BiAgrupadorTaxa biAgrupadorTaxa) {
        this.biAgrupadorTaxa = biAgrupadorTaxa;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }
}
