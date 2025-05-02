package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
public class ItemCalculoCompensacao extends ItemCalculo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valor;
    @ManyToOne
    private Tributo tributo;
    @ManyToOne
    private CalculoCompensacao calculoCompensacao;

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

    public CalculoCompensacao getCalculoCompensacao() {
        return calculoCompensacao;
    }

    public void setCalculoCompensacao(CalculoCompensacao calculoCompensacao) {
        this.calculoCompensacao = calculoCompensacao;
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
