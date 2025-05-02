package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

public class NotaExecucaoOrcamentariaPagamento {
    private String numero;
    private Date dataPag;
    private String historico;
    private BigDecimal valor;

    public NotaExecucaoOrcamentariaPagamento() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataPag() {
        return dataPag;
    }

    public void setDataPag(Date dataPag) {
        this.dataPag = dataPag;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
