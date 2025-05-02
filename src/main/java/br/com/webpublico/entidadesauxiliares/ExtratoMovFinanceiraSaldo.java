package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 03/03/2015.
 */
public class ExtratoMovFinanceiraSaldo {
    private BigDecimal saldo;

    public ExtratoMovFinanceiraSaldo() {
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
