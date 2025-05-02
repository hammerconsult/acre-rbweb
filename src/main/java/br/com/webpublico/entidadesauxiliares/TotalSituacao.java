package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: scarpini
 * Date: 28/01/14
 * Time: 15:13
 * To change this template use File | Settings | File Templates.
 */
public class TotalSituacao {

    private String situacao;
    private BigDecimal valor;

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
