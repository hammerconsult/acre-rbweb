package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
@Etiqueta("Processo de Cálculo de Bloqueio Judicial")
public class ProcessoCalcBloqJudicial extends ProcessoCalculo {
    @ManyToOne
    private BloqueioJudicial bloqueioJudicial;
    @OneToMany(mappedBy = "processoCalcBloqJudicial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoBloqueioJudicial> calculosBloqueio;

    public ProcessoCalcBloqJudicial() {
        this.calculosBloqueio = Lists.newArrayList();
    }

    public BloqueioJudicial getBloqueioJudicial() {
        return bloqueioJudicial;
    }

    public void setBloqueioJudicial(BloqueioJudicial bloqueioJudicial) {
        this.bloqueioJudicial = bloqueioJudicial;
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return calculosBloqueio;
    }

    public List<CalculoBloqueioJudicial> getCalculosBloqueio() {
        return calculosBloqueio;
    }

    public void setCalculosBloqueio(List<CalculoBloqueioJudicial> calculosBloqueio) {
        this.calculosBloqueio = calculosBloqueio;
    }
}
