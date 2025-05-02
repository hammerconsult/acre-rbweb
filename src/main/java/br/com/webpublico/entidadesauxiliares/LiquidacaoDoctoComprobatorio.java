package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 09/12/2014.
 */
public class LiquidacaoDoctoComprobatorio {
    private String documento;
    private String numero;
    private Date dataDocumento;
    private BigDecimal valor;

    public LiquidacaoDoctoComprobatorio() {
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNumero() {

        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataDocumento() {
        return dataDocumento;
    }

    public void setDataDocumento(Date dataDocumento) {
        this.dataDocumento = dataDocumento;
    }
}
