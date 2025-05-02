package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
@Table(name = "ITEMCANCELPARCELAMENTO")
public class ItemCancelamentoParcelamento extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CancelamentoParcelamento cancelamentoParcelamento;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal valor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CancelamentoParcelamento getCancelamentoParcelamento() {
        return cancelamentoParcelamento;
    }

    public void setCancelamentoParcelamento(CancelamentoParcelamento cancelamentoParcelamento) {
        this.cancelamentoParcelamento = cancelamentoParcelamento;
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
