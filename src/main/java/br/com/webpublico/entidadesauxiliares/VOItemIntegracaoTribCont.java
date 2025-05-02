package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

public class VOItemIntegracaoTribCont {
    private Date dataPagamento;
    private BigDecimal valor;
    private Long idConta;
    private String conta;
    private Long idLoteBaixa;
    private String codigoLoteBaixa;

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getIdConta() {
        return idConta;
    }

    public void setIdConta(Long idConta) {
        this.idConta = idConta;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public Long getIdLoteBaixa() {
        return idLoteBaixa;
    }

    public void setIdLoteBaixa(Long idLoteBaixa) {
        this.idLoteBaixa = idLoteBaixa;
    }

    public String getCodigoLoteBaixa() {
        return codigoLoteBaixa;
    }

    public void setCodigoLoteBaixa(String codigoLoteBaixa) {
        this.codigoLoteBaixa = codigoLoteBaixa;
    }
}
