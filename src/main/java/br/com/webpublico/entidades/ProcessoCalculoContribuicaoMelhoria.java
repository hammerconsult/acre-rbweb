package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "tributario")
@Etiqueta(value = "ProcessoCalculoContribuicaoMelhoria", genero = "M")
@Table(name = "PROCESSOCALCCONTMELHORIA")
public class ProcessoCalculoContribuicaoMelhoria extends ProcessoCalculo implements Serializable {

    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Edital de Contribuição de Melhoria")
    private ContribuicaoMelhoriaEdital contribuicaoMelhoriaEdital;
    @OneToMany(mappedBy = "processoCalcContMelhoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoContribuicaoMelhoria> calculosContribuicaoMelhorias;

    public ProcessoCalculoContribuicaoMelhoria() {
        super();
        calculosContribuicaoMelhorias = new ArrayList<>();
    }

    @Override
    public List<CalculoContribuicaoMelhoria> getCalculos() {
        return calculosContribuicaoMelhorias;
    }

    public List<CalculoContribuicaoMelhoria> getCalculosContribuicaoMelhorias() {
        return calculosContribuicaoMelhorias;
    }

    public void setCalculosContribuicaoMelhorias(List<CalculoContribuicaoMelhoria> calculosContribuicaoMelhorias) {
        this.calculosContribuicaoMelhorias = calculosContribuicaoMelhorias;
    }

    public ContribuicaoMelhoriaEdital getContribuicaoMelhoriaEdital() {
        return contribuicaoMelhoriaEdital;
    }

    public void setContribuicaoMelhoriaEdital(ContribuicaoMelhoriaEdital contribuicaoMelhoriaEdital) {
        this.contribuicaoMelhoriaEdital = contribuicaoMelhoriaEdital;
    }

    @Override
    public String toString() {
        return getContribuicaoMelhoriaEdital().toString();
    }
}
