/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author fabio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
public enum IncidenciaMultaFiscalizacao {

    MULTA_ACESSORIA("Multa Acessória"),
    MULTA_PROCESSO_FISCAL("Multa do Processo Fiscal"),
    MULTA_PUNITIVA("Multa Punitiva por não declaração de ISSQN");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private IncidenciaMultaFiscalizacao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
