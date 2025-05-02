package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 04/10/13
 * Time: 14:23
 * To change this template use File | Settings | File Templates.
 */
public class RREOAnexo12ItemParticipacao {

    private String descricao;
    private BigDecimal valor;
    private Integer nivel;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
