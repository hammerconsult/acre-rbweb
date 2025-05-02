package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Table(name = "PGCCTARIFABANCARIA")
public class PlanoGeralContasComentadoTarifaBancaria extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private TarifaBancaria tarifaBancaria;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    private BigDecimal valorUnitario;
    private BigDecimal valorPercentual;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TarifaBancaria getTarifaBancaria() {
        return tarifaBancaria;
    }

    public void setTarifaBancaria(TarifaBancaria tarifaBancaria) {
        this.tarifaBancaria = tarifaBancaria;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorPercentual() {
        return valorPercentual;
    }

    public void setValorPercentual(BigDecimal valorPercentual) {
        this.valorPercentual = valorPercentual;
    }
}
