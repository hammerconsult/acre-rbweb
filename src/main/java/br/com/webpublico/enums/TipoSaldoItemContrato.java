package br.com.webpublico.enums;

import com.google.common.collect.Lists;

import java.util.List;

public enum TipoSaldoItemContrato {
    ORIGINAL("Original"),
    RESCISAO("Rescisão"),
    DESPESA_NORMAL("Despesa - Normal"),
    DESPESA_ESTORNO("Despesa - Estorno"),
    ALTERACAO_CONTRATUAL_ACRESCIMO("Alteração Contratual - Acréscimo"),
    ALTERACAO_CONTRATUAL_SUPRESSAO("Alteração Contratual - Supressão");
    private String descricao;

    TipoSaldoItemContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<TipoSaldoItemContrato> getTiposAcrescimo() {
        List<TipoSaldoItemContrato> list = Lists.newArrayList();
        list.add(ORIGINAL);
        list.add(DESPESA_ESTORNO);
        list.add(ALTERACAO_CONTRATUAL_ACRESCIMO);
        return list;
    }

    public boolean isTipoMovimentoAcrescimo() {
        return getTiposAcrescimo().contains(this);
    }

    public boolean isExecucaoNormal() {
        return DESPESA_NORMAL.equals(this);
    }

    public boolean isExecucaoEstorno() {
        return DESPESA_ESTORNO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
