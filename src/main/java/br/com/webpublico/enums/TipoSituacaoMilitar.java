/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Jaime
 */
@GrupoDiagrama(nome = "CadastroUnico")
public enum TipoSituacaoMilitar {

    RESERVISTA("Reservista"),
    DISPENSADO("Dispensado"),
    ISENTO("Isento");
    private String descricao;

    private TipoSituacaoMilitar(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
