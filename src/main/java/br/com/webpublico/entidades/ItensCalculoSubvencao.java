package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by tharlyson on 04/12/19.
 */
@Entity
@Audited
public class ItensCalculoSubvencao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoPagamentoSubvencao calculoPagamentoSubvencao;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal valor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoPagamentoSubvencao getCalculoPagamentoSubvencao() {
        return calculoPagamentoSubvencao;
    }

    public void setCalculoPagamentoSubvencao(CalculoPagamentoSubvencao calculoPagamentoSubvencao) {
        this.calculoPagamentoSubvencao = calculoPagamentoSubvencao;
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
}
