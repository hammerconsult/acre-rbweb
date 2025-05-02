package br.com.webpublico.entidades;


import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 27/09/2016.
 */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@GrupoDiagrama(nome = "tributario")
@Etiqueta(value = "Cálculo de Contribuicao de Melhoria")
@Table(name = "CALCCONTRIBUICAOMELHORIA")
public class CalculoContribuicaoMelhoria extends Calculo implements Serializable {

    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Processo de Cálculo de Contribuição de Melhoria")
    private ProcessoCalculoContribuicaoMelhoria processoCalcContMelhoria;
    @OneToMany(mappedBy = "calculoContribuicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCalculoContribuicaoMelhoria> itensCalculoContribuicaoMelhorias;
    @ManyToOne
    private ContribuicaoMelhoriaLancamento contribuicaoMelhoria;

    public CalculoContribuicaoMelhoria() {
        super();
        itensCalculoContribuicaoMelhorias = new ArrayList<>();
        super.setTipoCalculo(TipoCalculo.CONTRIBUICAO_MELHORIA);
    }

    public CalculoContribuicaoMelhoria(Long id) {
        this();
        setId(id);
    }

    public ProcessoCalculoContribuicaoMelhoria getProcessoCalcContMelhoria() {
        return processoCalcContMelhoria;
    }

    public void setProcessoCalcContMelhoria(ProcessoCalculoContribuicaoMelhoria processoCalcContMelhoria) {
        setProcessoCalculo(processoCalcContMelhoria);
        this.processoCalcContMelhoria = processoCalcContMelhoria;
    }

    public List<ItemCalculoContribuicaoMelhoria> getItensCalculoContribuicaoMelhorias() {
        return itensCalculoContribuicaoMelhorias == null ? null : new ArrayList<>(itensCalculoContribuicaoMelhorias);
    }

    public void setItensCalculoContribuicaoMelhorias(List<ItemCalculoContribuicaoMelhoria> itensCalculoContribuicaoMelhorias) {
        this.itensCalculoContribuicaoMelhorias = itensCalculoContribuicaoMelhorias == null ? null : new ArrayList<>(itensCalculoContribuicaoMelhorias);
    }

    @Override
    public String toString() {
        return (processoCalcContMelhoria != null
            && processoCalcContMelhoria.getContribuicaoMelhoriaEdital() != null) ?
            processoCalcContMelhoria.getContribuicaoMelhoriaEdital().toString() : "Contribuiçao de Melhoria null"
            + " - "
            + (getCadastro() != null ? getCadastro().toString() : "Cadastro null");
    }

    public ContribuicaoMelhoriaLancamento getContribuicaoMelhoria() {
        return contribuicaoMelhoria;
    }

    public void setContribuicaoMelhoria(ContribuicaoMelhoriaLancamento contribuicaoMelhoria) {
        this.contribuicaoMelhoria = contribuicaoMelhoria;
    }
}
