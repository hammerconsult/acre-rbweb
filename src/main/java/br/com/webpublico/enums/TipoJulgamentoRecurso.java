/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author lucas
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoJulgamentoRecurso {
    INDEFERIDO("Indeferido"),
    DEFERIDO("Deferido");

    private String descricao;

    private TipoJulgamentoRecurso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
