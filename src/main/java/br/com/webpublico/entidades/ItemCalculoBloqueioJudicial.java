package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
@Etiqueta("Item Cálculo de Bloqueio Judicial")
@Table(name = "ITEMCALCBLOQUEIOJUDICIAL")
public class ItemCalculoBloqueioJudicial extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoBloqueioJudicial calculoBloqueioJudicial;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal proporcaoResidual;
    private BigDecimal valor;

    public ItemCalculoBloqueioJudicial() {
        proporcaoResidual = BigDecimal.ZERO;
        valor = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoBloqueioJudicial getCalculoBloqueioJudicial() {
        return calculoBloqueioJudicial;
    }

    public void setCalculoBloqueioJudicial(CalculoBloqueioJudicial calculoBloqueioJudicial) {
        this.calculoBloqueioJudicial = calculoBloqueioJudicial;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getProporcaoResidual() {
        return proporcaoResidual;
    }

    public void setProporcaoResidual(BigDecimal proporcaoResidual) {
        this.proporcaoResidual = proporcaoResidual;
    }
}
