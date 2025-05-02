package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wanderley
 * Date: 05/11/13
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ItemCalculoMultaAcessoria extends ItemCalculo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valor;
    @ManyToOne
    private Tributo tributo;
    @ManyToOne
    private CalculoMultaAcessoria calculoMultaAcessoria;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public CalculoMultaAcessoria getCalculoMultaAcessoria() {
        return calculoMultaAcessoria;
    }

    public void setCalculoMultaAcessoria(CalculoMultaAcessoria calculoMultaAcessoria) {
        this.calculoMultaAcessoria = calculoMultaAcessoria;
    }

    @Override
    public Tributo getTributo() {
        return tributo;
    }

    @Override
    public BigDecimal getValor() {
        return valor;
    }
}
