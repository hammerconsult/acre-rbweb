package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Alvara")
@Etiqueta("Cálculo de Alvará de Construção e Habite-se")
@Table(name = "CALCALVARACONSTRUCHABITESE")
public class CalculoAlvaraConstrucaoHabitese extends Calculo {

    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private ProcessoCalculoAlvaraConstrucaoHabitese procCalcAlvaraConstruHabit;
    private BigDecimal areaTotal;
    private BigDecimal valorUFM;
    @Temporal(TemporalType.DATE)
    private Date vencimento;
    @OneToMany(mappedBy = "calcAlvaraConstrucHabitese", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ItemCalculoAlvaraConstrucaoHabitese> itensCalculo;
    @Enumerated(EnumType.STRING)
    private SubTipoCalculo subTipoCalculo;

    public CalculoAlvaraConstrucaoHabitese() {
        super.setTipoCalculo(TipoCalculo.ALVARA_CONSTRUCAO_HABITESE);
        super.setSubDivida(1L);
        itensCalculo = Lists.newArrayList();
        subTipoCalculo = SubTipoCalculo.ALVARA;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public ProcessoCalculoAlvaraConstrucaoHabitese getProcCalcAlvaraConstruHabit() {
        return procCalcAlvaraConstruHabit;
    }

    public void setProcCalcAlvaraConstruHabit(ProcessoCalculoAlvaraConstrucaoHabitese procCalcAlvaraConstruHabit) {
        this.procCalcAlvaraConstruHabit = procCalcAlvaraConstruHabit;
        setProcessoCalculo(procCalcAlvaraConstruHabit);
    }

    public BigDecimal getAreaTotal() {
        return areaTotal != null ? areaTotal : BigDecimal.ZERO;
    }

    public void setAreaTotal(BigDecimal areaTotal) {
        this.areaTotal = areaTotal;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public List<ItemCalculoAlvaraConstrucaoHabitese> getItensCalculo() {
        return itensCalculo;
    }

    public BigDecimal getValorUFM() {
        return valorUFM != null ? valorUFM : BigDecimal.ZERO;
    }

    public void setValorUFM(BigDecimal valorUFM) {
        this.valorUFM = valorUFM;
    }

    public void setItensCalculo(List<ItemCalculoAlvaraConstrucaoHabitese> itensCalculo) {
        this.itensCalculo = itensCalculo;
    }

    public SubTipoCalculo getSubTipoCalculo() {
        return subTipoCalculo;
    }

    public void setSubTipoCalculo(SubTipoCalculo subTipoCalculo) {
        this.subTipoCalculo = subTipoCalculo;
    }

    public enum SubTipoCalculo {
        ALVARA,
        VISTORIA,
        HABITESE
    }
}
