package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VOValoresAtualizadosParcela implements Serializable {

    private Long idParcela;
    private BigDecimal valorImposto;
    private BigDecimal valorTaxa;
    private BigDecimal valorCorrecao;
    private BigDecimal valorJuros;
    private BigDecimal valorMulta;
    private BigDecimal valorHonorarios;
    private BigDecimal valorPago;
    private Date dataPrescricao;

    public VOValoresAtualizadosParcela(ResultadoParcela rp) {
        this.idParcela = rp.getIdParcela();
        this.valorImposto = rp.getValorImposto();
        this.valorTaxa = rp.getValorTaxa();
        this.valorCorrecao = rp.getValorCorrecao();
        this.valorJuros = rp.getValorJuros();
        this.valorMulta = rp.getValorMulta();
        this.valorHonorarios = rp.getValorHonorarios();
        this.valorPago = rp.getValorPago();
        this.dataPrescricao = rp.getPrescricao();
    }

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public BigDecimal getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa;
    }

    public void setValorTaxa(BigDecimal valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public BigDecimal getValorCorrecao() {
        return valorCorrecao;
    }

    public void setValorCorrecao(BigDecimal valorCorrecao) {
        this.valorCorrecao = valorCorrecao;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorHonorarios() {
        return valorHonorarios;
    }

    public void setValorHonorarios(BigDecimal valorHonorarios) {
        this.valorHonorarios = valorHonorarios;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public Date getDataPrescricao() {
        return dataPrescricao;
    }

    public void setDataPrescricao(Date dataPrescricao) {
        this.dataPrescricao = dataPrescricao;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VOValoresAtualizadosParcela)) {
            return false;
        }
        VOValoresAtualizadosParcela valor = (VOValoresAtualizadosParcela) obj;
        return Objects.equal(getIdParcela(), valor.getIdParcela());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdParcela());
    }
}
