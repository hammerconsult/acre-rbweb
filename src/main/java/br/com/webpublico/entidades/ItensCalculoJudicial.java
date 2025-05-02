package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 29/05/15
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ItensCalculoJudicial implements Serializable{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoPagamentoJudicial calculoPagamentoJudicial;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal valor;
    @Transient
    private Long criadoEm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoPagamentoJudicial getCalculoPagamentoJudicial() {
        return calculoPagamentoJudicial;
    }

    public void setCalculoPagamentoJudicial(CalculoPagamentoJudicial calculoPagamentoJudicial) {
        this.calculoPagamentoJudicial = calculoPagamentoJudicial;
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

    public ItensCalculoJudicial() {
        criadoEm = System.nanoTime();;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
