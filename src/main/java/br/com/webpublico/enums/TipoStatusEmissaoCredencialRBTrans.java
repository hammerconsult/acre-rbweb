/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author cheles
 */
@GrupoDiagrama(nome = "RBTrans")
public enum TipoStatusEmissaoCredencialRBTrans {
    EMITIDA("Emitida"),
    NAO_EMITIDA("Não Emitida"),
    TODAS("Todas");
    private String descricao;

    private TipoStatusEmissaoCredencialRBTrans(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
