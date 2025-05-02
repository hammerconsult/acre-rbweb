package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;

/**
 * Created by William on 12/06/2018.
 */
public class RelatorioServidoresCargosVerbasItemVO {
    private String verba;
    private BigDecimal valor;

    public String getVerba() {
        return verba;
    }

    public void setVerba(String verba) {
        this.verba = verba;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
