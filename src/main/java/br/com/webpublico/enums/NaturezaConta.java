/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Renato Romanini
 */
public enum NaturezaConta implements EnumComDescricao {
    ATIVO("Ativo", "11"),
    PASSIVO("Passivo", "22"),
    PATRIMONIO_PUBLICO("Patrimônio Líquido", "23"),
    VARIACAO_PATRIMONIAL_DIMINUTIVA("Variação Patrimonial Diminutiva", "31"),
    VARIACAO_PATRIMONIAL_AUMENTATIVA("Variação Patrimonial Aumentativa", "41"),
    CONTROLES_APROVACAO_PLANEJAMENTO_ORCAMENTO("Controles da Aprovação do Planejamento e Orçamento", "51"),
    CONTROLES_EXECUCAO_PLANEJAMENTO_ORCAMENTO("Controles da Execução do Planejamento e Orçamento", "61"),
    CONTROLES_DEVEDORES("Controles Devedores", "71"),
    CONTROLES_CREDORES("Controles Credores", "81"),
    DEMAIS_CONTROLES("Demais Controles", "91");
    private String descricao;
    private String codigo;

    NaturezaConta(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    @Override
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
}
