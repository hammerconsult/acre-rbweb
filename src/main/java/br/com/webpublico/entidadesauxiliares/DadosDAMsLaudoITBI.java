package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DadosDAMsLaudoITBI implements Serializable {
    private String numeroDAM;
    private Date pagamento;
    private BigDecimal valorPago;

    public DadosDAMsLaudoITBI() {
        this.valorPago = BigDecimal.ZERO;
    }

    public String getNumeroDAM() {
        return numeroDAM;
    }

    public void setNumeroDAM(String numeroDAM) {
        this.numeroDAM = numeroDAM;
    }

    public Date getPagamento() {
        return pagamento;
    }

    public void setPagamento(Date pagamento) {
        this.pagamento = pagamento;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }
}
