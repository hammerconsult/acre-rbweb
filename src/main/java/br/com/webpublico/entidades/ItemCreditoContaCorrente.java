package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity

@GrupoDiagrama(nome = "Tributario")
@Audited
public class ItemCreditoContaCorrente extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CreditoContaCorrente creditoContaCorrente;
    @ManyToOne
    private ParcelaValorDivida parcela;
    private String referencia;
    private BigDecimal diferencaContaCorrente;
    @Transient
    private ResultadoParcela resultadoParcela;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public CreditoContaCorrente getCreditoContaCorrente() {
        return creditoContaCorrente;
    }

    public void setCreditoContaCorrente(CreditoContaCorrente creditoContaCorrente) {
        this.creditoContaCorrente = creditoContaCorrente;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public BigDecimal getDiferencaContaCorrente() {
        return diferencaContaCorrente != null ? diferencaContaCorrente : BigDecimal.ZERO;
    }

    public void setDiferencaContaCorrente(BigDecimal diferencaContaCorrente) {
        this.diferencaContaCorrente = diferencaContaCorrente;
    }
}
