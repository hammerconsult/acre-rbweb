/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Claudio
 */
public enum IndicadorRetificacao implements EnumComDescricao {
    NAO_RETIFICA("2", "2 - A declaração não é retificação (é primeira entrega)"),
    RETIFICA("1", "1 - Retifica os estabelecimentos entregues anteriormente");

    private String codigo;
    private String descricao;

    private IndicadorRetificacao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}
