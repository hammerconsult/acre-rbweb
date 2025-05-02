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
public class ItemCompensacao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Compensacao compensacao;
    @ManyToOne
    private ParcelaValorDivida parcela;
    private String referencia;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private BigDecimal honorarios;
    private BigDecimal valorCompensado;
    private BigDecimal valorResidual;
    @ManyToOne
    private CalculoCompensacao calculoCompensacao;
    @Transient
    private ResultadoParcela resultadoParcela;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemCompensacao() {
        this.valorResidual = BigDecimal.ZERO;
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

    public BigDecimal getValorCompensado() {
        return valorCompensado != null ? valorCompensado : BigDecimal.ZERO;
    }

    public void setValorCompensado(BigDecimal valorCompensado) {
        this.valorCompensado = valorCompensado;
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getValorResidual() {
        return valorResidual != null ? valorResidual : BigDecimal.ZERO;
    }

    public void setValorResidual(BigDecimal valorResidual) {
        this.valorResidual = valorResidual;
    }

    public CalculoCompensacao getCalculoCompensacao() {
        return calculoCompensacao;
    }

    public void setCalculoCompensacao(CalculoCompensacao calculoCompensacao) {
        this.calculoCompensacao = calculoCompensacao;
    }
}
