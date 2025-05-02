package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoReprocessamentoHistorico implements EnumComDescricao {
    SALDO_SUB_CONTA("Saldo Conta Financeira"),
    SALDO_DIVIDA_PUBLICA("Saldo Dívida Pública"),
    SALDO_EXTRAORCAMENTARIO("Saldo Extraorçamentário"),
    SALDO_GRUPO_BEM_GRUPO_MATERIAL("Saldo Grupo Patrimonial/Materiais"),
    SALDO_CREDITO_RECEBER("Saldo de Crédito a Receber"),
    SALDO_DIVIDA_ATIVA("Saldo de Dívida Ativa"),
    SALDO_FONTE_DESPESA_ORC("Saldo Fonte Despesa Orçamentária");
    private String descricao;

    TipoReprocessamentoHistorico(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
