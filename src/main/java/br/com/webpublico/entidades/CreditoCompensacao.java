package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity

@GrupoDiagrama(nome = "Tributario")
@Audited
public class CreditoCompensacao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Compensacao compensacao;
    @ManyToOne
    private ParcelaValorDivida parcela;
    private String referencia;
    private BigDecimal valor;
    private BigDecimal valorPago;
    private BigDecimal diferenca;
    private BigDecimal diferencaAtualizada;
    private BigDecimal valorCompensado;
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

    public Compensacao getCompensacao() {
        return compensacao;
    }

    public void setCompensacao(Compensacao compensacao) {
        this.compensacao = compensacao;
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

    public BigDecimal getValor() {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValorPago() {
        return valorPago != null ? valorPago : BigDecimal.ZERO;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getDiferenca() {
        return diferenca != null ? diferenca : BigDecimal.ZERO;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public BigDecimal getDiferencaAtualizada() {
        return diferencaAtualizada != null ? diferencaAtualizada : BigDecimal.ZERO;
    }

    public void setDiferencaAtualizada(BigDecimal diferencaAtualizada) {
        this.diferencaAtualizada = diferencaAtualizada;
    }

    public BigDecimal getValorCompensado() {
        return valorCompensado != null ? valorCompensado : BigDecimal.ZERO;
    }

    public void setValorCompensado(BigDecimal valorCompensado) {
        this.valorCompensado = valorCompensado;
    }
}
