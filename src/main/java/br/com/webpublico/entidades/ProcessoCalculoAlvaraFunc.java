package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@GrupoDiagrama(nome = "Alvara")
@Audited
@Entity

public class ProcessoCalculoAlvaraFunc extends ProcessoCalculo {
    @OneToMany(orphanRemoval = true, mappedBy = "processoCalculo", cascade = CascadeType.ALL)
    private List<CalculoAlvaraFuncionamento> calculosAlvaraFunc;
    @Transient
    private Long criadoEm;

    public ProcessoCalculoAlvaraFunc() {
        this.criadoEm = System.nanoTime();
        this.calculosAlvaraFunc = new ArrayList<CalculoAlvaraFuncionamento>();
    }

    public List<CalculoAlvaraFuncionamento> getCalculosAlvaraFunc() {
        return calculosAlvaraFunc;
    }

    public void setCalculosAlvaraFunc(List<CalculoAlvaraFuncionamento> calculosAlvaraFunc) {
        this.calculosAlvaraFunc = calculosAlvaraFunc;
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
        if (!(object instanceof ProcessoCalculoAlvaraFunc)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ProcessoCalculoAlvaraFunc[ id=" + super.getId() + " ]";
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return calculosAlvaraFunc;
    }

}
