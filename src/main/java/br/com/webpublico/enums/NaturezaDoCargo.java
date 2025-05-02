/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Licitacao")
public enum NaturezaDoCargo {

    AGENTE_POLITICO("Agente Político"),
    CARGO_EM_COMISSAO("Cargo em Comissão"),
    EMPREGADO_TEMPORARIO("Empregado Temporário"),
    SERVIDOR_EFETIVO("Servidor Efetivo"),
    OUTRA("Outra");
    private String descricao;

    private NaturezaDoCargo(String descricao) {
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
