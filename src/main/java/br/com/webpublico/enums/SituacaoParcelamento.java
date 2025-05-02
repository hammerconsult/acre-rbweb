/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Tributario")
public enum SituacaoParcelamento {

    ABERTO("Aberto", "01"),
    FINALIZADO("Finalizado", "01"),
    PAGO("Pago", "02"),
    ESTORNADO("Estornado", "03"),
    CANCELADO("Cancelado", "03"),
    REATIVADO("Reativado", "04"),
    DÍVIDA_ATIVA("Dívida Ativa", "05"),
    REFIS("Refis", "06"),
    PAGO_REFIS("Pago por Refis", "08");
    private String descricao;
    private String codigo;

    private SituacaoParcelamento(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isAtivo() {
        return FINALIZADO.equals(this) || REATIVADO.equals(this);
    }
}
