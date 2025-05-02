package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

public class NotaExecucaoOrcamentariaRetencao {
    private String numero;
    private Date dataRet;
    private String contaExtra;
    private BigDecimal valor;

    public NotaExecucaoOrcamentariaRetencao() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataRet() {
        return dataRet;
    }

    public void setDataRet(Date dataRet) {
        this.dataRet = dataRet;
    }

    public String getContaExtra() {
        return contaExtra;
    }

    public void setContaExtra(String contaExtra) {
        this.contaExtra = contaExtra;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
