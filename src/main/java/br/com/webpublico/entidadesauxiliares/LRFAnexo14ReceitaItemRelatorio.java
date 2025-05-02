package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 09/12/13
 * Time: 11:07
 * To change this template use File | Settings | File Templates.
 */
public class LRFAnexo14ReceitaItemRelatorio {

    private String descricaoReceita;
    private BigDecimal valorReceita;
    private Integer nivel;

    public LRFAnexo14ReceitaItemRelatorio() {
    }

    public String getDescricaoReceita() {
        return descricaoReceita;
    }

    public void setDescricaoReceita(String descricaoReceita) {
        this.descricaoReceita = descricaoReceita;
    }

    public BigDecimal getValorReceita() {
        return valorReceita;
    }

    public void setValorReceita(BigDecimal valorReceita) {
        this.valorReceita = valorReceita;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
