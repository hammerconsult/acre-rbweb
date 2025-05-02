package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@Table(name = "ITEMCALCALVARACONSTHAB")
@GrupoDiagrama(nome = "Tributario")
public class ItemCalculoAlvaraConstrucaoHabitese extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal valorUFM;
    private BigDecimal valorReal;
    @ManyToOne
    private CalculoAlvaraConstrucaoHabitese calcAlvaraConstrucHabitese;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public BigDecimal getValorUFM() {
        return valorUFM;
    }

    public void setValorUFM(BigDecimal valorUFM) {
        this.valorUFM = valorUFM;
    }

    public BigDecimal getValorReal() {
        return valorReal;
    }

    public void setValorReal(BigDecimal valorReal) {
        this.valorReal = valorReal;
    }

    public CalculoAlvaraConstrucaoHabitese getCalcAlvaraConstrucHabitese() {
        return calcAlvaraConstrucHabitese;
    }

    public void setCalcAlvaraConstrucHabitese(CalculoAlvaraConstrucaoHabitese calcAlvaraConstrucHabitese) {
        this.calcAlvaraConstrucHabitese = calcAlvaraConstrucHabitese;
    }
}
