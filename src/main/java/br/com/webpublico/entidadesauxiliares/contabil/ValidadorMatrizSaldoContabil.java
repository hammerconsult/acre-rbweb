package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.enums.TipoMatrizSaldoContabil;

import java.math.BigDecimal;

public class ValidadorMatrizSaldoContabil {

    private String codigo;
    private BigDecimal credito;
    private BigDecimal debito;
    private TipoMatrizSaldoContabil tipoValor;

    public ValidadorMatrizSaldoContabil() {
        credito = BigDecimal.ZERO;
        debito = BigDecimal.ZERO;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }

    public TipoMatrizSaldoContabil getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(TipoMatrizSaldoContabil tipoValor) {
        this.tipoValor = tipoValor;
    }

}
