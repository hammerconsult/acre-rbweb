package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Audited
public class ItemCalculoLicenciamentoAmbiental extends ItemCalculo {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private CalculoLicenciamentoAmbiental calculoLicenciamentoAmbiental;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal valorUFM;
    private BigDecimal valor;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoLicenciamentoAmbiental getCalculoLicenciamentoAmbiental() {
        return calculoLicenciamentoAmbiental;
    }

    public void setCalculoLicenciamentoAmbiental(CalculoLicenciamentoAmbiental calculoLicenciamentoAmbiental) {
        this.calculoLicenciamentoAmbiental = calculoLicenciamentoAmbiental;
    }

    @Override
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

    @Override
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
