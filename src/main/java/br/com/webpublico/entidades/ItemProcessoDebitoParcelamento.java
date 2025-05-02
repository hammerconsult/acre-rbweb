package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by William on 19/04/2017.
 */
@Entity
@Audited
@Table(name = "ITEMPROCESSODEBITOPARC")
public class ItemProcessoDebitoParcelamento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private BigDecimal honorarios;
    @ManyToOne
    private ItemProcessoParcelamento itemProcessoParcelamento;
    @ManyToOne
    private ItemProcessoDebito itemProcessoDebito;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getImposto() {
        if (imposto != null) {
            return imposto;
        }
        return BigDecimal.ZERO;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        if (taxa != null) {
            return taxa;
        }
        return BigDecimal.ZERO;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getJuros() {
        if (juros != null) {
            return juros;
        }
        return BigDecimal.ZERO;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        if (multa != null) {
            return multa;
        }
        return BigDecimal.ZERO;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        if (correcao != null) {
            return correcao;
        }
        return BigDecimal.ZERO;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public ItemProcessoParcelamento getItemProcessoParcelamento() {
        return itemProcessoParcelamento;
    }

    public void setItemProcessoParcelamento(ItemProcessoParcelamento itemProcessoParcelamento) {
        this.itemProcessoParcelamento = itemProcessoParcelamento;
    }

    public ItemProcessoDebito getItemProcessoDebito() {
        return itemProcessoDebito;
    }

    public void setItemProcessoDebito(ItemProcessoDebito itemProcessoDebito) {
        this.itemProcessoDebito = itemProcessoDebito;
    }

    public BigDecimal getHonorarios() {
        if (honorarios != null) {
            return honorarios;
        }
        return BigDecimal.ZERO;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }
}
