package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@GrupoDiagrama(nome = "Conta Corrente")
@Audited
@Entity
@Table(name = "PROCESSOCALCCONTACORRENTE")
public class ProcessoCalculoContaCorrente extends ProcessoCalculo {

    @OneToMany(orphanRemoval = true, mappedBy = "processoCalculo", cascade = CascadeType.ALL)
    private List<CalculoContaCorrente> calculoContaCorrente;
    @Transient
    private Long criadoEm;

    public ProcessoCalculoContaCorrente() {
        this.criadoEm = System.nanoTime();
        this.calculoContaCorrente = new ArrayList<CalculoContaCorrente>();
    }

    public List<CalculoContaCorrente> getCalculoContaCorrente() {
        return calculoContaCorrente;
    }

    public void setCalculoContaCorrente(List<CalculoContaCorrente> calculoContaCorrente) {
        this.calculoContaCorrente = calculoContaCorrente;
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
        if (!(object instanceof ProcessoCalculoContaCorrente)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ProcessoCalculoContaCorrente[ id=" + super.getId() + " ]";
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return calculoContaCorrente;
    }

}
