/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Felipe Marinzeck
 */
public enum CategoriaDeclaracaoPrestacaoContas implements EnumComDescricao {
    SIPREV("SIPREV"),
    SEFIP("SEFIP"),
    DIRF("DIRF"),
    CAGED("CAGED"),
    RAIS("RAIS"),
    SICAP("SICAP"),
    FOLHA_PAGAMENTO("Folha de Pagamento"),
    RELATORIOS("Relatórios"),
    BBPREV("BBPrev");
    private String descricao;

    private CategoriaDeclaracaoPrestacaoContas(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
