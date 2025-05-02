/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author cheles
 */
@GrupoDiagrama(nome = "RBTrans")
public enum TipoCredencialRBTrans implements EnumComDescricao {

    TRAFEGO("Tráfego"),
    TRANSPORTE("Transporte");
    private String descricao;

    private TipoCredencialRBTrans(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
