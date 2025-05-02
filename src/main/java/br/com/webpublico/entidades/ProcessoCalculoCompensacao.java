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
public class ProcessoCalculoCompensacao extends ProcessoCalculo {

    @OneToMany(orphanRemoval = true, mappedBy = "processoCalculo", cascade = CascadeType.ALL)
    private List<CalculoCompensacao> calculoCompensacao;
    @Transient
    private Long criadoEm;

    public ProcessoCalculoCompensacao() {
        this.criadoEm = System.nanoTime();
        this.calculoCompensacao = new ArrayList<CalculoCompensacao>();
    }

    public List<CalculoCompensacao> getCalculoCompensacao() {
        return calculoCompensacao;
    }

    public void setCalculoCompensacao(List<CalculoCompensacao> calculoCompensacao) {
        this.calculoCompensacao = calculoCompensacao;
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
        if (!(object instanceof ProcessoCalculoCompensacao)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ProcessoCalculoCompensacao[ id=" + super.getId() + " ]";
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return calculoCompensacao;
    }

}
