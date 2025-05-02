package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
public class ItemRestituicao extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Restituicao restituicao;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    private String referencia;
    private BigDecimal valor;
    private BigDecimal valorPago;
    private BigDecimal diferenca;
    private BigDecimal diferencaAtualizada;
    private BigDecimal valorRestituido;

    @Transient
    private ResultadoParcela resultadoParcela;

    public ItemRestituicao() {
        valor = BigDecimal.ZERO;
        valorPago = BigDecimal.ZERO;
        diferenca = BigDecimal.ZERO;
        diferencaAtualizada = BigDecimal.ZERO;
        valorRestituido = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Restituicao getRestituicao() {
        return restituicao;
    }

    public void setRestituicao(Restituicao restituicao) {
        this.restituicao = restituicao;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public BigDecimal getDiferencaAtualizada() {
        return diferencaAtualizada;
    }

    public void setDiferencaAtualizada(BigDecimal diferencaAtualizada) {
        this.diferencaAtualizada = diferencaAtualizada;
    }

    public BigDecimal getValorRestituido() {
        return valorRestituido;
    }

    public void setValorRestituido(BigDecimal valorCompensado) {
        this.valorRestituido = valorCompensado;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }
}
