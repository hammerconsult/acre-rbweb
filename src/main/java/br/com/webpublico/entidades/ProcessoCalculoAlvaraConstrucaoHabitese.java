package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Alvara")
@Table(name = "PROCESSOCALCALVACONSTHABI")
public class ProcessoCalculoAlvaraConstrucaoHabitese extends ProcessoCalculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToOne
    private AlvaraConstrucao alvaraConstrucao;
    @OneToOne
    private Habitese habitese;
    @OneToMany(mappedBy = "procCalcAlvaraConstruHabit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoAlvaraConstrucaoHabitese> calculosAlvaraConstrucaoHabitese;

    public List<CalculoAlvaraConstrucaoHabitese> getCalculosAlvaraConstrucaoHabitese() {
        if (calculosAlvaraConstrucaoHabitese == null) {
            calculosAlvaraConstrucaoHabitese = Lists.newArrayList();
        }
        return calculosAlvaraConstrucaoHabitese;
    }

    public void setCalculosAlvaraConstrucaoHabitese(List<CalculoAlvaraConstrucaoHabitese> calculosAlvaraConstrucaoHabitese) {
        this.calculosAlvaraConstrucaoHabitese = calculosAlvaraConstrucaoHabitese;
    }

    public AlvaraConstrucao getAlvaraConstrucao() {
        return alvaraConstrucao;
    }

    public void setAlvaraConstrucao(AlvaraConstrucao alvaraConstrucao) {
        this.alvaraConstrucao = alvaraConstrucao;
    }

    public Habitese getHabitese() {
        return habitese;
    }

    public void setHabitese(Habitese habitese) {
        this.habitese = habitese;
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return calculosAlvaraConstrucaoHabitese;
    }
}
