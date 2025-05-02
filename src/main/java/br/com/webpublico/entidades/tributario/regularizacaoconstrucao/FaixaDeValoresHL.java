package br.com.webpublico.entidades.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Lista de Faixa de Valores de Construções Habite-se")
public class FaixaDeValoresHL extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Padrões de Construção")
    private HabitesePadroesConstrucao habitesePadroesConstrucao;
    @Obrigatorio
    @Etiqueta("Área Inicial")
    private BigDecimal areaInicial;
    @Etiqueta("Área Final")
    private BigDecimal areaFinal;
    @Obrigatorio
    @Etiqueta("Valor em UFM")
    private BigDecimal valorUFM;
    @ManyToOne
    @Etiqueta("Faixa de Valores habite-se")
    private HabiteseFaixaDeValores habiteseFaixaDeValores;

    public FaixaDeValoresHL() {
    }

    public FaixaDeValoresHL(HabitesePadroesConstrucao habitesePadroesConstrucao, BigDecimal areaInicial, BigDecimal areaFinal, BigDecimal valorUFM) {
        this.habitesePadroesConstrucao = habitesePadroesConstrucao;
        this.areaInicial = areaInicial;
        this.areaFinal = areaFinal;
        this.valorUFM = valorUFM;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HabitesePadroesConstrucao getHabitesePadroesConstrucao() {
        return habitesePadroesConstrucao;
    }

    public void setHabitesePadroesConstrucao(HabitesePadroesConstrucao habitesePadroesConstrucao) {
        this.habitesePadroesConstrucao = habitesePadroesConstrucao;
    }

    public BigDecimal getAreaInicial() {
        return areaInicial;
    }

    public void setAreaInicial(BigDecimal areaInicial) {
        this.areaInicial = areaInicial;
    }

    public BigDecimal getAreaFinal() {
        if(areaFinal == null) areaFinal = BigDecimal.ZERO;
        return areaFinal;
    }

    public void setAreaFinal(BigDecimal areaFinal) {
        this.areaFinal = areaFinal;
    }

    public BigDecimal getValorUFM() {
        return valorUFM;
    }

    public void setValorUFM(BigDecimal valorUFM) {
        this.valorUFM = valorUFM;
    }

    public HabiteseFaixaDeValores getHabiteseFaixaDeValores() {
        return habiteseFaixaDeValores;
    }

    public void setHabiteseFaixaDeValores(HabiteseFaixaDeValores habiteseFaixaDeValores) {
        this.habiteseFaixaDeValores = habiteseFaixaDeValores;
    }

    @Override
    public String toString() {
        return "FaixaDeValoresHL{" +
            ", habitesePadroesConstrucao=" + habitesePadroesConstrucao +
            ", areaInicial=" + areaInicial +
            ", areaFinal=" + areaFinal +
            ", valorUFM=" + valorUFM +
            '}';
    }
}
