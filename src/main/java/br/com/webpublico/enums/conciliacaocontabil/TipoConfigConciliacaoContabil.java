package br.com.webpublico.enums.conciliacaocontabil;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoConfigConciliacaoContabil implements EnumComDescricao {

    DISPONIBILIDADE_DE_CAIXA("Disponibilidade de Caixa"),
    CREDITOS_A_RECEBER("Créditos a Receber"),
    DIVIDA_ATIVA("Dívida Ativa"),
    ESTOQUE("Estoque"),
    BEM_MOVEL("Bem Móvel"),
    BEM_IMOVEL("Bem Imóvel"),
    GRUPO_BEM_MOVEL("Grupo Patrimonial Móvel"),

    OBRIGACAO_A_PAGAR("Obrigação a Pagar"),
    DIVIDA_PUBLICA_E_PRECATORIO("Dívida Pública e Precatório"),
    PASSIVO_ATUARIAL("Passivo Atuarial"),

    RECEITA_PATRIMONIAL_X_RECEITA_ORCAMENTARIA("Receita Patrimonial x Receita Orçamentária"),
    RECEITA_DE_OPERACAO_DE_CREDITO("Receita de Operação de Crédito"),
    RECEITA_DE_ALIENACAO_DE_BEM("Receita de Alienação de Bem"),

    DESPESA_PATRIMONIAL_X_DESPESA_ORCAMENTARIA("Despesa Patrimonial x Despesa Orçamentária"),
    LIQUIDACAO_DE_DESPESA_E_DE_RESTOS_A_PAGAR("Liquidação de Despesa e de Restos a Pagar"),
    MATERIAL_DE_CONSUMO("Material de Consumo"),
    MATERIAL_PERMANENTE("Material Permanente"),
    OBRAS_E_INSTALACOES_E_AQUISICAO_DE_IMOVEIS("Obras e Instalações e Aquisição de Imóveis");

    private String descricao;

    TipoConfigConciliacaoContabil(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isDividaPublicaEPrecatorio() {
        return TipoConfigConciliacaoContabil.DIVIDA_PUBLICA_E_PRECATORIO.equals(this);
    }
}
