package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by AndreGustavo on 16/07/2014.
 */
@Entity
@Audited
@Etiqueta("Item de Cálculo da RBTrans")
public class ItemCalculoRBTrans extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoRBTrans calculoRBTrans;
    @OneToOne
    private Tributo tributo;
    private BigDecimal valor;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoRBTrans getCalculoRBTrans() {
        return calculoRBTrans;
    }

    public void setCalculoRBTrans(CalculoRBTrans calculoRBTrans) {
        this.calculoRBTrans = calculoRBTrans;
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
